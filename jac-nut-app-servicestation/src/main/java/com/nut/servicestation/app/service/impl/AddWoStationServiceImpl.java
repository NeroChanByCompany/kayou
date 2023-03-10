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
    @Value("${interceptMessage:????????????24?????????????????????????????????????????????????????????????????????400-800-9933??????}")
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
    @Value("${queryNoticeType:????????????,????????????,????????????,????????????,????????????,????????????}")
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
            // ??????vin ????????? ????????????????????? ??????
            GetCarLocationForm getCarLocationCommand = new GetCarLocationForm();
            // ??????Vin
            getCarLocationCommand.setVins(chassisNum);
            // ??????webservice????????????
            log.info("locationServiceClient.carLocation paramVin: {}", chassisNum);
            HttpCommandResultWithData result = locationServiceClient.carLocation(getCarLocationCommand);
            log.info("locationServiceClient.carLocation result: {}", JsonUtil.toJson(result));
            if (result != null && ECode.SUCCESS.code() == result.getResultCode()) {
                // ??????????????????map
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
        // ????????????
        String error = validateCommand(command);
        if (StringUtil.isNotEmpty(error)) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage(error);
            return result;
        }

        // ????????????
        Car car = carMapper.selectByPrimaryKey(command.getCarId());
        if (car == null) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("???????????????");
            return result;
        }

        // ?????????????????????
        UserInfoDto stationUser = userService.getUserInfoByUserId(command.getUserId(), true);
        if (stationUser == null) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("???????????????");
            return result;
        }
        if (stationUser.getStationWoCreatable() == 0) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("?????????????????????????????????");
            return result;
        }
        if (stationUser.getRoleCode() == ServiceStationVal.JOB_TYPE_SALESMAN) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("?????????????????????????????????????????????????????????");
            return result;
        }
        if (stationUser.getSecondaryCreateWoRange() != 1 && stationUser.getSecondaryCreateWoRange() != 3) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("???????????????????????????????????????");
            return result;
        }

        //???????????????????????????
        WorkOrder isWorkOrder = workOrderMapper.selectByCarVin(car.getCarVin(),stationUser.getServiceStationId());
        if (isWorkOrder != null){
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("????????????????????????????????????");
            return result;
        }

        // ??????24????????????????????????????????????
 /*       List<String> areaCodeList = Arrays.asList(areaCodes.split(","));
        if (StringUtil.isNotEmpty(car.getCarVin()) && StringUtil.isNotEmpty(stationUser.getAreaCode())
                && CollectionUtils.isNotEmpty(areaCodeList) && areaCodeList.contains(stationUser.getAreaCode())) {
            // ???????????????????????????????????????
            WorkOrderInfoPojo wo = workOrderMapper.queryLatelyWorkOrderByVin(car.getCarVin());
            if (null != wo && null != wo.getCreateTime()
                    && (System.currentTimeMillis() - wo.getCreateTime().getTime()) / 1000 < interceptHours * 3600) {
                result.setResultCode(ECode.CLIENT_ERROR.code());
                result.setMessage(interceptMessage);
                return result;
            }
        }*/

        // ??????
        WorkOrder workOrder = stationNewWo(command, car.getCarVin(), stationUser);
        String woCode = workOrder.getWoCode();
        // ????????????
        stationAcceptOrder(woCode, command.getUserId(), stationUser.getServiceStationName());
        // ????????????
        scanReceiveService.receive(workOrder);

        /* ?????????????????? */
        // ??????????????????????????????????????????kafka?????????????????????mysql???????????????5?????????
//        ScheduledExecutorService pool = Executors.newSingleThreadScheduledExecutor();
//        pool.schedule(() -> {
//            startEndRepairService.trySendKafka(woCode, ServiceStationVal.WEB_SERVICE_CREATESMORDER, "????????????");
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
            // feign????????????
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
     * ????????????
     */
    private String validateCommand(AddWoStationForm command) {
        if (StringUtil.isEmpty(command.getRepairItem()) && StringUtil.isEmpty(command.getMaintainItem())) {
            return "????????????????????????????????????";
        }
        // ????????????????????????????????????,res????????????0???????????????????????????????????????
        Long res = DateUtil.diffNowDateTime(command.getAppoArriveTime());
        if (res > 0) {
            return "?????????????????????????????????????????????";
        }
        return "";
    }
    /**
     * ????????????????????????????????????????????????????????????????????????
     */
    private WorkOrder stationNewWo(AddWoStationForm command, String chassisNum, UserInfoDto station) {
        log.info("[stationNewWo]start");
        // ??????????????????????????????
        CarLocationOutputDto dto = getCarUpInfo(chassisNum);
        // ?????????
        String woType = "I";
        // orderWay K:400??????,S:??????App?????????Z??????????????????
        String woCode = WoServiceAssembler.getDfWoCode(redis, woType, "Z", 4, station.getServiceStationId(), station.getServiceCode());

        WorkOrder workOrder = new WorkOrder();
        // ?????????
        workOrder.setWoCode(woCode);
        // ????????????
        workOrder.setWoType(ServiceStationVal.STATION_SERVICE);
        // ????????????
        workOrder.setWoStatus(ServiceStationEnum.TO_BE_ACCEPTED.code());
        // ????????????
        workOrder.setAppoType(ServiceStationVal.APPO_TYPE_STA);
        // ?????????ID
        workOrder.setAppoUserId(userServiceMapper.queryIdByPhone(command.getAppoUserPhone()));
        // ???????????????
        workOrder.setAppoUserName(command.getAppoUserName());
        // ???????????????
        workOrder.setAppoUserPhone(command.getAppoUserPhone());
        // ?????????
        if(StringUtils.isBlank(station.getAccountName())){
            workOrder.setOperatorId(station.getServiceCode() + "-" + station.getAccountId());
        }else {
            workOrder.setOperatorId(station.getServiceCode() + "-" + station.getAccountName());
        }
        // ?????????
        workOrder.setChassisNum(chassisNum);
        String mileage = command.getMileage();
        if (".".equals(mileage.substring(mileage.length()-1))){
            mileage = mileage.substring(0,mileage.lastIndexOf("."));
        }
        // ????????????
        workOrder.setMileage(mileage);
        if (dto != null && dto.getLat() != null && dto.getLon() != null) {
            // ???????????????
            String location = queryGeographicalService.getPosition(dto.getLat().toString(), dto.getLon().toString());
            // ????????????
            workOrder.setCarLocation(location);
            // ????????????
            workOrder.setCarLon(dto.getLon().toString());
            // ????????????
            workOrder.setCarLat(dto.getLat().toString());
            // ?????????????????????
            if (StringUtil.isNotEmpty(station.getServiceStationLon()) && StringUtil.isNotEmpty(station.getServiceStationLat())) {
                ///////////////////////////////////////////////////// ?????????????????????????????? /////////////////////////////////////////////////////
                int dis = locationService.getDistance(station.getServiceStationLon(), station.getServiceStationLat(), dto.getLon().toString(), dto.getLat().toString());
                workOrder.setCarDistance(String.valueOf(dis));
                ///////////////////////////////////////////////////// //////////////////////////////////////////////////////////////////////////
            }
        }
        // ?????????id
        workOrder.setStationId(station.getServiceStationId());
        // ???????????????
        workOrder.setStationCode(station.getServiceCode());
        // ???????????????
        workOrder.setStationName(station.getServiceStationName());
        // ??????????????????id
        workOrder.setAppoStationId(station.getServiceStationId());
        // ?????????????????????
        workOrder.setAreaCode(station.getAreaCode());
        // ????????????
        workOrder.setRepairItem(command.getRepairItem());
        // ????????????
        workOrder.setMaintainItem(command.getMaintainItem());
        // ??????????????????
        workOrder.setAppoArriveTime(DateUtil.parseTime(command.getAppoArriveTime()));
        // ????????????
        workOrder.setUserComment(command.getUserComment());
        Date date = new Date();
        // ????????????
        workOrder.setTimeCreate(date);
        // ????????????
        workOrder.setUpdateTime(date);
        // ????????????
        workOrder.setCreateTime(date);

        /*
         * ????????????????????????????????????
         */
        workOrder.setWoStatus(ServiceStationEnum.TO_RECEIVE.code());
        workOrder.setTimeAccept(date);
        workOrder.setRescueType(ServiceStationVal.OUTSIDE_NORMALRESCUE);
        // ????????????
        long countProtocoWo = serviceAarNoticeMapper.countProtocolVin(Arrays.asList(queryNoticeType.split(",")), chassisNum);
        if (countProtocoWo > 0) {
            // ??????
            workOrder.setProtocolMark(2);
        } else {
            /*long countCrmPushProtocoWo = serviceAarNoticeCrmPushMapper.countProtocolVin(Arrays.asList(queryNoticeType.split(",")), chassisNum);
            if (countCrmPushProtocoWo > 0) {
                // ??????
                workOrder.setProtocolMark(2);
            } else {*/
            // ?????????
            workOrder.setProtocolMark(1);
            /*}*/
        }

        // ????????????
        workOrderMapper.insert(workOrder);
        // ?????????????????????????????????????????????????????????????????????
        warnInTheStationService.updWarningStatus(command.getCarId(), station.getServiceCode(), ServiceStationVal.INQ_CREATE);
        log.info("[stationNewWo]end");
        return workOrder;
    }
    /**
     * ????????????
     */
    private void stationAcceptOrder(String woCode, String stationUserId, String stationName) throws JsonProcessingException {
        log.info("[stationAcceptOrder]start");
        // ????????????
        Date date = new Date();
        // ????????????????????????
        WorkOrderOperate workOrderOperate = new WorkOrderOperate();
        workOrderOperate.setWoCode(woCode);
        workOrderOperate.setOperateCode(OperateCodeEnum.OP_ACCEPT.code());
        workOrderOperate.setTitle(OperateCodeEnum.OP_ACCEPT.message());
        workOrderOperate.setUserId(stationUserId);
        workOrderOperate.setCreateTime(date);
        workOrderOperate.setUpdateTime(date);
        // ???????????????????????????json?????????????????????
        Map<String, String> jsonMap = new LinkedHashMap<>(5);
        jsonMap.put("???????????????", stationUserId);
        jsonMap.put("???????????????", DateUtil.getDatePattern(date.getTime()));
        jsonMap.put("??????????????????", stationName);
        workOrderOperate.setTextJson(JsonUtil.toJson(jsonMap));
        // ??????????????????????????????????????????????????????????????????????????????
        workOrderOperate.setHiddenFlg(1);
        workOrderOperateMapper.insertSelective(workOrderOperate);
        log.info("[stationAcceptOrder]end");
    }
}
