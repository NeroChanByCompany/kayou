package com.nut.driver.app.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.dozermapper.core.Mapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.nut.common.base.BaseForm;
import com.nut.common.exception.ExceptionUtil;
import com.nut.common.push.PushStaticLocarVal;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.PagingInfo;
import com.nut.driver.app.dao.UserMessageRecordDao;
import com.nut.driver.app.domain.MessagePage;
import com.nut.driver.app.dto.QueryMessageDetailListDto;
import com.nut.driver.app.dto.QueryMessageListDto;
import com.nut.driver.app.entity.UserMessageRecordEntity;
import com.nut.driver.app.form.QueryMessageDetailListForm;
import com.nut.driver.app.form.QueryMessageForm;
import com.nut.driver.app.pojo.QueryMessageCountPojo;
import com.nut.driver.app.pojo.QueryMessageListPojo;
import com.nut.driver.app.service.MessageService;
import com.nut.driver.app.service.SlaveMessageService;
import com.nut.driver.common.constants.CommonConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.service.impl
 * @Author: yzl
 * @CreateTime: 2021-06-27 11:23
 * @Version: 1.0
 */
@Service
@Slf4j
public class MessageServiceImpl extends DriverBaseService implements MessageService {

    @Autowired
    private UserMessageRecordDao userMessageRecordDao;

    @Autowired
    private Mapper convert;

    @Autowired
    private SlaveMessageService slaveMessageServicel;

    /**
     * ??????????????????
     */
    public List queryMessageList(QueryMessageForm form) {
        log.info("[MessageAndAlarmNoticeService][queryMessageList]start!");
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        Map<String, Object> param = new HashMap<>(5);
        encapsulationParameter(param, form, true);
        param.put("messageTypeList", encapsulationMessageTypeList());
        // ???????????????????????????????????????????????????????????????
        List<QueryMessageListPojo> messageListAll = userMessageRecordDao.queryMessageList(param);

        // ????????????????????????????????????????????????????????????
        Map<String, QueryMessageListPojo> messageType = new HashMap<>(messageListAll.size());
        for (QueryMessageListPojo item : messageListAll) {
            if (messageType.containsKey(item.getTypeName())) {
                if (messageType.get(item.getTypeName()).getSendTime() < item.getSendTime()) {
                    messageType.put(item.getTypeName(), item);
                }
            } else {
                messageType.put(item.getTypeName(), item);
            }
        }
        List<QueryMessageListPojo> messageList = new ArrayList<>(messageType.entrySet().size());
        for (String key : messageType.keySet()) {
            messageList.add(messageType.get(key));
        }
        // ?????????????????????????????????????????????
        List<QueryMessageCountPojo> countList = userMessageRecordDao.queryMessageCount(param);
        Map<String, Integer> countMap = countList.stream().collect(
                Collectors.toMap(entity -> String.valueOf(entity.getMessageType()), QueryMessageCountPojo::getCount));
        countMap = MapUtils.isNotEmpty(countMap) ? countMap : new HashMap<>(0);
        List<QueryMessageListDto> dtoList = new ArrayList<>();
        for (QueryMessageListPojo pojo : messageList) {
            dtoList.add(convertPojoToDto(pojo, countMap.get(String.valueOf(pojo.getMessageType()))));
        }
       List<QueryMessageListDto> listDtos =  slaveMessageServicel.queryMessageList(form);
        if ( !listDtos.isEmpty()){
            dtoList.addAll(listDtos);
        }
        log.info("queryMessageList end return:{}", dtoList);
        return dtoList;
    }

    /**
     * ??????????????????????????????????????????????????????
     */
    @Override
    public PagingInfo<QueryMessageDetailListDto> queryMessageDetailList(QueryMessageDetailListForm form) {
        MessagePage<QueryMessageDetailListDto> page = new <QueryMessageDetailListDto>MessagePage();
        //???????????????????????????????????????????????????
        if (CommonConstants.FAULT_MESSAGE.equals(form.getMessageType()) || CommonConstants.START_STOP_MESSAGE.equals(form.getMessageType())){
          page = getAfterMessageDetailList(form);
        }else{
            //??????????????????????????????????????????
            Page<QueryMessageDetailListDto> nowPage = getMessageDetailList(form);
            page.setTotal(nowPage.getTotal());
            page.setPages(nowPage.getPages());
            page.setResult(nowPage.getResult());
        }
        PagingInfo<QueryMessageDetailListDto> result = convertPagingToPage(page);
        log.info("????????????????????????end return:{}", result);
        return result;
    }

    //@Async("doSomethingExecutor")
    public MessagePage<QueryMessageDetailListDto> getAfterMessageDetailList(QueryMessageDetailListForm form) {
        MessagePage<QueryMessageDetailListDto> afterPage = slaveMessageServicel.queryMessageDetailList(form);
        return afterPage;
    }

    //@Async("doSomethingExecutor")
    public Page<QueryMessageDetailListDto> getMessageDetailList(QueryMessageDetailListForm form) {
        Map<String, Object> param = new HashMap<>(5);
        encapsulationParameter(param, form, true);
        param.put("messageType", form.getMessageType());
        getPage(form);
        Page<QueryMessageDetailListDto> page = userMessageRecordDao.queryMessageDetailList(param);
        return page;
    }


    @Override
    public void updateUnread(QueryMessageDetailListForm form) {
        Assert.notNull(form.getMessageType(), "??????????????????");

        if (CommonConstants.FAULT_MESSAGE.equals(form.getMessageType()) || CommonConstants.START_STOP_MESSAGE.equals(form.getMessageType())){
            slaveMessageServicel.update(form);
        }else{
            int count = userMessageRecordDao.update(
                    new UserMessageRecordEntity().setReadFlag(2),
                    new QueryWrapper<UserMessageRecordEntity>()
                            .eq("message_type", form.getMessageType()).eq("receiver_id",form.getUserId()));
            log.info("?????????????????????????????????{}", count);
        }

    }


    /**
     * ??????????????????
     */
    private void encapsulationParameter(Map<String, Object> param, BaseForm form, boolean flag) {
        if (null != param) {
            param.put("receiveAppType", Integer.parseInt(form.getAppType()));
            if (flag) {
                param.put("pushShowType", Integer.parseInt(PushStaticLocarVal.PUSH_SHOW_TYPE_USER));
                param.put("receiverId", form.getUserId());
                param.put("sendTime", System.currentTimeMillis());
            }
        }
    }

    /**
     * ??????????????????????????????
     */
    private List<Integer> encapsulationMessageTypeList() {
        return Arrays.asList(Integer.parseInt(PushStaticLocarVal.MESSAGE_TYPE_FLEET),
                Integer.parseInt(PushStaticLocarVal.MESSAGE_TYPE_SERVICE),
                Integer.parseInt(PushStaticLocarVal.MESSAGE_TYPE_MAINTAIN),
                Integer.parseInt(PushStaticLocarVal.MESSAGE_TYPE_SYSTEM),
                Integer.parseInt(PushStaticLocarVal.MESSAGE_TYPE_FORUM),
                Integer.parseInt(PushStaticLocarVal.MESSAGE_TYPE_SIM_FLOW));
    }

    /**
     * ??????????????????pojo???dto
     */
    private QueryMessageListDto convertPojoToDto(QueryMessageListPojo pojo, Integer count) {
        QueryMessageListDto dto = new QueryMessageListDto();
        dto.setTypeName(pojo.getTypeName());
        dto.setContent(pojo.getContent());
        dto.setSendTime(pojo.getSendTime());
        dto.setCount(null != count ? count : 0);
        dto.setMessageType(pojo.getMessageType());
        return dto;
    }

}
