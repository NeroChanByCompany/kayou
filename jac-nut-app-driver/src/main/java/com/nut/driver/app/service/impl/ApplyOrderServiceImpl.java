package com.nut.driver.app.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.nut.common.utils.*;
import com.nut.driver.app.client.LocationServiceClient;
import com.nut.driver.app.dao.*;
import com.nut.driver.app.domain.RelationWorkOrderUser;
import com.nut.driver.app.dto.CarLocationOutputDto;
import com.nut.driver.app.entity.*;
import com.nut.driver.app.form.ApplyOrderForm;
import com.nut.driver.app.form.CancelOrderForm;
import com.nut.driver.app.form.GetCarLocationForm;
import com.nut.driver.app.pojo.ServiceStationInfoPojo;
import com.nut.driver.app.pojo.WorkOrderInfoPojo;
import com.nut.driver.app.service.ApplyOrderService;
import com.nut.driver.app.service.PushMessageService;
import com.nut.common.assembler.DistanceClient;
import com.nut.common.assembler.ReverseGeoCodingClient;
import com.nut.common.assembler.WoServiceAssembler;
import com.nut.common.constant.ServiceStationVal;
import com.nut.common.enums.ServiceStationEnum;
import com.nut.common.exception.ExceptionUtil;
import com.nut.common.push.PushStaticLocarVal;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.Result;
import com.nut.driver.app.pojo.UserInfoPojo;


import com.nut.common.utils.DelayingQueueComponent;
import com.nut.driver.common.component.BaiduMapComponent;
import com.vdurmont.emoji.EmojiParser;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.driver.service.impl
 * @Author: yzl
 * @CreateTime: 2021-06-21 19:29
 * @Version: 1.0
 */
@Slf4j
@Service
public class ApplyOrderServiceImpl extends DriverBaseService implements ApplyOrderService {

    @Autowired
    private RedisComponent redisComponent;

    @Autowired
    private DistanceClient locationService;

    @Autowired
    private ReverseGeoCodingClient queryGeographicalService;

    @Autowired
    private PushMessageService pushMessageService;

    @Autowired
    private LocationServiceClient locationServiceClient;

    @Autowired
    private BaiduMapComponent baiduMapComponent;

    @Autowired
    private CarDao carDao;

    @Autowired
    private ServiceStationDao serviceStationDao;

    @Autowired
    private WorkOrderDao workOrderDao;

    @Autowired
    private ServiceAarNoticeDao serviceAarNoticeDao;

    @Autowired
    private CarStationStayOvertimeDao carStationStayOvertimeDao;

    @Autowired
    private DataDictDao dataDictDao;

    @Autowired
    private UserDao userDao;

    @Value("${database_name}")
    private String DbName;

    @Value("${workOrderNotAllowAreaCode:dummy}")
    private String areaCodes;

    @Value("${interceptHours:24}")
    private int interceptHours;

    @Value("${interceptMessage:该车辆在24小时内创建过工单，若要继续建单请联系客服创建（400-800-9933）！}")
    private String interceptMessage;

    private int distanceMax = 200000;

    @Value("${queryNoticeType:买赠产品,保养产品,保养活动,托管产品,延保产品,全包产品}")
    private String queryNoticeType;
    /**
     * 第一次延迟推送时间
     */
    @Value("${delayTime:5}")
    private Long delayTime;

    @Autowired
    DelayingQueueComponent queueComponent;
    /**
     * 消息推送管道
     */
    private static final String WORK_ORDER_CHANNEL = "WORK_ORDER_CHANNEL";


    /**
     * 服务预约
     */
    @Transactional(rollbackFor = Exception.class)
    public Map applyOrder(ApplyOrderForm form) {

        if (StringUtils.isNotEmpty(form.getAppoUserName())) {
            String emoji = EmojiParser.removeAllEmojis(form.getAppoUserName());
            form.setAppoUserName(emoji);
        }
        if (StringUtils.isEmpty(form.getMaintainItem()) && StringUtils.isEmpty(form.getRepairItem())) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "请您选择要预约的项目！");
        }
        // 验证时间是否大于当前时间,res时间大于0则表示提醒时间小于系统时间
        Long res = DateUtil.diffNowDateTime(form.getAppoArriveTime());
        if (res > 0) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "预约时间要大于当前系统时间");
        }

        // 外出救援工单时，验证参数
        if (form.getWoType() == ServiceStationVal.OUTSIDE_RESCUE) {
            if (StringUtil.isEmpty(form.getCarLocation())) {
                ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "故障车辆位置不能为空！");
            }
            if (StringUtil.isEmpty(form.getCarLon())) {
                ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "故障车辆经度不能为空！");
            }
            if (StringUtil.isEmpty(form.getCarLat())) {
                ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "故障车辆纬度不能为空！");
            }
        }


        UserInfoPojo userInfoPojo = carDao.queryCarVinAndPhone(form.getAutoIncreaseId().toString(), form.getCarId());
        if (userInfoPojo == null || StringUtil.isEmpty(userInfoPojo.getCarVin())) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "预约车辆不存在！");
        }
        // 注册手机号
        String registeredPhone = userInfoPojo.getPhone();
        // 底盘号
        String carVin = userInfoPojo.getCarVin();
        /* 服务站信息查询 */
        ServiceStationInfoPojo pojo = serviceStationDao.queryServiceStationInfo(DbName, form.getStationId(), ServiceStationVal.JOB_TYPE_ADMIN);
        if (pojo == null) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "未查到预约的服务站！");
        }
        // 判断在指定区域24小时内车辆是否创建过工单
        if (null != pojo.getPovince() && interceptWorkOrder(carVin, String.valueOf(pojo.getPovince()))) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), interceptMessage);
        }

        WorkOrderEntity workOrder = new WorkOrderEntity();
        BigDecimal carLat = BigDecimal.ZERO;
        if (StringUtils.isNotBlank(form.getCarLat())) {
            carLat = new BigDecimal(form.getCarLat());
        }
        BigDecimal carLon = BigDecimal.ZERO;
        BigDecimal zero = BigDecimal.ZERO;
        if (StringUtils.isNotBlank(form.getCarLon())) {
            carLon = new BigDecimal(form.getCarLon());
        }
        if (form.getWoType().equals(ServiceStationVal.OUTSIDE_RESCUE)) {
            getDistance(form, pojo);
        } else if (pojo.getLongitude() != null && pojo.getLatitude() != null
                && StringUtils.isNotBlank(form.getCarLon())
                && StringUtils.isNotBlank(form.getCarLat())
                && (carLat.compareTo(zero) != 0)
                && (carLon.compareTo(zero) != 0)){
            // 车与服务站距离
            workOrder.setCarDistance(getDistance(form, pojo));
        }
        /* 获取车辆上报位置信息 */
        CarLocationOutputDto dto = getCarUpInfo(carVin);
        /* 新建工单 */
        String woType = "I";
        if (form.getWoType() == ServiceStationVal.OUTSIDE_RESCUE) {
            woType = "O";
        }
        // orderWay K:400预约,S:司机App预约，Z：服务站建单
        String woCode = WoServiceAssembler.getDfWoCode(redisComponent, woType, "S", 4, form.getStationId(), pojo.getServiceCode());

        // 工单号
        workOrder.setWoCode(woCode);
        // 工单类型
        workOrder.setWoType(form.getWoType());
        // 工单状态
        workOrder.setWoStatus(ServiceStationEnum.TO_BE_ACCEPTED.code());
        // 预约方式
        workOrder.setAppoType(ServiceStationVal.APPO_TYPE_APP);
        // 预约人ID
        workOrder.setAppoUserId(form.getAutoIncreaseId());
        // 预约人姓名
        workOrder.setAppoUserName(form.getAppoUserName());
        // 预约人电话
        workOrder.setAppoUserPhone(form.getAppoUserPhone());
        // 创建人 同步crm使用
        workOrder.setOperatorId(form.getAppoUserPhone() + "-" + form.getAppoUserName());
        // 底盘号
        workOrder.setChassisNum(carVin);
        // 行驶里程
        if (dto != null && dto.getMileage() != null) {
            DecimalFormat df = new DecimalFormat("######0.0");
            workOrder.setMileage(df.format(dto.getMileage()));
        }

        if (form.getWoType() == ServiceStationVal.STATION_SERVICE) {
            // 进站时取位置云经纬度
            if (dto != null && dto.getLat() != null && dto.getLon() != null) {
                // 逆地理调用
                String location = queryGeographicalService.getPosition(dto.getLat().toString(), dto.getLon().toString());
                // 车辆位置
                workOrder.setCarLocation(location);
                // 车辆经度
                workOrder.setCarLon(dto.getLon().toString());
                // 车辆纬度
                workOrder.setCarLat(dto.getLat().toString());
            }
        } else if (form.getWoType() == ServiceStationVal.OUTSIDE_RESCUE) {
            // 外出救援时取APP端传入位置参数
            // 车辆位置
            workOrder.setCarLocation(form.getCarLocation());
            // 车辆经度
            workOrder.setCarLon(form.getCarLon());
            // 车辆纬度
            workOrder.setCarLat(form.getCarLat());
        }
        // 服务站站id
        workOrder.setStationId(form.getStationId());
        // 服务站编码
        workOrder.setStationCode(pojo.getServiceCode());
        // 服务站名称
        workOrder.setStationName(pojo.getStationName());
        // 预约服务站站id
        workOrder.setAppoStationId(form.getStationId());
        // 服务站省市编码
        workOrder.setAreaCode(pojo.getPovince() == null ? "" : pojo.getPovince().toString());
        // 维修项目
        workOrder.setRepairItem(form.getRepairItem());
        // 保养项目
        workOrder.setMaintainItem(form.getMaintainItem());
        // 预约到站时间
        workOrder.setAppoArriveTime(DateUtil.parseTime(form.getAppoArriveTime()));
        // 注册手机号
        workOrder.setRegisteredPhone(registeredPhone);
        // 用户反馈
        workOrder.setUserComment(form.getUserComment());
        Date date = new Date();
        // 建单时间
        workOrder.setTimeCreate(date);
        // 更新时间
        workOrder.setUpdateTime(date);
        // 创建时间
        workOrder.setCreateTime(date);
        workOrder.setRescueType(ServiceStationVal.OUTSIDE_NORMALRESCUE);
        // 判断工单是否服务协议
        long countProtocoWo = serviceAarNoticeDao.countProtocolVin(Arrays.asList(queryNoticeType.split(",")), carVin);
        if (countProtocoWo > 0) {
            // 是服务协议
            workOrder.setProtocolMark(2);
        } else {
            // 非服务协议
            workOrder.setProtocolMark(1);
        }
        // 数据插入
        workOrderDao.insert(workOrder);

        RelationWorkOrderUser relationWorkOrderUser = new RelationWorkOrderUser();
        relationWorkOrderUser.setUcId(form.getUserId());
        relationWorkOrderUser.setWoCode(woCode);
        relationWorkOrderUser.setPhone(registeredPhone);
        relationWorkOrderUser.setAppType(form.getAppType());
        relationWorkOrderUser.setWorkState(0);
        relationWorkOrderUser.setCreateTime(new Date());

        log.info("----添加工单用户关联信息----star");
        log.info("----关联信息：----" + relationWorkOrderUser.toString());
        workOrderDao.insertRelationWorkOrderUser(relationWorkOrderUser);
        log.info("----添加工单用户关联信息----end");

        Map<String, String> woCodeMap = new HashMap<>(2);
        woCodeMap.put("woCode", woCode);

        // 是否存在进站预警，存在时新建进站工单后解除预警
        updWarningStatus(form.getCarId(), pojo.getServiceCode(), ServiceStationVal.INQ_CREATE);
        /*延时 推送 */
        try {
            //第一次正常推送不做处理
            // 通配符替换
            Map<String, String> wildcardMap = new HashMap<>(3);
            wildcardMap.put("{工单号}", woCode);
            //截取手机号后四位
            wildcardMap.put("{手机后四位}", registeredPhone.substring(registeredPhone.length() - 4));

            if ("I".equals(woType)) {
                wildcardMap.put("{预约类型}", "预约进站");
            }
            if ("O".equals(woType)) {
                wildcardMap.put("{预约类型}", "外出救援");
            }

            String wildcard = JsonUtil.toJson(wildcardMap);
            // 扩展信息
            Map<String, String> messageExtraMap = new HashMap<>(4);
            // 工单号
            messageExtraMap.put(PushStaticLocarVal.MESSAGE_EXTRA_WO_CODE, woCode);
            messageExtraMap.put(PushStaticLocarVal.MESSAGE_EXTRA_WO_STATUS, workOrder.getWoStatus().toString());
            messageExtraMap.put(PushStaticLocarVal.MESSAGE_EXTRA_WO_TYPE, workOrder.getWoType().toString());
            messageExtraMap.put(PushStaticLocarVal.MESSAGE_EXTRA_APPO_TYPE, workOrder.getAppoType().toString());
            String messageExtra = JsonUtil.toJson(messageExtraMap);
            pushMessageService.pushMessageToStation(PushStaticLocarVal.PUSH_TYPE_TEN_STYPE_ONE, wildcard, messageExtra, form.getUserId(), pojo.getUserId(), PushStaticLocarVal.PUSH_SHOW_TYPE_WO);
            //todo 用于延迟队列，暂时功能隐藏
            //生产者生产消息
            /*DelayMessageEntity.MessageBody body = new DelayMessageEntity.MessageBody();
            Boolean flag = queueComponent.push(
                    new DelayMessageEntity()
                        .setBody(JsonUtil.toJson(
                                body.setWildcard(wildcard)
                                    .setMessageExtra(messageExtra)
                                    .setSendId(form.getUserId())
                                    .setReceiveId(pojo.getUserId())
                        ))
                        .setChannel(WORK_ORDER_CHANNEL)
                        .setVersion(1)
                        .setId(UUID.randomUUID().toString())
                        .setCreateTime(LocalDateTime.now())
                        .setDelayTime(System.currentTimeMillis()+(delayTime* CommonConstants.QUEUE_TIME))
            );

            log.info("工单延迟队列生产消息结果：{}",flag);*/

        } catch (Exception e) {
            log.info("[woAdd] push Exception:{}", e.getMessage());
        }

        log.info("applyOrder end return:{}", woCodeMap);
        return woCodeMap;
    }

    private String getDistance(ApplyOrderForm form, ServiceStationInfoPojo pojo) {
        JSONObject jsonObject = baiduMapComponent.distance2point(
                pojo.getLatitude().toString(),
                pojo.getLongitude().toString(),
                form.getCarLat(), form.getCarLon(), "bd09ll");
        if (Objects.isNull(jsonObject)) {
            ExceptionUtil.result(ECode.DISTANCE_LIMIT.code(), "当前位置获取失败，请打开定位后重试");
        }
        String distance = jsonObject.getJSONArray("routes").getJSONObject(0).get("distance").toString();
        if (Integer.parseInt(distance) > distanceMax) {
            ExceptionUtil.result(ECode.DISTANCE_LIMIT.code(), "故障车位置超过服务站200km的服务距离，请重新选择或电话预约");
        }

        return distance;
    }

    @Override
    @SneakyThrows
    @Transactional(rollbackFor = Exception.class)
    public HttpCommandResultWithData cancleOrder(CancelOrderForm form) {


        DataDictEntity record = new DataDictEntity();
        record.setCode(ServiceStationVal.DATA_DICT_CODE_CANCEL_REASON);
        record.setValue(Integer.parseInt(form.getCancelReason()));
        record = dataDictDao.selectByCodeAndValue(record);
        if (record == null) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "未获取到取消原因！");
        }
        /* 查询工单信息 */
        WorkOrderEntity workOrder = workOrderDao.queryWorkOrderByWoCode(form.getWoCode());
        if (workOrder == null) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "此工单不存在！");
        }
        /* 工单预约人判断 */
        if (workOrder.getAppoUserId() != null) {
            if (form.getAutoIncreaseId().longValue() != workOrder.getAppoUserId().longValue()) {
                ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "您不是此工单的预约人，不能取消预约！！");
            }
        } else {
            UserEntity user = userDao.selectByPrimaryKey(form.getAutoIncreaseId());
            if (StringUtil.isNotEq(workOrder.getAppoUserPhone(), user.getPhone())) {
                ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "您不是此工单的预约人，不能取消预约！！");
            }
        }

        /* 数据校验 */
        String errMsg = validateStatus(workOrder);
        if (StringUtil.isNotEmpty(errMsg)) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), errMsg);
        }

        /* 更新工单 */
        WorkOrderEntity updateWorkOrder = new WorkOrderEntity();
        updateWorkOrder.setWoStatus(ServiceStationEnum.CANCEL_ORDER.code());
        updateWorkOrder.setCancelReason(record.getName());
        updateWorkOrder.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        updateWorkOrder.setId(workOrder.getId());
        workOrderDao.updateByPrimaryKeySelective(updateWorkOrder);

        log.info("cancelOrder end return:{null}");
        return Result.ok();
    }


    /**
     * 判断24小时内车辆是否创建过工单
     */
    private boolean interceptWorkOrder(String carVin, String areaCode) {
        boolean result = false;
        List<String> areaCodeList = Arrays.asList(areaCodes.split(","));
        if (StringUtil.isNotEmpty(carVin) && StringUtil.isNotEmpty(areaCode)
                && CollectionUtils.isNotEmpty(areaCodeList) && areaCodeList.contains(areaCode)) {
            // 查询车辆最近一条创建的工单
            List<WorkOrderInfoPojo> list = workOrderDao.queryLatelyWorkOrderByVin(Arrays.asList(carVin));
            if (CollectionUtils.isNotEmpty(list)) {
                WorkOrderInfoPojo pojo = list.get(0);
                if (null != pojo && null != pojo.getCreateTime()
                        && (new Date().getTime() - pojo.getCreateTime().getTime()) / 1000 < interceptHours * 3600) {
                    result = true;
                }
            }
        }
        return result;
    }

    /**
     * @param vin vin
     * @Description: 获取车辆上报位置信息
     */
    public CarLocationOutputDto getCarUpInfo(String vin) {
        log.info("[getCarUpInfo]start");
        CarLocationOutputDto dto = new CarLocationOutputDto();
        try {
            // 根据vin 的集合 获取对应的地理 坐标
            GetCarLocationForm getCarLocationCommand = new GetCarLocationForm();
            // 设置Vin
            getCarLocationCommand.setVins(vin);
            // 调用webservice获取结果
            log.info("locationServiceClient.carLocation paramVin: {}", vin);
            HttpCommandResultWithData result = locationServiceClient.carLocation(getCarLocationCommand);
            log.info("locationServiceClient.carLocation result: {}", JsonUtil.toJson(result));
            if (result != null && ECode.SUCCESS.code() == result.getResultCode()) {
                // 获取位置数据map
                Map<String, CarLocationOutputDto> locateCarMap = (Map<String, CarLocationOutputDto>) result.getData();
                log.info("locationServiceClient.carLocation locateCarMap: {}", JsonUtil.toJson(locateCarMap));
                dto = JsonUtil.fromJson(JsonUtil.toJson(locateCarMap.get(vin)), CarLocationOutputDto.class);
            }
        } catch (Exception e) {
            log.error("[getCarUpInfo]Exception:", e);
        }
        log.info("[getCarUpInfo]end");
        return dto;
    }

    /**
     * 更新超时预警信息
     *
     * @param carId   车辆ID
     * @param staCode 服务站编码
     * @param status  状态（0：默认 1：已回访 2：标记解除预警
     *                3：建单解除预警 4：接单解除预警 5：接车解除预警
     *                6：拒单解除预警 7：超时解除预警）
     * @throws Exception 异常
     */
    private void updWarningStatus(String carId, String staCode, int status) {
        log.info("[ApplyOrderService][applyOrder] update params: carId : {} || staCode : {} || status : {}", carId, staCode, status);
        CarStationStayOvertimeEntity csso = carStationStayOvertimeDao.queryWarningData(carId, staCode);

        if (csso == null) {
            log.info("[ApplyOrderService][applyOrder] This csso is null");
            return;
        }
        if (csso.getStatus() != 0) {
            log.info("[ApplyOrderService][applyOrder] This status is not 0");
            return;
        }

        csso.setStatus(status);
        csso.setUpdateTime(new Date());
        carStationStayOvertimeDao.updateByPrimaryKeySelective(csso);
    }

    /**
     * 数据校验
     */
    private String validateStatus(WorkOrderEntity workOrder) {
        /* 工单状态判断 出发接车前都可以取消预约*/
        Integer woStatus = workOrder.getWoStatus();
        if (ServiceStationEnum.CANCEL_ORDER.code() == woStatus) {
            return "您已取消预约！";
        } else if (ServiceStationEnum.WORK_DONE.code() == woStatus) {
            return "您的预约服务已完成，请您给个评价吧！";
        } else if (ServiceStationEnum.CLOSE_REFUSED.code() == woStatus || ServiceStationEnum.CLOSE_INSPECT.code() == woStatus || ServiceStationEnum.CLOSE_REPAIR.code() == woStatus
                || ServiceStationEnum.CLOSE_TAKEOFF.code() == woStatus
                || ServiceStationEnum.CLOSE_RECEIVE.code() == woStatus) {
            // 已关闭原因
            String closeReason;
            if (ServiceStationEnum.CLOSE_REFUSED.code() == woStatus && StringUtil.isNotEmpty(workOrder.getRefuseReason())) {
                closeReason = workOrder.getRefuseReason();
            } else if (StringUtil.isNotEmpty(workOrder.getCloseReason())) {
                closeReason = workOrder.getCloseReason();
            } else {
                closeReason = "服务站已取消预约，请更换其他服务站";
            }
            return closeReason;
        } else {
            if (workOrder.getWoType() == ServiceStationVal.STATION_SERVICE) {
                // 进站
                if (ServiceStationEnum.TO_BE_ACCEPTED.code() != woStatus
                        && ServiceStationEnum.TO_RECEIVE.code() != woStatus) {
                    return "您的预约服务无法取消，请电话联系服务站";
                }
            } else {
                // 外出救援
                if (ServiceStationEnum.TO_BE_ACCEPTED.code() != woStatus
                        && ServiceStationEnum.TO_TAKE_OFF.code() != woStatus) {
                    return "您的预约服务无法取消，请电话联系服务站";
                }
            }
        }
        return "";
    }


}
