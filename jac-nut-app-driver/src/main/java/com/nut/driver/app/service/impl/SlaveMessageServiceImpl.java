package com.nut.driver.app.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.dynamic.datasource.annotation.Slave;
import com.github.pagehelper.Page;
import com.nut.common.base.BaseForm;
import com.nut.common.push.PushStaticLocarVal;
import com.nut.common.result.PagingInfo;
import com.nut.driver.app.dao.SlaveMessageDao;
import com.nut.driver.app.domain.MessagePage;
import com.nut.driver.app.dto.QueryMessageDetailListDto;
import com.nut.driver.app.dto.QueryMessageListDto;
import com.nut.driver.app.enums.CountEnum;
import com.nut.driver.app.form.QueryMessageDetailListForm;
import com.nut.driver.app.form.QueryMessageForm;
import com.nut.driver.app.service.SlaveMessageService;
import com.nut.driver.common.constants.CommonConstants;
import com.nut.driver.common.utils.PageUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.util.LimitedInputStream;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author liuBing
 * @Classname SlaveMessageServiceImpl
 * @Description TODO
 * @Date 2021/8/5 16:05
 */
@Slf4j
@Slave
//@DS(CommonConstants.DB_MESSAGE)
@Service
public class SlaveMessageServiceImpl implements SlaveMessageService {

    @Autowired
    SlaveMessageDao slaveMessageDao;

    @Autowired
    PageUtil pageUtil;

    @Override
    public MessagePage<QueryMessageDetailListDto> queryMessageDetailList(QueryMessageDetailListForm form) {
        MessagePage<QueryMessageDetailListDto> page = new MessagePage<>();
        String nowTable = CommonConstants.TABLE_PREFIX + DateUtil.format(new Date(), "yyyyMM");
        String beforeTable = CommonConstants.TABLE_PREFIX + DateUtil.format(DateUtil.offsetMonth(new Date(), -1), "yyyyMM");
        //封装参数条件
        Map<String, Object> param = new HashMap<>(7);
        encapsulationParameter(param, form, true);
        param.put("messageType", form.getMessageType());
        //配置动态表名
        param.put("nowTable", nowTable);
        param.put("beforeTable", beforeTable);
        log.info("查询启停消息start param:{}", param);
        //查询出当前月所有数据
        List<QueryMessageDetailListDto> nowList = slaveMessageDao.queryNowMessageDetailList(param);
        //查询出上个月所有数据
        List<QueryMessageDetailListDto> beforeList = slaveMessageDao.queryBeforeMessageDetailList(param);
        if (!nowList.isEmpty() && !beforeList.isEmpty()) {
            nowList.addAll(beforeList);
            PagingInfo<QueryMessageDetailListDto> paging = pageUtil.paging(nowList, form.getPage_number(), form.getPage_size());
            page.setTotal(paging.getTotal());
            page.setResult(paging.getList());
            page.setPages(new Long(paging.getPage_total()).intValue());

        } else if (!nowList.isEmpty()) {
            PagingInfo<QueryMessageDetailListDto> paging = pageUtil.paging(nowList, form.getPage_number(), form.getPage_size());
            page.setTotal(paging.getTotal());
            page.setResult(paging.getList());
            page.setPages(new Long(paging.getPage_total()).intValue());
        } else {
            PagingInfo<QueryMessageDetailListDto> paging = pageUtil.paging(beforeList, form.getPage_number(), form.getPage_size());
            page.setTotal(paging.getTotal());
            page.setResult(paging.getList());
            page.setPages(new Long(paging.getPage_total()).intValue());
        }
        log.info("查询启停消息end return:{}", page);
        return page;
    }

    @Override
    @SneakyThrows
    public List<QueryMessageListDto> queryMessageList(QueryMessageForm form) {
        List<QueryMessageListDto> list = new ArrayList<>();
        String nowTable = CommonConstants.TABLE_PREFIX + DateUtil.format(new Date(), "yyyyMM");
        String beforeTable = CommonConstants.TABLE_PREFIX + DateUtil.format(DateUtil.offsetMonth(new Date(), -1), "yyyyMM");
        log.info("nowTable:{},beforeTable:{}", nowTable, beforeTable);
        //封装参数条件
        Map<String, Object> param = new HashMap<>(8);
        encapsulationParameter(param, form, true);
        param.put("messageType", CommonConstants.FAULT_MESSAGE);
        param.put("nowTable", nowTable);
        param.put("beforeTable", beforeTable);
        //故障消息
        this.faultMessage(param, list);
        //启停消息
        this.startStopMessage(param, list);
        return list;
    }

    /**
     * 启停消息
     *
     * @param param
     * @param list
     */
    private void startStopMessage(Map<String, Object> param, List<QueryMessageListDto> list) {
        param.put("messageType", CommonConstants.START_STOP_MESSAGE);
        //当月
        QueryMessageListDto endNowMessageListDto = slaveMessageDao.queryNowMessageList(param);
        //上月
        QueryMessageListDto endBeforeMessageListDto = slaveMessageDao.queryAfterMessageList(param);
        if (endNowMessageListDto != null && endBeforeMessageListDto != null) {
            //消息未读数
            endNowMessageListDto.setCount(getCount(CountEnum.ALL.getCode(),param));
            list.add(endNowMessageListDto);
        } else if (endNowMessageListDto != null) {
            endNowMessageListDto.setCount(getCount(CountEnum.NOW.getCode(),param));
            list.add(endNowMessageListDto);
        } else if (endBeforeMessageListDto != null) {
            endBeforeMessageListDto.setCount(getCount(CountEnum.BEFORE.getCode(),param));
            list.add(endBeforeMessageListDto);
        }
    }

    /**
     * 根据状态查询不同月份数据 0 所有 1 上月 2当前月
     *
     * @param type
     * @return
     */
    public Integer getCount(Integer type, Map<String, Object> param) {
        Integer count = 0;

        switch (CountEnum.getCodeByType(type)) {
            case ALL:
                Integer nowCount = slaveMessageDao.queryNowCount(param);
                Integer beforeCount = slaveMessageDao.queryBeforeCount(param);
                if (nowCount == null) {
                    nowCount = 0;
                }
                if (beforeCount == null) {
                    beforeCount = 0;
                }
                count = nowCount + beforeCount;
                break;
            case NOW:
                Integer queryNowCount = slaveMessageDao.queryNowCount(param);
                if (queryNowCount == null) {
                    queryNowCount = 0;
                }
                count = queryNowCount;
                break;
            case BEFORE:
                Integer queryBeforeCount = slaveMessageDao.queryBeforeCount(param);
                if (queryBeforeCount == null) {
                    queryBeforeCount = 0;
                }
                count = queryBeforeCount;
                break;
            default:
                count = 0;
                break;
        }
        return count;
    }

    /**
     * 故障消息
     *
     * @param param
     * @param list
     */
    private void faultMessage(Map<String, Object> param, List<QueryMessageListDto> list) {
        //当月
        QueryMessageListDto queryNowMessageListDto = slaveMessageDao.queryNowMessageList(param);
        log.info("当月数据：{}", queryNowMessageListDto);
        //上月
        QueryMessageListDto queryBeforeMessageListDto = slaveMessageDao.queryAfterMessageList(param);
        log.info("上月数据：{}", queryBeforeMessageListDto);
        if (queryNowMessageListDto != null && queryBeforeMessageListDto != null) {
            //消息未读数
            queryNowMessageListDto.setCount(getCount(CountEnum.ALL.getCode(),param));
            list.add(queryNowMessageListDto);
        } else if (queryNowMessageListDto != null) {
            queryNowMessageListDto.setCount(getCount(CountEnum.NOW.getCode(),param));
            list.add(queryNowMessageListDto);
        } else if (queryBeforeMessageListDto != null) {
            queryBeforeMessageListDto.setCount(getCount(CountEnum.BEFORE.getCode(),param));
            list.add(queryBeforeMessageListDto);
        }
    }

    @Override
    @DSTransactional()
    public void update(QueryMessageDetailListForm form) {
        List<String> list = Arrays.asList(
                CommonConstants.TABLE_PREFIX + DateUtil.format(new Date(), "yyyyMM"),
                CommonConstants.TABLE_PREFIX + DateUtil.format(DateUtil.offsetMonth(new Date(), -1), "yyyyMM")
        );
        for (String table : list) {
            slaveMessageDao.update(table, form.getMessageType(), form.getUserId());
        }
    }

    /**
     * 封装查询条件
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

}
