package com.nut.servicestation.app.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nut.common.assembler.ReverseGeoCodingClient;
import com.nut.common.constant.OperateCodeEnum;
import com.nut.common.constant.ServiceStationVal;
import com.nut.common.enums.ServiceStationEnum;
import com.nut.common.exception.ExceptionUtil;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.utils.DateUtil;
import com.nut.common.utils.JsonUtil;
import com.nut.common.utils.StringUtil;
import com.nut.locationservice.app.dto.CarLocationOutputDto;
import com.nut.locationservice.app.dto.CarOnlineStatusDTO;
import com.nut.locationservice.app.form.GetCarLocationForm;
import com.nut.locationservice.app.form.GetOnlineStatusForm;
import com.nut.servicestation.app.client.LocationServiceClient;
import com.nut.servicestation.app.dao.*;
import com.nut.servicestation.app.domain.Car;
import com.nut.servicestation.app.domain.WorkOrder;
import com.nut.servicestation.app.domain.WorkOrderOperate;
import com.nut.servicestation.app.dto.UserInfoDto;
import com.nut.servicestation.app.form.AddWoStationForm;
import com.nut.servicestation.app.pojo.WorkOrderInfoPojo;
import com.nut.servicestation.app.service.AddWoStationService;
import com.nut.servicestation.app.service.ScanReceiveService;
import com.nut.servicestation.app.service.UserService;
import com.nut.servicestation.app.service.WarnInTheStationService;
import com.nut.servicestation.common.assembler.NaviDistanceClient;
import com.nut.servicestation.common.assembler.WoServiceAssembler;
import com.nut.servicestation.common.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

/*
 *  @author wuhaotian 2021/7/5
 */
@Slf4j
@Service("AddWoStationService")
public class AddWoStationServiceImpl implements AddWoStationService {


    @Autowired
    private LocationServiceClient locationServiceClient;
    @Autowired
    private CarDao carMapper;
    @Autowired
    private UserService userService;
    @Value("${workOrderNotAllowAreaCode:dummy}")
    private String areaCodes;
    @Autowired
    private WorkOrderDao workOrderMapper;
    @Value("${interceptHours:24}")
    private int interceptHours;
    @Value("${interceptMessage:该车辆在24小时内创建过工单，若要继续建单请联系客服创建（400-800-9933）！}")
    private String interceptMessage;
    @Autowired
    private RedisUtil redis;
    @Autowired
    private ScanReceiveService scanReceiveService;
    @Autowired
    private UserDao userServiceMapper;
    @Autowired
    private ReverseGeoCodingClient queryGeographicalService;
    @Autowired
    private NaviDistanceClient locationService;
    @Autowired
    private ServiceAarNoticeDao serviceAarNoticeMapper;
    @Value("${queryNoticeType:买赠产品,保养产品,保养活动,托管产品,延保产品,全包产品}")
    private String queryNoticeType;
    @Autowired
    private WarnInTheStationService warnInTheStationService;
    @Autowired
    private WorkOrderOperateDao workOrderOperateMapper;

    @Override
    public CarLocationOutputDto getCarUpInfo(String chassisNum) {
        log.info("[getCarUpInfo]start");
        CarLocationOutputDto dto = new CarLocationOutputDto();
        try {
            // 根据vin 的集合 获取对应的地理 坐标
            GetCarLocationForm getCarLocationCommand = new GetCarLocationForm();
            // 设置Vin
            getCarLocationCommand.setVins(chassisNum);
            // 调用webservice获取结果
            log.info("locationServiceClient.carLocation paramVin: {}", chassisNum);
            HttpCommandResultWithData result = locationServiceClient.carLocation(getCarLocationCommand);
            log.info("locationServiceClient.carLocation result: {}", JsonUtil.toJson(result));
            if (result != null && ECode.SUCCESS.code() == result.getResultCode()) {
                // 获取位置数据map
                Map<String, CarLocationOutputDto> locateCarMap = (Map<String, CarLocationOutputDto>) result.getData();
                log.info("locationServiceClient.carLocation locateCarMap: {}", JsonUtil.toJson(locateCarMap));
                if (locateCarMap != null) {
                    dto = JsonUtil.fromJson(JsonUtil.toJson(locateCarMap.get(chassisNum)), CarLocationOutputDto.class);
                }
            }
        } catch (Exception e) {
            log.error("[getCarUpInfo]Exception:", e);
        }
        log.info("[getCarUpInfo]end");
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HttpCommandResultWithData<Map<String, String>> newWoSta(AddWoStationForm command) throws Exception {
        log.info("[newWoSta]start");
        HttpCommandResultWithData<Map<String, String>> result = new HttpCommandResultWithData<>();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());
        // 入参校验
        String error = validateCommand(command);
        if (StringUtil.isNotEmpty(error)) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage(error);
            return result;
        }

        // 车辆信息
        Car car = carMapper.selectByPrimaryKey(command.getCarId());
        if (car == null) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("车辆不存在");
            return result;
        }

        // 服务站用户信息
        UserInfoDto stationUser = userService.getUserInfoByUserId(command.getUserId(), true);
        if (stationUser == null) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("用户不存在");
            return result;
        }
        if (stationUser.getStationWoCreatable() == 0) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("该服务站无自主建单权限");
            return result;
        }
        if (stationUser.getRoleCode() == ServiceStationVal.JOB_TYPE_SALESMAN) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("该账号无建单权限，请联系本站服务专员！");
            return result;
        }
        if (stationUser.getSecondaryCreateWoRange() != 1 && stationUser.getSecondaryCreateWoRange() != 3) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("该服务站无建进站工单权限！");
            return result;
        }

        //判断是否有建单权限
        WorkOrder isWorkOrder = workOrderMapper.selectByCarVin(car.getCarVin(),stationUser.getServiceStationId());
        if (isWorkOrder != null){
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("此车辆已有未完成工单！！");
            return result;
        }

        // 判断24小时内车辆是否创建过工单
 /*       List<String> areaCodeList = Arrays.asList(areaCodes.split(","));
        if (StringUtil.isNotEmpty(car.getCarVin()) && StringUtil.isNotEmpty(stationUser.getAreaCode())
                && CollectionUtils.isNotEmpty(areaCodeList) && areaCodeList.contains(stationUser.getAreaCode())) {
            // 查询车辆最近一条创建的工单
            WorkOrderInfoPojo wo = workOrderMapper.queryLatelyWorkOrderByVin(car.getCarVin());
            if (null != wo && null != wo.getCreateTime()
                    && (System.currentTimeMillis() - wo.getCreateTime().getTime()) / 1000 < interceptHours * 3600) {
                result.setResultCode(ECode.CLIENT_ERROR.code());
                result.setMessage(interceptMessage);
                return result;
            }
        }*/

        // 建单
        WorkOrder workOrder = stationNewWo(command, car.getCarVin(), stationUser);
        String woCode = workOrder.getWoCode();
        // 自动接单
        stationAcceptOrder(woCode, command.getUserId(), stationUser.getServiceStationName());
        // 自动接车
        scanReceiveService.receive(workOrder);

        /* 触发报单流程 */
        // 直接触发的话事务还没有提交，kafka消费者查询不到mysql数据，延时5秒处理
//        ScheduledExecutorService pool = Executors.newSingleThreadScheduledExecutor();
//        pool.schedule(() -> {
//            startEndRepairService.trySendKafka(woCode, ServiceStationVal.WEB_SERVICE_CREATESMORDER, "确认接单");
//        }, 5, TimeUnit.SECONDS);
//        pool.shutdown();
        Map<String, String> data = new HashMap<>(3);
        data.put("woCode", woCode);
        result.setData(data);
        log.info("[newWoSta]end");
        return result;
    }

    @Override
    public HttpCommandResultWithData getRealTimeInfo(List<String> carVins, Map<String, CarOnlineStatusDTO> outMap) throws JsonProcessingException {
        log.info("[getRealTimeInfo]start");
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        if (carVins == null || carVins.isEmpty()) {
            return result;
        }
        GetOnlineStatusForm cloudCommand = new GetOnlineStatusForm();
        cloudCommand.setVinList(carVins);
        cloudCommand.setSourceFlag(false);
        log.info("[getRealTimeInfo]call getOnlineStatus start");
        log.info("[getRealTimeInfo]param: {}", JsonUtil.toJson(cloudCommand));
        HttpCommandResultWithData cloudResult = locationServiceClient.getOnlineStatus(cloudCommand);
        log.info("[getRealTimeInfo]reutrn: {}", JsonUtil.toJson(cloudResult));
        log.info("[getRealTimeInfo]call getOnlineStatus end");
        if (cloudResult.getResultCode() == ECode.FALLBACK.code()) {
            // feign降级处理
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage(ECode.FALLBACK.message());
            return result;
        } else if (cloudResult.getResultCode() != ECode.SUCCESS.code()) {
            return cloudResult;
        }
        if (cloudResult.getData() != null && outMap != null) {
            Map<String, Object> map = null;
            try {
                map = JsonUtil.toMap(JsonUtil.toJson(cloudResult.getData()));
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
            if (map != null) {
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    CarOnlineStatusDTO carOnlineStatusDto = null;
                    try {
                        carOnlineStatusDto = JsonUtil.fromJson(JsonUtil.toJson(entry.getValue()), CarOnlineStatusDTO.class);
                    } catch (JsonProcessingException e) {
                        log.error(e.getMessage(), e);
                    }
                    if (carOnlineStatusDto == null) {
                        continue;
                    }
                    outMap.put(entry.getKey(), carOnlineStatusDto);
                }
            }
        }
        log.info("[getRealTimeInfo]end");
        return result;
    }

    /**
     * 验证入参
     */
    private String validateCommand(AddWoStationForm command) {
        if (StringUtil.isEmpty(command.getRepairItem()) && StringUtil.isEmpty(command.getMaintainItem())) {
            return "请选择保养项目或维修项目";
        }
        // 验证时间是否大于当前时间,res时间大于0则表示提醒时间小于系统时间
        Long res = DateUtil.diffNowDateTime(command.getAppoArriveTime());
        if (res > 0) {
            return "预约到站时间应大于等于当前时间";
        }
        return "";
    }
    /**
     * 服务站建单逻辑（包含自动接单的工单表字段更新！）
     */
    private WorkOrder stationNewWo(AddWoStationForm command, String chassisNum, UserInfoDto station) {
        log.info("[stationNewWo]start");
        // 获取车辆上报位置信息
        CarLocationOutputDto dto = getCarUpInfo(chassisNum);
        // 进出站
        String woType = "I";
        // orderWay K:400预约,S:司机App预约，Z：服务站建单
        String woCode = WoServiceAssembler.getDfWoCode(redis, woType, "Z", 4, station.getServiceStationId(), station.getServiceCode());

        WorkOrder workOrder = new WorkOrder();
        // 工单号
        workOrder.setWoCode(woCode);
        // 工单类型
        workOrder.setWoType(ServiceStationVal.STATION_SERVICE);
        // 工单状态
        workOrder.setWoStatus(ServiceStationEnum.TO_BE_ACCEPTED.code());
        // 预约方式
        workOrder.setAppoType(ServiceStationVal.APPO_TYPE_STA);
        // 预约人ID
        workOrder.setAppoUserId(userServiceMapper.queryIdByPhone(command.getAppoUserPhone()));
        // 预约人姓名
        workOrder.setAppoUserName(command.getAppoUserName());
        // 预约人电话
        workOrder.setAppoUserPhone(command.getAppoUserPhone());
        // 创建人
        if(StringUtils.isBlank(station.getAccountName())){
            workOrder.setOperatorId(station.getServiceCode() + "-" + station.getAccountId());
        }else {
            workOrder.setOperatorId(station.getServiceCode() + "-" + station.getAccountName());
        }
        // 底盘号
        workOrder.setChassisNum(chassisNum);
        String mileage = command.getMileage();
        if (".".equals(mileage.substring(mileage.length()-1))){
            mileage = mileage.substring(0,mileage.lastIndexOf("."));
        }
        // 行驶里程
        workOrder.setMileage(mileage);
        if (dto != null && dto.getLat() != null && dto.getLon() != null) {
            // 逆地理调用
            String location = queryGeographicalService.getPosition(dto.getLat().toString(), dto.getLon().toString());
            // 车辆位置
            workOrder.setCarLocation(location);
            // 车辆经度
            workOrder.setCarLon(dto.getLon().toString());
            // 车辆纬度
            workOrder.setCarLat(dto.getLat().toString());
            // 车与服务站距离
            if (StringUtil.isNotEmpty(station.getServiceStationLon()) && StringUtil.isNotEmpty(station.getServiceStationLat())) {
                ///////////////////////////////////////////////////// 图吧导航距离计算接口 /////////////////////////////////////////////////////
                int dis = locationService.getDistance(station.getServiceStationLon(), station.getServiceStationLat(), dto.getLon().toString(), dto.getLat().toString());
                workOrder.setCarDistance(String.valueOf(dis));
                ///////////////////////////////////////////////////// //////////////////////////////////////////////////////////////////////////
            }
        }
        // 服务站id
        workOrder.setStationId(station.getServiceStationId());
        // 服务站编码
        workOrder.setStationCode(station.getServiceCode());
        // 服务站名称
        workOrder.setStationName(station.getServiceStationName());
        // 预约服务站站id
        workOrder.setAppoStationId(station.getServiceStationId());
        // 服务站省市编码
        workOrder.setAreaCode(station.getAreaCode());
        // 维修项目
        workOrder.setRepairItem(command.getRepairItem());
        // 保养项目
        workOrder.setMaintainItem(command.getMaintainItem());
        // 预约到站时间
        workOrder.setAppoArriveTime(DateUtil.parseTime(command.getAppoArriveTime()));
        // 用户反馈
        workOrder.setUserComment(command.getUserComment());
        Date date = new Date();
        // 建单时间
        workOrder.setTimeCreate(date);
        // 更新时间
        workOrder.setUpdateTime(date);
        // 创建时间
        workOrder.setCreateTime(date);

        /*
         * 自动接单的工单表字段更新
         */
        workOrder.setWoStatus(ServiceStationEnum.TO_RECEIVE.code());
        workOrder.setTimeAccept(date);
        workOrder.setRescueType(ServiceStationVal.OUTSIDE_NORMALRESCUE);
        // 是否协议
        long countProtocoWo = serviceAarNoticeMapper.countProtocolVin(Arrays.asList(queryNoticeType.split(",")), chassisNum);
        if (countProtocoWo > 0) {
            // 协议
            workOrder.setProtocolMark(2);
        } else {
            /*long countCrmPushProtocoWo = serviceAarNoticeCrmPushMapper.countProtocolVin(Arrays.asList(queryNoticeType.split(",")), chassisNum);
            if (countCrmPushProtocoWo > 0) {
                // 协议
                workOrder.setProtocolMark(2);
            } else {*/
            // 非协议
            workOrder.setProtocolMark(1);
            /*}*/
        }

        // 数据插入
        workOrderMapper.insert(workOrder);
        // 是否存在进站预警，存在时新建进站工单后解除预警
        warnInTheStationService.updWarningStatus(command.getCarId(), station.getServiceCode(), ServiceStationVal.INQ_CREATE);
        log.info("[stationNewWo]end");
        return workOrder;
    }
    /**
     * 自动接单
     */
    private void stationAcceptOrder(String woCode, String stationUserId, String stationName) throws JsonProcessingException {
        log.info("[stationAcceptOrder]start");
        // 系统时间
        Date date = new Date();
        // 添加工单操作记录
        WorkOrderOperate workOrderOperate = new WorkOrderOperate();
        workOrderOperate.setWoCode(woCode);
        workOrderOperate.setOperateCode(OperateCodeEnum.OP_ACCEPT.code());
        workOrderOperate.setTitle(OperateCodeEnum.OP_ACCEPT.message());
        workOrderOperate.setUserId(stationUserId);
        workOrderOperate.setCreateTime(date);
        workOrderOperate.setUpdateTime(date);
        // 服务记录显示内容用json字符串格式保存
        Map<String, String> jsonMap = new LinkedHashMap<>(5);
        jsonMap.put("接单账号：", stationUserId);
        jsonMap.put("接单时间：", DateUtil.getDatePattern(date.getTime()));
        jsonMap.put("接单服务站：", stationName);
        workOrderOperate.setTextJson(JsonUtil.toJson(jsonMap));
        // 设置隐藏，产品未要求显示自动接单记录，但后台需要保存
        workOrderOperate.setHiddenFlg(1);
        workOrderOperateMapper.insertSelective(workOrderOperate);
        log.info("[stationAcceptOrder]end");
    }
}
