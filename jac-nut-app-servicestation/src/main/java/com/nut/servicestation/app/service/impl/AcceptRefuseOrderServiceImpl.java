package com.nut.servicestation.app.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.TypeReference;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.pagehelper.Page;
import com.nut.common.assembler.ReverseGeoCodingClient;
import com.nut.common.constant.OperateCodeEnum;
import com.nut.common.constant.ServiceStationVal;
import com.nut.common.entity.DelayMessageEntity;
import com.nut.common.enums.ServiceStationEnum;
import com.nut.common.push.PushStaticLocarVal;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.PagingInfo;
import com.nut.common.result.Result;
import com.nut.common.utils.DateUtil;
import com.nut.common.utils.DelayingQueueComponent;
import com.nut.common.utils.JsonUtil;
import com.nut.common.utils.StringUtil;
import com.nut.servicestation.app.dao.*;
import com.nut.servicestation.app.domain.*;
import com.nut.servicestation.app.dto.LatLngDto;
import com.nut.servicestation.app.dto.UserInfoDto;
import com.nut.servicestation.app.dto.QueryWoListByStatusDto;
import com.nut.servicestation.app.form.*;
import com.nut.servicestation.app.pojo.QueryWoListByStatusPojo;
import com.nut.servicestation.app.service.*;
import com.nut.servicestation.common.assembler.BaiduMapComponent;
import com.nut.servicestation.common.assembler.NaviDistanceClient;
import com.nut.servicestation.common.constants.RefuseOrCloseEnum;
import com.nut.servicestation.common.utils.MathUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.Charset;
import java.util.*;

/*
 *  @author wuhaotian 2021/7/5
 */
@Slf4j
@Service("AcceptRefuseOrderService")
public class AcceptRefuseOrderServiceImpl extends ServiceStationBaseService implements AcceptRefuseOrderService {


    private static final String METERS = "METERS";
    private static final String MINUTES = "MINUTES";
    private static final String CUERANGE = "cueRange";
    private static final String CUETIME = "cueTime";
    @Value("${pointsToRedisKeyPrefix.one:196bba4fc}")
    private String pointsToRedisKeyPrefixOne;
    @Value("${pointsToRedisKeyPrefix.two:296bba4fc}")
    private String pointsToRedisKeyPrefixTwo;
    @Autowired
    private UserService userService;
    /**
     * ????????????????????????????????????
     */
    private static final int BYTE_LENGTH = 40;

    /**
     * ????????????????????????????????????
     */
    private static final int CHINESE_MAX = 20;

    /**
     * ???????????????????????????????????????
     */
    private static final String CHAR_SET = "GBK";
    @Autowired
    private WorkOrderDao workOrderMapper;
    @Autowired
    private RescueRoutePointDao rescueRoutePointMapper;
    @Autowired
    private WorkOrderTransferPartsDao workOrderTransferPartsMapper;
    @Autowired
    private NaviDistanceClient locationService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ReverseGeoCodingClient queryGeographicalService;
    @Autowired
    private WorkOrderOperateDao workOrderOperateMapper;
    @Autowired
    private WorkOrderOutDetailDao workOrderOutDetailMapper;
    @Autowired
    private CarDao carMapper;
    @Autowired
    private AsyPushMessageService asyPushMessageService;
    /**
     * ????????? ????????????????????????
     */
    @Value("${refuse_max_times:3}")
    private Integer refuseMaxTimes;

    /**
     * ???????????? ????????????????????????
     */
    @Value("${refuse_max_times_rescue:1}")
    private Integer refuseMaxTimesRescue;

    @Autowired
    private DelayingQueueComponent queueComponent;

    @Autowired
    private BaiduMapComponent baiduMapComponent;

    @Autowired
    private WarnInTheStationService warnInTheStationService;
    @Autowired
    private StartEndRepairService startEndRepairService;
    @Autowired
    private WoInfoService woInfoService;


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public HttpCommandResultWithData rescueGo(RescueGoForm command) throws Exception {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        // ????????????
        String errMsg = validateCommand(command);
        if (StringUtil.isNotEmpty(errMsg)) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage(errMsg);
            return result;
        }
        // ??????????????????
        UserInfoDto userInfoDto = userService.getUserInfoByUserId(command.getUserId(), false);
        if (userInfoDto == null) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("??????????????????");
            return result;
        }
        /**
         * 2021-05-17:?????????????????????????????????????????????????????????

         if (userInfoDto.getRoleCode() != ServiceStationVal.JOB_TYPE_SALESMAN) {
         result.setResultCode(ReturnCode.CLIENT_ERROR.code());
         result.setMessage("???????????????????????????");
         return result;
         }
         */
        Map<String, String> queryMap = new HashMap<>(2);
        queryMap.put("woCode", command.getWoCode());
        queryMap.put("stationId", userInfoDto.getServiceStationId());
        // ??????????????????
        WorkOrder workOrder = workOrderMapper.selectByWoCode(queryMap);
        if (workOrder == null) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("???????????????????????????");
            return result;
        }
        /**
         * 2021-05-24 ????????????????????????????????????????????????????????????????????????????????????
         */
        if (!command.getUserId().equals(workOrder.getAssignTo())) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("???????????????????????????????????????????????????");
            return result;
        }
        // ????????????
        errMsg = validateState(workOrder, command.getUserId(), userInfoDto.getServiceCode());
        if (StringUtil.isNotEmpty(errMsg)) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage(errMsg);
            return result;
        }
        // ????????????
        Date date = new Date();
        /* ????????????????????? */
        Map<String, Integer> outParam = new HashMap<>(4);
        insertRescueRoutePoint(workOrder, command, command.getUserId(), date, outParam);
        // ??????????????????????????????
        int actualMileage = outParam.get(METERS);
        // ??????????????????????????????
        int actualMinute = outParam.get(MINUTES);
        /* ???????????????????????? */
        Map<String, String> outParam2 = new HashMap<>(4);
        insertWorkOrderOperate(workOrder, command, command.getUserId(), date, actualMileage, actualMinute, outParam2);
        String cueRange = outParam2.get(CUERANGE);
        String cueTime = outParam2.get(CUETIME);
        /* ????????????????????? */
        updateWorkOrder(workOrder, command, date, cueTime);

        /* ?????????????????? */
        saveOutDetail(workOrder, command, date);

        /* ?????? */
        pushRescueTakeOff(workOrder, command.getUserId());
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());
        StringBuilder builder = new StringBuilder();
        builder.append("??????????????????");
        builder.append(cueRange);
        builder.append("km?????????");
        builder.append(cueTime);
        builder.append("?????????");
        result.setMessage(builder.toString());
        return result;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public HttpCommandResultWithData refuseOrder(RefuseOrderForm command) throws JsonProcessingException {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        // ??????????????????
        UserInfoDto userInfoDto = userService.getUserInfoByUserId(command.getUserId(), false);
        if (userInfoDto != null) {
            if (userInfoDto.getRoleCode() != ServiceStationVal.JOB_TYPE_ADMIN) {
                result.setResultCode(ECode.CLIENT_ERROR.code());
                result.setMessage("???????????????????????????");
                return result;
            }
            Map<String, String> queryMap = new HashMap<>(2);
            queryMap.put("woCode", command.getWoCode());
            queryMap.put("stationId", userInfoDto.getServiceStationId());
            // ??????????????????
            WorkOrder workOrder = workOrderMapper.selectByWoCode(queryMap);
            if (workOrder == null) {
                result.setResultCode(ECode.CLIENT_ERROR.code());
                result.setMessage("???????????????????????????");
                return result;
            }
            // ????????????????????????????????????
            if (workOrder.getWoStatus() != ServiceStationEnum.TO_BE_ACCEPTED.code()) {
                result.setResultCode(ECode.CLIENT_ERROR.code());
                result.setMessage("????????????????????????????????????");
                return result;
            }
            // ????????????????????????????????????
            int limit = 99;
            if (workOrder.getWoType() == ServiceStationVal.STATION_SERVICE) {
                limit = refuseMaxTimes;
            } else if (workOrder.getWoType() == ServiceStationVal.OUTSIDE_RESCUE) {
                limit = refuseMaxTimesRescue;
            }
            if (workOrder.getRefuseTimes() != null && workOrder.getRefuseTimes() >= limit) {
                result.setResultCode(ECode.CLIENT_ERROR.code());
                result.setMessage("?????????????????????????????????");
                return result;
            }
            // ????????????
            Date date = new Date();
            // ????????????????????????
            WorkOrderOperate workOrderOperate = new WorkOrderOperate();
            workOrderOperate.setWoCode(workOrder.getWoCode());
            workOrderOperate.setOperateCode(OperateCodeEnum.OP_REFUSE_APPLY.code());
            workOrderOperate.setTitle(OperateCodeEnum.OP_REFUSE_APPLY.message());
            workOrderOperate.setUserId(userInfoDto.getAccountId());
            workOrderOperate.setCreateTime(date);
            workOrderOperate.setUpdateTime(date);
            // ???????????????????????????json?????????????????????
            Map<String, String> jsonMap = new LinkedHashMap<>(5);
            jsonMap.put("???????????????", RefuseOrCloseEnum.getMessage(Integer.valueOf(command.getRefuseType())));
            jsonMap.put("???????????????", command.getRefuseReason());
            jsonMap.put("???????????????", userInfoDto.getAccountId());
            jsonMap.put("???????????????", DateUtil.getDatePattern(date.getTime()));
            jsonMap.put("??????????????????", workOrder.getStationName());
            workOrderOperate.setTextJson(JsonUtil.toJson(jsonMap));
            workOrderOperateMapper.insertSelective(workOrderOperate);
            // ?????????????????????
            WorkOrder updateWorkOrder = new WorkOrder();
            updateWorkOrder.setId(workOrder.getId());
            updateWorkOrder.setWoStatus(ServiceStationEnum.REFUSE_APPLYING.code());
            updateWorkOrder.setTimeApplyrefuse(date);
            updateWorkOrder.setRefuseType(Integer.parseInt(command.getRefuseType()));
            updateWorkOrder.setRefuseReason(command.getRefuseReason());
            updateWorkOrder.setUpdateTime(date);
            if (workOrder.getRefuseTimes() != null) {
                updateWorkOrder.setRefuseTimes(workOrder.getRefuseTimes() + 1);
            } else {
                updateWorkOrder.setRefuseTimes(1);
            }
            workOrderMapper.updateByPrimaryKeySelective(updateWorkOrder);

            // ???????????????????????????????????????????????????????????????????????????
            if (workOrder.getWoType() == ServiceStationVal.STATION_SERVICE) {
                // ????????????????????????ID
                String carId = carMapper.queryCarIdByVin(workOrder.getChassisNum());
                warnInTheStationService.updWarningStatus(carId, workOrder.getStationCode(), ServiceStationVal.INQ_REFUSE);
            }

            result.setResultCode(ECode.SUCCESS.code());
            result.setMessage(ECode.SUCCESS.message());
            Map<String, Object> data = new HashMap<>(3);
            int canRefuseCount = limit - updateWorkOrder.getRefuseTimes();
            data.put("canRefuseCount", canRefuseCount);
            result.setData(data);
            result.setMessage("???????????????????????????" + (canRefuseCount) + "??????????????????");
        } else {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("??????????????????");
        }
        return result;
    }

    @Override
    public HttpCommandResultWithData scanWo(ScanWoForm command) {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        // ??????????????????
        UserInfoDto userInfoDto = userService.getUserInfoByUserId(command.getUserId(), false);
        if (userInfoDto != null) {
            PagingInfo<QueryWoListByStatusDto> resultPageInfo = new PagingInfo<>();
            Map<String, String> queryMap = new HashMap<>(2);
            queryMap.put("stationId", userInfoDto.getServiceStationId());
            queryMap.put("chassisNum", command.getChassisNum());
            getPage(command, true);
            Page<QueryWoListByStatusPojo> pojoPageList = workOrderMapper.queryWorkOrderByVin(queryMap);
            /* dto?????? */
            List<QueryWoListByStatusDto> dtoList = new ArrayList<>();
            if (pojoPageList != null && pojoPageList.getResult() != null) {
                List<QueryWoListByStatusPojo> pojoList = pojoPageList.getResult();
                for (QueryWoListByStatusPojo pojo : pojoList) {
                    dtoList.add(pojoToDto(pojo));
                }
                resultPageInfo.setPage_total(pojoPageList.getPages());
                resultPageInfo.setTotal(pojoPageList.getTotal());
                resultPageInfo.setList(dtoList);
            }
            result.setData(resultPageInfo);
            result.setResultCode(ECode.SUCCESS.code());
            result.setMessage(ECode.SUCCESS.message());
        } else {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("??????????????????");
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public HttpCommandResultWithData acceptOrder(AcceptOrderForm command) throws JsonProcessingException {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        // ??????????????????
        UserInfoDto userInfoDto = userService.getUserInfoByUserId(command.getUserId(), false);
        if (userInfoDto != null) {
            if (userInfoDto.getRoleCode() != ServiceStationVal.JOB_TYPE_ADMIN) {
                result.setResultCode(ECode.CLIENT_ERROR.code());
                result.setMessage("???????????????????????????");
                return result;
            }
            //????????????????????????????????????????????????????????????????????????
            log.info("??????????????????");
            WorkOrder order = workOrderMapper.queryWorkOrderStation(command.getWoCode(), userInfoDto.getServiceStationId());
            if (order != null) {
                //?????????????????????????????????
                workOrderMapper.updateWorkOrderStationBind(order);
                //????????????????????????????????????????????????
                workOrderMapper.updateWorkOrderStationNoBind(order);
                //??????????????????
                workOrderMapper.updateWorkOrder(order);
                log.info("?????????????????? param:{}", order);
            }

            Map<String, String> queryMap = new HashMap<>(2);
            queryMap.put("woCode", command.getWoCode());
            queryMap.put("stationId", userInfoDto.getServiceStationId());
            // ??????????????????
            WorkOrder workOrder = workOrderMapper.selectByWoCode(queryMap);
            if (workOrder == null) {
                result.setResultCode(ECode.CLIENT_ERROR.code());
                result.setMessage("???????????????????????????");
                return result;
            }
            // ????????????????????????????????????
            if (workOrder.getWoStatus() != ServiceStationEnum.TO_BE_ACCEPTED.code()) {
                result.setResultCode(ECode.CLIENT_ERROR.code());
                result.setMessage("????????????????????????????????????");
                return result;
            }
            UserInfoDto assignUserInfo = null;
            // ??????????????????????????????????????????????????????ID????????????
            if (workOrder.getWoType() == ServiceStationVal.OUTSIDE_RESCUE) {
                if (StringUtil.isEmpty(command.getAssignTo())) {
                    result.setResultCode(ECode.CLIENT_ERROR.code());
                    result.setMessage("?????????????????????");
                    return result;
                } else {
                    assignUserInfo = userService.getUserInfoByUserId(command.getAssignTo(), false);
                    if (assignUserInfo != null) {
                        if (!assignUserInfo.getServiceStationId().equals(workOrder.getStationId())) {
                            result.setResultCode(ECode.CLIENT_ERROR.code());
                            result.setMessage("?????????????????????????????????????????????");
                            return result;
                        }
                        /**
                         * 2021-05-24 ???????????????????????????????????????
                         */
//                        if (assignUserInfo.getRoleCode() != ServiceStationVal.JOB_TYPE_SALESMAN) {
//                            result.setResultCode(ReturnCode.CLIENT_ERROR.code());
//                            result.setMessage("??????????????????????????????");
//                            return result;
//                        }
                    } else {
                        result.setResultCode(ECode.CLIENT_ERROR.code());
                        result.setMessage("???????????????????????????");
                        return result;
                    }
                }
            }
            // ????????????
            Date date = new Date();
            // ????????????????????????
            WorkOrderOperate workOrderOperate = new WorkOrderOperate();
            workOrderOperate.setWoCode(workOrder.getWoCode());
            workOrderOperate.setOperateCode(OperateCodeEnum.OP_ACCEPT.code());
            workOrderOperate.setTitle(OperateCodeEnum.OP_ACCEPT.message());
            workOrderOperate.setUserId(userInfoDto.getAccountId());
            workOrderOperate.setCreateTime(date);
            workOrderOperate.setUpdateTime(date);
            // ???????????????????????????json?????????????????????
            Map<String, String> jsonMap = new LinkedHashMap<>(6);
            if (workOrder.getWoType() == ServiceStationVal.OUTSIDE_RESCUE && assignUserInfo != null) {
                jsonMap.put("???????????????", assignUserInfo.getAccountId());
            }
            jsonMap.put("???????????????", userInfoDto.getAccountId());
            jsonMap.put("???????????????", DateUtil.getDatePattern(date.getTime()));
            jsonMap.put("??????????????????", workOrder.getStationName());
            workOrderOperate.setTextJson(JsonUtil.toJson(jsonMap));
            workOrderOperateMapper.insertSelective(workOrderOperate);
            // ?????????????????????
            WorkOrder updateWorkOrder = new WorkOrder();
            updateWorkOrder.setId(workOrder.getId());
            updateWorkOrder.setTimeAccept(date);
            if (workOrder.getWoType() == ServiceStationVal.STATION_SERVICE) {
                updateWorkOrder.setWoStatus(ServiceStationEnum.TO_RECEIVE.code());
            } else if (workOrder.getWoType() == ServiceStationVal.OUTSIDE_RESCUE) {
                updateWorkOrder.setWoStatus(ServiceStationEnum.TO_TAKE_OFF.code());
                if (assignUserInfo != null) {
                    updateWorkOrder.setAssignTo(assignUserInfo.getAccountId());
                }
            }
            updateWorkOrder.setUpdateTime(date);
            workOrderMapper.updateByPrimaryKeySelective(updateWorkOrder);

            // ???????????????????????????????????????????????????????????????????????????
            if (workOrder.getWoType() == ServiceStationVal.STATION_SERVICE) {
                // ????????????????????????ID
                String carId = carMapper.queryCarIdByVin(workOrder.getChassisNum());
                warnInTheStationService.updWarningStatus(carId, workOrder.getStationCode(), ServiceStationVal.INQ_ACCEPT);
            }

            /* ?????? */
            pushAcceptOrder(workOrder, command.getUserId());

            /* ?????????????????? */
            startEndRepairService.trySendKafka(workOrder.getWoCode(), ServiceStationVal.WEB_SERVICE_CREATESMORDER, "????????????");
            //todo ???????????????????????????????????????
      /*      //????????????????????????
            List<DelayMessageEntity> messageEntities = queueComponent.pull();
            messageEntities.forEach(msg ->{
                //??????????????????
                DelayMessageEntity.MessageBody messageBody = JsonUtil.fromJson(msg.getBody(), DelayMessageEntity.MessageBody.class);
                //???????????????
                Map map = JsonUtil.fromJson(messageBody.getMessageExtra(), Map.class);
                //?????????????????????Map<String, Object>
                Map<String, Object> convert = Convert.convert(new TypeReference<Map<String, Object>>() {}, map);
                //???????????????
                String woCode = String.valueOf(convert.get(PushStaticLocarVal.MESSAGE_EXTRA_WO_CODE));
                //????????????????????????????????????????????????????????????????????????
                if (StringUtils.isNotBlank(woCode)){
                    if (workOrder.getWoCode().equals(woCode)){
                        queueComponent.remove(msg);
                    }
                }
            });*/

            result.setResultCode(ECode.SUCCESS.code());
            result.setMessage(ECode.SUCCESS.message());
        } else {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("??????????????????");
        }
        return result;
    }

    @Override
    public HttpCommandResultWithData scanOrder(ScanOrderForm form) {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        // ??????????????????
        UserInfoDto userInfoDto = userService.getUserInfoByUserId(form.getUserId(), false);
        if (userInfoDto != null) {
            String vin = form.getChassisNum();  // ?????????
            String assignTo = userInfoDto.getAccountId();  // ????????????
            String stationId = userInfoDto.getServiceStationId();  // ?????????ID
            List<Map<String, Object>> list1 = new ArrayList<>();
            List<Map<String, Object>> list2 = new ArrayList<>();
            Integer roleCode = userInfoDto.getRoleCode();
            List<Map<String, Object>> listRuning = workOrderMapper.queryWoCodeByVin(vin, stationId);
            if (roleCode == 1) {
                // ?????????
                if (listRuning.size() > 0) {
                    for (Map<String, Object> map : listRuning) {
                        // ????????????-???????????????????????????????????????
                        String woCode = map.get("woStatus").toString();
                        // ??????????????????
                        if (woCode.equals("100") || woCode.equals("140") || woCode.equals("220")) {
                            list1.add(map);  // ???????????????????????????
                            continue;
                        }
                        // ??????????????????
                        Object name = map.get("assignTo");
                        if (name == null) {
                            list2.add(map); // ???????????????????????????
                            continue;
                        }
                        if (assignTo.equals(name.toString())) {
                            list1.add(map);  // ???????????????????????????
                            continue;
                        } else {
                            list2.add(map);
                            continue;
                        }
                    }
                    //  ??????????????????????????????????????????1???????????????????????????????????????
                    if (list1.size() >= 1) {
                        String woCode = list1.get(0).get("woCode").toString();
                        log.info("??????????????????????????????{}", woCode);
                        // ??????????????????
                        List<QueryWoListByStatusPojo> list = new ArrayList<>();
                        QueryWoListByStatusPojo pojo = workOrderMapper.queryWorkOrderByWoCodeNew(woCode);
                        list.add(pojo);
                        Map<String, Object> map = new HashMap<>();
                        map.put("numberType", 1);
                        map.put("list", list);
                        result.setData(map);
                        return result;
                    } else {
                        // ???????????????????????????????????????????????????????????????
                        // ??????????????????
                        List<QueryWoListByStatusPojo> list = new ArrayList<>();
                        for (Map<String, Object> map : list2) {
                            String woCode = map.get("woCode").toString();
                            QueryWoListByStatusPojo pojo = workOrderMapper.queryWorkOrderByWoCodeNew(woCode);
                            list.add(pojo);
                        }
                        Map<String, Object> map = new HashMap<>();
                        map.put("numberType", 2);
                        map.put("list", list);
                        result.setData(map);
                        return result;
                    }
                } else {
                    // ???????????????????????????????????????
                    List<Map<String, Object>> listNotRuning = workOrderMapper.queryWoCodeByVin2(vin, stationId);
                    if (listNotRuning.size() != 0) {
                        // ????????????????????????????????????
                        List<QueryWoListByStatusPojo> list = new ArrayList<>();
                        for (Map<String, Object> map : listNotRuning) {
                            String woCode = map.get("woCode").toString();
                            QueryWoListByStatusPojo pojo = workOrderMapper.queryWorkOrderByWoCodeNew(woCode);
                            list.add(pojo);
                        }
                        Map<String, Object> map = new HashMap<>();
                        map.put("numberType", 3);
                        map.put("list", list);
                        result.setData(map);
                        return result;
                    } else {
                        // ???????????????????????????????????????
                        Map<String, Object> map = new HashMap<>();
                        map.put("numberType", 0);
                        map.put("list", null);
                        result.setData(map);
                        result.setMessage("????????????");
                        return result;
                    }
                }
            } else if (roleCode == 2) {
                if (listRuning.size() > 0) {
                    for (Map<String, Object> map : listRuning) {
                        // ????????????-???????????????????????????????????????
                        // ??????????????????
                        Object name = map.get("assignTo");
                        if (name == null) {
                            continue;
                        }
                        if (assignTo.equals(name.toString())) {
                            list1.add(map);  // ???????????????????????????
                            continue;
                        }
                    }
                    //  ??????????????????????????????????????????1???????????????????????????????????????
                    if (list1.size() >= 1) {
                        String woCode = list1.get(0).get("woCode").toString();
                        log.info("??????????????????????????????{}", woCode);
                        // ??????????????????
                        List<QueryWoListByStatusPojo> list = new ArrayList<>();
                        QueryWoListByStatusPojo pojo = workOrderMapper.queryWorkOrderByWoCodeNew(woCode);
                        Map<String, Object> map = new HashMap<>();
                        list.add(pojo);
                        map.put("numberType", 1);
                        map.put("list", list);
                        result.setData(map);
                        return result;
                    }
                } else {
                    // ???????????????????????????????????????
                    List<Map<String, Object>> listNotRuning = workOrderMapper.queryWoCodeByVin2(vin, stationId);
                    if (listNotRuning.size() != 0) {
                        // ????????????????????????????????????
                        List<QueryWoListByStatusPojo> list = new ArrayList<>();
                        for (Map<String, Object> map : listNotRuning) {
                            String woCode = map.get("woCode").toString();
                            String woStatus = map.get("woStatus").toString();
                            Object name = map.get("assignTo");
                            if (name == null) {
                                continue;
                            }
                            if (name.toString().equals(assignTo) && !woStatus.equals("240") && !woStatus.equals("250")) {
                                QueryWoListByStatusPojo pojo = workOrderMapper.queryWorkOrderByWoCodeNew(woCode);
                                list.add(pojo);
                            }
                        }
                        Map<String, Object> map = new HashMap<>();
                        map.put("numberType", 3);
                        map.put("list", list);
                        result.setData(map);
                        return result;
                    } else {
                        // ???????????????????????????????????????
                        Map<String, Object> map = new HashMap<>();
                        map.put("numberType", 0);
                        map.put("list", null);
                        result.setData(map);
                        result.setMessage("????????????");
                        return result;
                    }
                }
            }
        } else {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("??????????????????");
        }

        return result;
    }

    /**
     * ????????????
     */
    private void pushAcceptOrder(WorkOrder workOrder, String senderId) {
        log.info("[pushAcceptOrder]start");
        try {
            // ???????????????
            Map<String, String> wildcardMap = new HashMap<>(7);
            String chassisNum = workOrder.getChassisNum();
            String carNumber = carMapper.queryCarNumberByVin(chassisNum);
            wildcardMap.put("{???????????????}", StringUtil.isNotEmpty(carNumber) ? carNumber : chassisNum);
            wildcardMap.put("{?????????}", workOrder.getWoCode());
            String stype;
            if (ServiceStationVal.STATION_SERVICE == workOrder.getWoType()) {
                // ?????????
                wildcardMap.put("{????????????}", DateUtil.formatDate(DateUtil.time_pattern, workOrder.getAppoArriveTime()));
                wildcardMap.put("{???????????????}", workOrder.getStationName());
                stype = PushStaticLocarVal.PUSH_TYPE_TEN_STYPE_ACCEPT_IN;
            } else {
                // ????????????
                stype = PushStaticLocarVal.PUSH_TYPE_TEN_STYPE_ACCEPT_OUT;
            }
            String wildcard = JsonUtil.toJson(wildcardMap);
            // ????????????
            Map<String, String> messageExtraMap = new HashMap<>(3);
            // ?????????
            messageExtraMap.put(PushStaticLocarVal.MESSAGE_EXTRA_WO_CODE, workOrder.getWoCode());
            String messageExtra = JsonUtil.toJson(messageExtraMap);


            asyPushMessageService.pushToDriverAndOwner(stype, wildcard, messageExtra, senderId, null, chassisNum);
        } catch (Exception e) {
            log.info("[pushAcceptOrder]Exception:", e);
        }
        log.info("[pushAcceptOrder]end");
    }

    /**
     * ??????pojo???dto
     *
     * @param pojo ??????pojo
     * @return QueryWoListByStatusDto ????????????dto
     */
    private static QueryWoListByStatusDto pojoToDto(QueryWoListByStatusPojo pojo) {
        QueryWoListByStatusDto dto = new QueryWoListByStatusDto();
        dto.setWoStatus(StringUtil.valueOf(pojo.getWoStatus()));
        String vin = pojo.getChassisNum();
        int length = 8;
        if (vin.length() > length) {
            dto.setChassisNum(vin.substring(vin.length() - length));
        } else {
            dto.setChassisNum(vin);
        }
        dto.setCarNumber(pojo.getCarNumber());
        dto.setWoCode(pojo.getWoCode());
        dto.setCarLocation(pojo.getCarLocation());
        dto.setCarLon(pojo.getCarLon());
        dto.setCarLat(pojo.getCarLat());
        dto.setWoType(pojo.getWoType());
        dto.setAppointmentType(pojo.getAppointmentType());
        if (StringUtil.isNotEmpty(pojo.getMaintainItem()) && StringUtil.isNotEmpty(pojo.getRepairItem())) {
            dto.setAppointmentItem(3);
        } else {
            if (StringUtil.isNotEmpty(pojo.getMaintainItem())) {
                dto.setAppointmentItem(2);
            }
            if (StringUtil.isNotEmpty(pojo.getRepairItem())) {
                dto.setAppointmentItem(1);
            }
        }
        dto.setWorkTime(getTimeByStatus(pojo));
        dto.setRescueType(pojo.getRescueType());
        dto.setWoStatusValue(pojo.getWoStatusValue());
        // ????????????
        dto.setAssignTo(pojo.getAssignTo());
//        dto.setProtocolMark(pojo.getProtocolMark());
        return dto;
    }

    /**
     * ???????????????????????????????????????????????????
     * <h2>?????????????????????????????????????????????????????????????????????????????????tag: ATTENTION_add_new_woStatus_DOUBLE_CHECK</h2>
     *
     * @param pojo ??????pojo
     * @return String ????????????
     */
    private static String getTimeByStatus(QueryWoListByStatusPojo pojo) {
        String time = null;
        int status = pojo.getWoStatus();
        if (ServiceStationEnum.TO_BE_ACCEPTED.code() == status
                || ServiceStationEnum.REFUSE_APPLYING.code() == status
                || ServiceStationEnum.MODIFY_APPLYING_ACCEPT.code() == status) {
            // ????????????
            time = DateUtil.formatDate(DateUtil.time_pattern, pojo.getTimeCreate());

        } else if (ServiceStationEnum.TO_TAKE_OFF.code() == status
                || ServiceStationEnum.MODIFY_APPLYING_TAKEOFF.code() == status
                || ServiceStationEnum.CLOSE_APPLYING_TAKEOFF.code() == status) {
            // ????????????
            time = DateUtil.formatDate(DateUtil.time_pattern, pojo.getTimeAccept());

        } else if (ServiceStationEnum.TO_RECEIVE.code() == status
                || ServiceStationEnum.MODIFY_APPLYING_RECEIVE.code() == status
                || ServiceStationEnum.CLOSE_APPLYING_RECEIVE.code() == status) {
            if (ServiceStationVal.STATION_SERVICE == pojo.getWoType()) {
                // ????????????
                time = DateUtil.formatDate(DateUtil.time_pattern, pojo.getTimeAccept());
            } else if (ServiceStationVal.OUTSIDE_RESCUE == pojo.getWoType()) {
                // ????????????
                time = DateUtil.formatDate(DateUtil.time_pattern, pojo.getTimeDepart());
            }

        } else if (ServiceStationEnum.CLOSE_APPLYING_INSPECT.code() == status
                || ServiceStationEnum.INSPECTING.code() == status) {
            // ????????????
            time = DateUtil.formatDate(DateUtil.time_pattern, pojo.getTimeReceive());

        } else if (ServiceStationEnum.CLOSE_APPLYING_REPAIR.code() == status
                || ServiceStationEnum.REPAIRING.code() == status) {
            // ??????????????????
            time = DateUtil.formatDate(DateUtil.time_pattern, pojo.getTimeInspected());

        } else if (ServiceStationEnum.CLOSE_REFUSED.code() == status
                || ServiceStationEnum.CLOSE_INSPECT.code() == status
                || ServiceStationEnum.CLOSE_REPAIR.code() == status
                || ServiceStationEnum.CLOSE_RESCUE.code() == status
                || ServiceStationEnum.WORK_DONE.code() == status
                || ServiceStationEnum.EVALUATED.code() == status
                || ServiceStationEnum.CLOSE_TAKEOFF.code() == status
                || ServiceStationEnum.CLOSE_RECEIVE.code() == status) {
            // ????????????
            time = DateUtil.formatDate(DateUtil.time_pattern, pojo.getTimeClose());
        }
        return time;
    }

    /**
     * ????????????
     */
    private String validateCommand(RescueGoForm command) {
        // ????????????????????????????????????????????????20???????????????40????????????
        String name = command.getName();
        if (StringUtil.isNotEmpty(name)) {
            if (name.getBytes(Charset.forName(CHAR_SET)).length > BYTE_LENGTH) {
                return "???????????????20???????????????40????????????";
            } else {
                if (StringUtil.findNotAscill(name) > CHINESE_MAX) {
                    return "???????????????20???????????????40????????????";
                }
            }
        }
        return "";
    }

    /**
     * ????????????
     */
    private String validateState(WorkOrder workOrder, String userId, String stationCode) {
        // ????????????????????????????????????
        if (workOrder.getWoStatus() != ServiceStationEnum.TO_TAKE_OFF.code()) {
            return "????????????????????????????????????";
        }
        // ???????????????????????????????????????????????????
        List<WorkOrderTransferParts> transferParts = workOrderTransferPartsMapper.selectByWoCodeAndOperateId(workOrder.getWoCode(), null);
        if (transferParts.size() > 0) {
            return "??????????????????????????????????????????";
        }
        // ?????????????????????????????????????????????????????????????????????
        if (rescueRoutePointMapper.selectByWoCode(workOrder.getWoCode()) != null) {
            return "????????????????????????";
        }
        // ?????????????????????????????????????????????
        List<Integer> woStatusList = Arrays.asList(ServiceStationEnum.MODIFY_APPLYING_RECEIVE.code(),
                ServiceStationEnum.TO_RECEIVE.code(), ServiceStationEnum.INSPECTING.code(),
                ServiceStationEnum.CLOSE_APPLYING_INSPECT.code(), ServiceStationEnum.REPAIRING.code(),
                ServiceStationEnum.CLOSE_APPLYING_REPAIR.code(), ServiceStationEnum.CLOSE_APPLYING_RECEIVE.code());
        Long woCount = workOrderMapper.countAlreadyDepartWo(userId, stationCode, ServiceStationVal.OUTSIDE_RESCUE, woStatusList);
        if (woCount != null && woCount > 0) {
            return "????????????????????????????????????????????????????????????????????????";
        }
        return "";
    }

    /**
     * ?????????????????????
     */
    private void insertRescueRoutePoint(WorkOrder workOrder, RescueGoForm command, String userId, Date date,
                                        Map<String, Integer> outParam) throws JsonProcessingException {
        log.info("[insertRescueRoutePoint]start");
        RescueRoutePoint rescueRoutePoint = new RescueRoutePoint();
        rescueRoutePoint.setWoCode(workOrder.getWoCode());
        rescueRoutePoint.setUserId(userId);
        rescueRoutePoint.setCreateTime(date);
        rescueRoutePoint.setUpdateTime(date);
        // ?????????????????????
        String carLon = workOrder.getCarLon();
        String carLat = workOrder.getCarLat();
        // ??????????????????????????????
        int actualMileage = 0;
        // ??????????????????????????????
        int actualMinute = 0;
        if (StringUtil.isNotEmpty(carLon) && StringUtil.isNotEmpty(carLat)) {
            JSONObject jsonObject = baiduMapComponent.distance2point(
                    command.getLat(), command.getLon(), carLat, carLon, "bd09ll")
                    .getJSONObject("result");
            String distance = jsonObject.getJSONArray("routes").getJSONObject(0).get("distance").toString();
            log.info("???????????????{}", distance);
            actualMileage = Integer.parseInt(distance);
//            actualMileage = locationService.getDistance(command.getLon(), command.getLat(), carLon, carLat);
            rescueRoutePoint.setEstimateMileage(actualMileage);
            actualMinute = MathUtil.scale((double) actualMileage / 1000 / 40 * 60, 0).intValue();
            rescueRoutePoint.setEstimateDuration(actualMinute < 1 ? 1 : actualMinute);
        } else {
            rescueRoutePoint.setEstimateMileage(0);
            rescueRoutePoint.setEstimateDuration(0);
        }
        rescueRoutePoint.setMileage(0);
        rescueRoutePoint.setDuration(0);
        // ????????????json?????????????????????
        LatLngDto latLngDto = new LatLngDto();
        // ?????????????????????index:1
        latLngDto.setIndex("1");
        latLngDto.setLongitude(Double.parseDouble(command.getLon()));
        latLngDto.setLatitude(Double.parseDouble(command.getLat()));
        latLngDto.setTime(String.valueOf(date.getTime()));
        latLngDto.setRadius(1.0);
        List<LatLngDto> list = new ArrayList<>();
        list.add(latLngDto);

        String redisKey = "";
        Integer timesRescueNumber = 1;
        int times = 1;
        if (null != workOrder.getTimesRescueNumber()) {
            timesRescueNumber = workOrder.getTimesRescueNumber();
        }
        if (timesRescueNumber > 1) {
            log.info("?????????{}???????????????????????????", workOrder.getWoCode());
            redisKey = pointsToRedisKeyPrefixTwo + workOrder.getWoCode();
        } else {
            log.info("?????????{}???????????????????????????", workOrder.getWoCode());
            redisKey = pointsToRedisKeyPrefixOne + workOrder.getWoCode();
        }

        redisTemplate.opsForList().rightPush(redisKey, JsonUtil.toJson(latLngDto));

        rescueRoutePoint.setOriginalPoints(JsonUtil.toJson(list));
        rescueRoutePoint.setPoints(rescueRoutePoint.getOriginalPoints());
        rescueRoutePointMapper.insertSelective(rescueRoutePoint);

        if (outParam != null) {
            outParam.put(METERS, actualMileage);
            outParam.put(MINUTES, actualMinute);
        }
        log.info("[insertRescueRoutePoint]end");
    }

    /**
     * ????????????????????????
     */
    private void insertWorkOrderOperate(WorkOrder workOrder, RescueGoForm command, String userId, Date date,
                                        int actualMileage, int actualMinute, Map<String, String> outParam) throws JsonProcessingException {
        log.info("[insertWorkOrderOperate]start");
        WorkOrderOperate workOrderOperate = new WorkOrderOperate();
        workOrderOperate.setWoCode(workOrder.getWoCode());
        workOrderOperate.setOperateCode(OperateCodeEnum.OP_TAKE_OFF.code());
        workOrderOperate.setTitle(OperateCodeEnum.OP_TAKE_OFF.message());
        workOrderOperate.setLongitude(command.getLon());
        workOrderOperate.setLatitude(command.getLat());
        workOrderOperate.setUserId(userId);
        workOrderOperate.setCreateTime(date);
        workOrderOperate.setUpdateTime(date);
        workOrderOperate.setOperateId(command.getOperateId());
        workOrderOperate.setPhotoNum(command.getPhotoNum());
        // ???????????????????????????json?????????????????????
        Map<String, String> jsonMap = new LinkedHashMap<>(16);
        String cueRange = StringUtil.valueOf(MathUtil.scale((double) actualMileage / 1000, 1).doubleValue());
        String cueTime = DateUtil.getDatePattern(date.getTime() + actualMinute * 60 * 1000);
        jsonMap.put("???????????????", command.getName());
        jsonMap.put("?????????????????????", command.getPhone());
        jsonMap.put("???????????????", command.getNum());
        jsonMap.put("GPS????????????", StringUtil.isEmpty(command.getGpsDeviceNo()) ? "???GPS??????" : command.getGpsDeviceNo());
        jsonMap.put("?????????????????????", cueRange + "km");
        jsonMap.put("?????????????????????", cueTime);
        jsonMap.put("???????????????", userId);
        jsonMap.put("???????????????", DateUtil.getDatePattern(date.getTime()));
        jsonMap.put("???????????????", queryGeographicalService.getPosition(command.getLat(), command.getLon()));
        workOrderOperate.setTextJson(JsonUtil.toJson(jsonMap));
        workOrderOperateMapper.insertSelective(workOrderOperate);

        if (outParam != null) {
            outParam.put(CUERANGE, cueRange);
            outParam.put(CUETIME, cueTime);
        }
        log.info("[insertWorkOrderOperate]end");
    }

    /**
     * ??????????????????
     */
    private void updateWorkOrder(WorkOrder workOrder, RescueGoForm command, Date date, String cueTime) {
        log.info("[updateWorkOrder]start");
        WorkOrder updateWorkOrder = new WorkOrder();
        updateWorkOrder.setId(workOrder.getId());
        updateWorkOrder.setWoStatus(ServiceStationEnum.TO_RECEIVE.code());
        updateWorkOrder.setTimeDepart(date);
        updateWorkOrder.setTimeArriveExpected(DateUtil.parseDate(cueTime, DateUtil.TIME_PATTERN_MINUTE));
        updateWorkOrder.setRescueStaffName(command.getName());
        updateWorkOrder.setRescueStaffPhone(command.getPhone());
        updateWorkOrder.setRescueStaffNum(Integer.parseInt(command.getNum()));
        if (StringUtil.isNotEmpty(command.getGpsDeviceNo())) {
            updateWorkOrder.setRescueCarDevice(command.getGpsDeviceNo());
        }
        if (StringUtil.isNotEmpty(command.getDeviceId())) {
            updateWorkOrder.setDeviceId(command.getDeviceId());
        }
        // ??????????????????????????????
        updateWorkOrder.setPointCompleteness(2);
        updateWorkOrder.setPointCompletenessTwo(2);
        updateWorkOrder.setUpdateTime(date);
        workOrderMapper.updateByPrimaryKeySelective(updateWorkOrder);
        log.info("[updateWorkOrder]end");
    }

    private void saveOutDetail(WorkOrder workOrder, RescueGoForm command, Date date) {
        WorkOrderOutDetail wood = this.workOrderOutDetailMapper.selectByWoCode(workOrder.getWoCode());
        boolean isInsert = false;
        if (wood == null) {
            wood = new WorkOrderOutDetail();
            wood.setWoCode(workOrder.getWoCode());
            wood.setOutTimes(1);
            isInsert = true;
        }

        if (wood.getOutTimes() == 1) {
            wood.setsOutDate(date);
            wood.setStartAddress(command.getStartAddress());
            wood.setStartMileage(Float.valueOf(command.getStartMileage()));
            wood.setServiceLicense(command.getGpsDeviceNo());
        } else if (wood.getOutTimes() == 2) {
            wood.setTwiceSOutDate(date);
            wood.setTwiceStartAddress(command.getStartAddress());
            wood.setTwiceStartMileage(Float.valueOf(command.getStartMileage()));
            wood.setTwiceServiceLicense(command.getGpsDeviceNo());
        }

        if (isInsert) {
            this.workOrderOutDetailMapper.insert(wood);
        } else {
            this.workOrderOutDetailMapper.updateByPrimaryKey(wood);
        }
    }

    /**
     * ??????
     */
    private void pushRescueTakeOff(WorkOrder workOrder, String senderId) {
        log.info("[pushRescueTakeOff]start");
        try {
            // ???????????????
            Map<String, String> wildcardMap = new HashMap<>(4);
            String chassisNum = workOrder.getChassisNum();
            String carNumber = carMapper.queryCarNumberByVin(chassisNum);
            wildcardMap.put("{???????????????}", StringUtil.isNotEmpty(carNumber) ? carNumber : chassisNum);
            wildcardMap.put("{?????????}", workOrder.getWoCode());
            String wildcard = JsonUtil.toJson(wildcardMap);
            // ????????????
            Map<String, String> messageExtraMap = new HashMap<>(3);
            // ?????????
            messageExtraMap.put(PushStaticLocarVal.MESSAGE_EXTRA_WO_CODE, workOrder.getWoCode());
            String messageExtra = JsonUtil.toJson(messageExtraMap);
            asyPushMessageService.pushToDriverAndOwner(PushStaticLocarVal.PUSH_TYPE_TEN_STYPE_RESCUE_TAKE_OFF, wildcard,
                    messageExtra, senderId, null, chassisNum);
        } catch (Exception e) {
            log.info("[pushRescueTakeOff]Exception", e);
        }
        log.info("[pushRescueTakeOff]end");
    }


}
