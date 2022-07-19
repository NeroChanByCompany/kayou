package com.nut.driver.app.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.nut.common.base.BaseForm;
import com.nut.common.exception.ExceptionUtil;
import com.nut.common.push.PushStaticLocarVal;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.PagingInfo;
import com.nut.driver.app.client.ToolsClient;
import com.nut.driver.app.dao.CarDao;
import com.nut.driver.app.dao.CarMessageRecordDao;
import com.nut.driver.app.domain.UserMessageReceiveSet;
import com.nut.driver.app.domain.UserMessageRecord;
import com.nut.driver.app.dto.QueryAlarmNoticeDetailListDto;
import com.nut.driver.app.dto.QueryAlarmNoticeListAttributeDto;
import com.nut.driver.app.dto.QueryAlarmNoticeListDto;
import com.nut.driver.app.dto.QueryMessageAndAlarmNoticeReceiveSetDto;
import com.nut.driver.app.form.*;
import com.nut.driver.app.pojo.CarRolePojo;
import com.nut.driver.app.pojo.QueryAlarmNoticeCountPojo;
import com.nut.driver.app.pojo.QueryAlarmNoticeListPojo;
import com.nut.driver.app.service.NoticeService;
import com.nut.driver.app.service.UserService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 报警服务
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.service.impl
 * @Author: yzl
 * @CreateTime: 2021-06-30 10:16
 * @Version: 1.0
 */
@Service
@Slf4j
public class NoticeServiceImpl  extends DriverBaseService implements NoticeService {

    @Autowired
    private ToolsClient toolsClient;

    @Autowired
    private CarDao carDao;

    @Autowired
    private CarMessageRecordDao carMessageRecordDao;

    @Autowired
    private UserService userService;

    /**
    * @Description：报警消息列表
    * @author YZL
    * @data 2021/6/30 10:33
    */
    @Override
    @SneakyThrows
    public QueryAlarmNoticeListAttributeDto queryAlarmNoticeList(QueryNoticeListForm form) {
        // 查询用户相关车辆信息
        List<CarRolePojo> carList = carDao.selectUserRelatedCar(form.getAutoIncreaseId());
        // 用户没有相关车辆直接返回
        if (CollectionUtils.isEmpty(carList)) {
            return null ;
        }
        // 用户相关的所有车辆ID
        List<String> carIdList = carList.stream().map(CarRolePojo::getCarId).distinct().collect(Collectors.toList());
        // 用户没有相关车辆直接返回
        if (CollectionUtils.isEmpty(carIdList)) {
            return null;
        }
        Map<String, Object> param = new HashMap<>(3);
        encapsulationParameter(param, form, false);
        param.put("carIdList", carIdList);
        // 查询车辆最近的一条报警通知
        getPage(form);
        Page<QueryAlarmNoticeListPojo> page = carMessageRecordDao.queryAlarmNoticeList(param);
        if (null == page || CollectionUtils.isEmpty(page.getResult())) {
            return emptyDto();
        }
        param.put("userId", form.getAutoIncreaseId());
        // 查询报警通知总数和已读数量
        List<QueryAlarmNoticeCountPojo> pojoList = carMessageRecordDao.queryAlarmNoticeCount(param);
        QueryAlarmNoticeListAttributeDto dto = new QueryAlarmNoticeListAttributeDto();
        // 计算所有车辆的未读消息总数
        dto.setUnreadMesCount(pojoList.stream().mapToInt(entity -> entity.getTotalCount() - entity.getReadCount()).sum());
        Map<String, QueryAlarmNoticeCountPojo> countMap = pojoList.stream().collect(
                Collectors.toMap(QueryAlarmNoticeCountPojo::getCarId, entity -> entity));
        countMap = MapUtils.isNotEmpty(countMap) ? countMap : new HashMap<>(0);
        List<QueryAlarmNoticeListDto> dtoList = new ArrayList<>();
        for (QueryAlarmNoticeListPojo pojo : page.getResult()) {
            dtoList.add(convertPojoToDto(pojo, countMap.get(pojo.getCarId())));
        }
        dto.setTotal(page.getTotal());
        dto.setPage_total(page.getPages());
        dto.setList(dtoList);
        return dto;
    }

    /**
    * @Description：报警消息详情
    * @author YZL
    * @data 2021/6/30 10:58
    */
    @Override
    public HttpCommandResultWithData queryAlarmNoticeDetailList(QueryAlarmNoticeDetailListForm form) {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        // 查询用户相关车辆信息
        List<CarRolePojo> carList = carDao.selectUserRelatedCar(form.getAutoIncreaseId());
        // 用户没有相关车辆直接返回
        if (CollectionUtils.isEmpty(carList)) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(),"无权查看这辆车");
        }
        // 用户相关的所有车辆ID
        List<String> carIdList = carList.stream().map(CarRolePojo::getCarId).distinct().collect(Collectors.toList());
        // 用户没有相关车辆直接返回
        if (CollectionUtils.isEmpty(carIdList) || !carIdList.contains(form.getCarId())) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(),"无权查看这辆车");
        }
        Map<String, Object> param = new HashMap<>(2);
        encapsulationParameter(param, form, false);
        param.put("carId", form.getCarId());
        getPage(form);
        Page<QueryAlarmNoticeDetailListDto> page = carMessageRecordDao.queryAlarmNoticeDetailList(param);
        if (null != page && CollectionUtils.isNotEmpty(page.getResult())) {
            result.setData(convertPagingToPage(page));
        } else {
            PagingInfo<QueryAlarmNoticeDetailListDto> resultPagingInfo = new PagingInfo<>();
            resultPagingInfo.setTotal(0L);
            resultPagingInfo.setPage_total(0L);
            resultPagingInfo.setList(new ArrayList<>());
            result.setData(resultPagingInfo);
        }
        result.setResultCode(ECode.SUCCESS.code());
        return result;
    }

    @Override
    @SneakyThrows
    public HttpCommandResultWithData queryNewAlarmNoticeReceiveSet(QueryAlarmNoticeReceiveSetForm form) {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        UserMessageReceiveSet userMessageReceiveSet = new UserMessageReceiveSet();
        userMessageReceiveSet.setAppType(Integer.parseInt(form.getAppType()));
        userMessageReceiveSet.setUserId(form.getUserId());
        List<QueryMessageAndAlarmNoticeReceiveSetDto> list = carMessageRecordDao.queryMessageByUserIdAndAppType(form.getUserId(), form.getAppType());
        result.setData(CollectionUtils.isNotEmpty(list) ? list : new ArrayList<>());
        result.setResultCode(ECode.SUCCESS.code());
        return result;
    }

    @Override
    @SneakyThrows
    public HttpCommandResultWithData queryNewAlarmNoticeReceiveSetIos(QueryAlarmNoticeReceiveSetForm form) {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        UserMessageReceiveSet userMessageReceiveSet = new UserMessageReceiveSet();
        userMessageReceiveSet.setAppType(Integer.parseInt(form.getAppType()));
        userMessageReceiveSet.setUserId(form.getUserId());
        List<QueryMessageAndAlarmNoticeReceiveSetDto> list = carMessageRecordDao.queryMessageByUserIdAndAppType(form.getUserId(), form.getAppType());
        if (list == null){
            list = new ArrayList<>();
        }
        if (!isInclude(list, "5")){
            list.add(new QueryMessageAndAlarmNoticeReceiveSetDto(null,"5",1));
        }
        if (!isInclude(list, "6")){
            list.add(new QueryMessageAndAlarmNoticeReceiveSetDto(null,"6",1));
        }
        if (!isInclude(list, "7")){
            list.add(new QueryMessageAndAlarmNoticeReceiveSetDto(null,"7",1));
        }
        list.sort((o1, o2) -> new Integer(Integer.parseInt(o1.getMessageType())).compareTo(Integer.parseInt(o2.getMessageType())));
        result.setData(CollectionUtils.isNotEmpty(list) ? list : new ArrayList<>());
        result.setResultCode(ECode.SUCCESS.code());
        return result;
    }

    /**
    * @Description：消息和报警通知接收设置
    * @author YZL
    * @data 2021/6/30 11:18
    */
    @Override
    @SneakyThrows
    public HttpCommandResultWithData updateNewReceiveSet(UpdateReceiveSetForm form) {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        UserMessageReceiveSet userMessageReceiveSet = new UserMessageReceiveSet();
        userMessageReceiveSet.setAppType(Integer.parseInt(form.getAppType()));
        userMessageReceiveSet.setUserId(form.getUserId());
        userMessageReceiveSet.setMessageType(form.getMessageType());
        userMessageReceiveSet.setCreateTime(new Timestamp(System.currentTimeMillis()));
        if (form.getReceiveState() == 1) {
            carMessageRecordDao.deleteByMessageType(userMessageReceiveSet);
        } else {
            List<UserMessageReceiveSet> msgSetList = carMessageRecordDao.queryMessage(userMessageReceiveSet);
            if (msgSetList == null || CollectionUtils.isEmpty(msgSetList)) {
                carMessageRecordDao.insert(userMessageReceiveSet);
            }
        }
        result.setResultCode(ECode.SUCCESS.code());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HttpCommandResultWithData addUnreadMessage(AddUnreadMessageForm form) {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        //设置消息内容及初始条件
        UserMessageRecord userMessageRecord = this.userMessageRecordBeanCopy(form);
        //新增消息
        carMessageRecordDao.addUnreadMessage(userMessageRecord);
        //调用消息推送接口 推送消息
        this.jPushMes(userMessageRecord);
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage("新增成功");
        return result;
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

    /**
     * 没有车辆时的返回结果
     */
    private QueryAlarmNoticeListAttributeDto emptyDto() {
        QueryAlarmNoticeListAttributeDto dto = new QueryAlarmNoticeListAttributeDto();
        dto.setUnreadMesCount(0);
        dto.setTotal(0L);
        dto.setPage_total(0L);
        dto.setList(new ArrayList<>());
        return dto;
    }

    /**
     * 查询报警通知列表pojo转dto
     */
    private QueryAlarmNoticeListDto convertPojoToDto(
            QueryAlarmNoticeListPojo pojo, QueryAlarmNoticeCountPojo countPojo) {
        QueryAlarmNoticeListDto dto = new QueryAlarmNoticeListDto();
        dto.setCarId(pojo.getCarId());
        dto.setCarNumber(pojo.getCarNumber());
        dto.setChassisNum(pojo.getChassisNum());
        dto.setContent(pojo.getContent());
        dto.setSendTime(pojo.getSendTime());
        dto.setCount(null != countPojo ? countPojo.getTotalCount() - countPojo.getReadCount() : 0);
        return dto;
    }

    private boolean isInclude(List<QueryMessageAndAlarmNoticeReceiveSetDto> list, String messageType){
        for (QueryMessageAndAlarmNoticeReceiveSetDto m : list) {
            if (messageType.equals(m.getMessageType())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 设置消息内容及初始条件
     *
     * @param form 初始信息
     * @return
     */
    private UserMessageRecord userMessageRecordBeanCopy(AddUnreadMessageForm form) {
        UserMessageRecord userMessageRecord = UserMessageRecord.anUserMessageRecord()
                //设置消息id
                .withMessageId(UUID.randomUUID().toString().replaceAll("-", ""))
                //消息标题
                .withTitle(form.getTitle())
                //消息内容
                .withContent(form.getContent())
                //消息展示分类
                .withPushShowType(Integer.parseInt(PushStaticLocarVal.PUSH_SHOW_TYPE_USER))
                //跳转编码 目前是7 后续根据业务要求进行修改
                .withMessageCode(Integer.parseInt(PushStaticLocarVal.MESSAGE_CODE_YUN_YING))
                //用户是否可见，根据之前数据 目前统一设置为可见
                .withUserVisible(Integer.parseInt(PushStaticLocarVal.IS_USER_VISIBLE))
                //接收者id
                .withReceiverId(userService.selectUcIdById(form.getReceiverId()))
                //接收客户端  目前是车主版
                .withReceiveAppType(Integer.parseInt(PushStaticLocarVal.PUSH_RECEIVETYPE_OWNER))
                //发送者id bbs论坛发送id固定
                .withSenderId(form.getUserId())
                //发送时间
                .withSendTime(System.currentTimeMillis())
                //是否已读 初始未读
                .withReadFlag(PushStaticLocarVal.UNREAD)
                //附件字段 前段使用 目前不清楚作用 暂时不添加
                .withMessageExtra("")
                //大分类 目前算做通知消息，后续不清楚是否替换成论坛
                .withType(Integer.parseInt(PushStaticLocarVal.PUSH_TYPE_NOTICE))
                //小分类 通知 卡友圈消息
                .withStype(Integer.parseInt(PushStaticLocarVal.PUSH_TYPE_NOTICE_STYPE_FORUM))
                //接收状态 默认为接收
                .withReceiveState(Integer.parseInt(PushStaticLocarVal.RECEIVE_STATE))
                //消息分类
                .withMessageType(Integer.parseInt(PushStaticLocarVal.MESSAGE_TYPE_FORUM))
                //消息分类名称
                .withMessageTypeName(PushStaticLocarVal.MESSAGE_TYPE_FORUM_NAME)
                //创建时间
                .withCreateTime(new Date())
                //更新时间
                .withUpdateTime(new Date())
                .withDeviceType(form.getDeviceType()).build();
        if (form.getInvitationId() != null){
            userMessageRecord.setInvitationId(form.getInvitationId());
        }
        return userMessageRecord;
    }


    /**
     * 推送消息
     *
     * @param userMessageRecord
     */
    private void jPushMes(UserMessageRecord userMessageRecord) {
        JSONObject jsonObject = new JSONObject();
        //接收者id
        jsonObject.put("userId", userMessageRecord.getReceiverId());
        //消息标题
        jsonObject.put("title", userMessageRecord.getTitle());
        //消息主体内容
        jsonObject.put("content", userMessageRecord.getContent());
        jsonObject.put("deviceType",userMessageRecord.getDeviceType());
        /*IxinPushMesCommand command = new IxinPushMesCommand();
        // 设置消息类型
        command.setType(IxinPushStaticLocarVal.PUSH_TYPE_NOTICE);
        // true：通过 - false：不通过
        command.setStype(IxinPushStaticLocarVal.PUSH_TYPE_NOTICE_STYPE_FORUM);
        command.setContent(userMessageRecord.getContent());
        command.setTitle(userMessageRecord.getTitle());
        // 发送者用户id
        command.setSenderId(userMessageRecord.getSenderId());
        // 接收者用户id
        command.setReceiverId(userMessageRecord.getReceiverId());
        // 页面跳转code-客户app：预约详情页面
        command.setMessageCode(IxinPushStaticLocarVal.MESSAGE_CODE_YUN_YING);
        // 推送类型
        command.setPushType(IxinPushStaticLocarVal.PUSH_TYPE_MSG_SPECIFIED);
        // 消息展示分类
        command.setPushShowType(IxinPushStaticLocarVal.PUSH_SHOW_TYPE_USER);
        // 推送客户端
        command.setAppType(IxinPushStaticLocarVal.PUSH_RECEIVETYPE_OWNER);
        // 扩展信息
        command.setMessageExtra("");*/
        toolsClient.JPushMes(jsonObject);
    }
}
