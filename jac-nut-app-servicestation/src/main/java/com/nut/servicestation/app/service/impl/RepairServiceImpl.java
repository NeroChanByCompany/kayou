package com.nut.servicestation.app.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.pagehelper.Page;
import com.nut.common.constant.OperateCodeEnum;
import com.nut.common.constant.ServiceStationVal;
import com.nut.common.enums.ServiceStationEnum;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.utils.DateUtil;
import com.nut.common.utils.JsonUtil;
import com.nut.common.utils.StringUtil;
import com.nut.servicestation.app.dao.*;
import com.nut.servicestation.app.domain.*;
import com.nut.servicestation.app.dto.*;
import com.nut.servicestation.app.form.*;
import com.nut.servicestation.app.pojo.RepairRecordDetailPojo;
import com.nut.servicestation.app.service.RepairService;
import com.nut.servicestation.app.service.RescueTransferPartsService;
import com.nut.servicestation.app.service.UptimeService;
import com.nut.servicestation.app.service.UserService;
import com.nut.servicestation.common.constants.ChargeTypeEnum;
import com.nut.servicestation.common.constants.ServiceTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

/*
 *  @author wuhaotian 2021/7/2
 */
@Slf4j
@Service("RepairService")
public class RepairServiceImpl extends ServiceStationBaseService implements RepairService {


    private static final String VALIDATE_FLG = "validateFlg";
    private static final String WO_CODE = "woCode";
    private static final String WORK_ORDER = "workOrder";
    private static final String YES = "1";

    @Autowired
    private WorkOrderOperateDao workOrderOperateMapper;

    @Autowired
    private WorkOrderDao workOrderMapper;

    @Autowired
    private UserService queryUserInfoService;

    @Autowired
    private CarDao carMapper;

    @Autowired
    private WorkOrderTransferPartsDao workOrderTransferPartsMapper;

    @Autowired
    private AccessoriesCodeDao accessoriesCodeMapper;

    @Autowired
    private UptimeService uptimeService;

    @Autowired
    private DataDictDao dataDictMapper;

    @Value("${times_rescue_max_times:2}")
    private int timesRescueMaxTimes;

    @Autowired
    private WorkOrderOutDetailDao workOrderOutDetailMapper;

    @Autowired
    private RescueRoutePointDao rescueRoutePointMapper;

    @Autowired
    private RescueRoutePointHistoryDao rescueRoutePointHistoryMapper;

    @Autowired
    private WorkOrderOvertimeDao workOrderOvertimeMapper;

    @Autowired
    private WorkOrderHistoryDao workOrderHistoryMapper;

    @Autowired
    private RescueTransferPartsService rescueTransferPartsService;



    @Override
    public HttpCommandResultWithData repairRecords(RepairRecordsForm command) {
        log.info("****************repairRecords Start*****************");
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());
        /* 工单信息验证 */
        Map<String, Object> valueMap = repairRecordWorkCheck(result, command.getUserId(), command.getWoCode(), true);
        if ((Boolean) valueMap.get(VALIDATE_FLG)) {
            return result;
        }
        WorkOrder workOrder = (WorkOrder) valueMap.get(WORK_ORDER);
        /* 设置工单信息 */
        RepairRecordsListDto dto = setWorkOrderInfo(workOrder);
        /* 维修项查询 */
        Map<String, Object> param2 = new HashMap<>(3);
        param2.put(WO_CODE, command.getWoCode());
        param2.put("operateCode", OperateCodeEnum.OP_REPAIR.code());
        param2.put("status", ServiceStationVal.TRANS_FINISH);
        getPage(command, true);
        Page<RepairRecordsDto> pageList = workOrderOperateMapper.selectByWoCode(param2);
        if (pageList != null) {
            dto.setList(pageList.getResult());
            dto.setPage_total(pageList.getPages());
            dto.setTotal(pageList.getTotal());
        }
        // 断联维修提示
        Integer boxRepairAlert = workOrder.getTboxRepairAlert();
        if (boxRepairAlert != null) {
            dto.setShowDisConnection(boxRepairAlert);
        } else {
            if (workOrder.getTboxConnectStatus() != null && workOrder.getTboxRepairRecord() != null &&
                    workOrder.getWoType() == ServiceStationVal.STATION_SERVICE
                    && workOrder.getTboxConnectStatus() == 1 && workOrder.getTboxRepairRecord() == 1) {
                // 提示
                dto.setShowDisConnection(1);
                WorkOrder upWorkOrder = new WorkOrder();
                upWorkOrder.setId(workOrder.getId());
                upWorkOrder.setTboxRepairAlert(0);
                workOrderMapper.updateByPrimaryKeySelective(upWorkOrder);
            } else {
                // 不提示
                dto.setShowDisConnection(0);
            }
        }
        result.setData(dto);
        log.info("****************repairRecords End*****************");
        return result;
    }

    @Override
    @Transactional(timeout = 180, rollbackFor = Exception.class)
    public HttpCommandResultWithData repairRecordSave(RepairRecordSaveForm command) throws JsonProcessingException {
        log.info("[repairRecordSave]start");
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());
        // 自动设置服务类型/费用类型/处理方式/付费方式
        setCommand(command);
        /* 工单信息验证 */
        Map<String, Object> valueMap = repairRecordWorkCheck(result, command.getUserId(), command.getWoCode(), false);
        if ((Boolean) valueMap.get(VALIDATE_FLG)) {
            return result;
        }
        WorkOrder workOrder = (WorkOrder) valueMap.get(WORK_ORDER);

        /* 首保判断 */
        if ("8".equals(command.getServiceType())) {
            Long firstCnt = workOrderMapper.queryFirstCountByChuNum(workOrder.getChassisNum());
            if (firstCnt != null && firstCnt.longValue() > 0) {
                result.setResultCode(ECode.CLIENT_ERROR.code());
                result.setMessage("该车辆已执行过首保！");
                return result;
            }
        }

        int rescueTypeInt = 1;
        if (workOrder.getRescueType() != null) {
            rescueTypeInt = workOrder.getRescueType();
        }

        // 校验入参之间的必填/非必填关系
        String errorMsg = validateCommand(command, workOrder.getWoType(), rescueTypeInt);
        if (StringUtil.isNotEmpty(errorMsg)) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage(errorMsg);
            return result;
        }

        /* 进站工单调件信息验证 */
        if (ServiceStationVal.STATION_SERVICE == workOrder.getWoType()){
            /* 未到货确认不允许提交 */
            WorkOrderTransferParts workOrderTransferParts = new WorkOrderTransferParts();
            workOrderTransferParts.setWoCode(command.getWoCode());
            workOrderTransferParts.setOperateId(command.getOperateId());
            workOrderTransferParts.setWoType(workOrder.getWoType());
            workOrderTransferParts.setStatus(ServiceStationVal.TRANS_WAIT);
            int partsCount = workOrderTransferPartsMapper.selectByOperateIdAndStatus(workOrderTransferParts);
            if (partsCount > 0) {
                result.setResultCode(ECode.CLIENT_ERROR.code());
                result.setMessage("有未到货的调件信息，请确认！");
                return result;
            }
            /* 服务类型验证 */
            Map<String,Object> param = new HashMap<>(3);
            param.put("woCode",command.getWoCode());
            param.put("operateId",command.getOperateId());
            param.put("status",ServiceStationVal.TRANS_FINISH);
            Integer serviceType = workOrderTransferPartsMapper.selectServiceTypeByOperateId(param);
            if (serviceType != null && serviceType != Integer.parseInt(command.getServiceType())) {
                result.setResultCode(ECode.CLIENT_ERROR.code());
                result.setMessage("服务类型要与调件信息一致，不可变更！");
                return result;
            }
        }

        boolean lockGet = false;
        // 提交标识
        String comMark = command.getCommitMark();
        if (StringUtil.isEmpty(comMark)) {
            comMark = "3";
            command.setCommitMark("3");
        } else {
            if ("1".equals(comMark)) {
                if ((StringUtil.isNotEmpty(command.getServiceType()) && StringUtil.isEq(ServiceTypeEnum.SERVICE_TYPE_BAO_XIU.code(), command.getServiceType()))
                        || (StringUtil.isNotEmpty(command.getChargeType()) && StringUtil.isEq(ChargeTypeEnum.CHARGE_TYPE_WARRANTY.code(), command.getChargeType()))) {
                    // 保内 或 保修
                } else {
                    comMark = "3";
                    command.setCommitMark("3");
                }
            }
        }
        try {
            // ***********
            // 获得分布式锁
            // ***********
//            if (!startEndRepairService.lock(command.getWoCode(), command.getOperateId())) {
//                result.setResultCode(ReturnCode.CLIENT_ERROR.code());
//                result.setMessage("处理中，请稍后");
//                return result;
//            }
//            lockGet = true;
//            startEndRepairService.sleep();

            /* 操作唯一标识 唯一性验证 */
            if ("3".equals(comMark)) {
                WorkOrderOperate orderOperate = new WorkOrderOperate();
                orderOperate.setWoCode(command.getWoCode());
                orderOperate.setOperateCode(OperateCodeEnum.OP_REPAIR.code());
                orderOperate.setOperateId(command.getOperateId());
                Integer count = workOrderOperateMapper.selectByOperateId(orderOperate);
                if (count != null && count > 0) {
                    // 并发时，如果已经收到此记录，直接返回OK
                    log.info("[startRepair]already got data");
                    return result;
                }
            } else if ("2".equals(comMark)) {
                WorkOrderOperate orderOperate = new WorkOrderOperate();
                orderOperate.setWoCode(command.getWoCode());
                orderOperate.setOperateCode(OperateCodeEnum.OP_REPAIR.code());
                orderOperate.setOperateId(command.getOperateId());
                Integer count = workOrderOperateMapper.selectByOperateIdAndFinished(orderOperate);
                if (count != null && count > 0) {
                    // 二次提交时，避免网络不好的时候重复提交
                    log.info("[startRepair]commit twice");
                    return result;
                }
            }

            /* 插入操作记录 */
            insertRecord(command);

            // 处理配件码
            String woCode = command.getWoCode();
            String operateId = command.getOperateId();
            // 多次提交，先做删除
            accessoriesCodeMapper.deleteByWoCodeAndOper(woCode, operateId);
            List<PhotoPartForm> partCodeList = command.getPartCodeList();
            if(partCodeList != null && !partCodeList.isEmpty()){
                for(PhotoPartForm pp: partCodeList){
                    AccessoriesCode accessoriesCode = new AccessoriesCode();
                    accessoriesCode.setWoCode(woCode);
                    accessoriesCode.setAcceCode(pp.getPartCode());
                    accessoriesCode.setAcceType(pp.getPhotoTypeCode());
                    accessoriesCode.setOperateId(operateId);
                    accessoriesCodeMapper.insert(accessoriesCode);
                }
            }

         /*   if("1".equals(comMark)){
                // 同步
                uptimeService.trigger(result, command.getWoCode(), ServiceStationVal.WEB_SERVICE_WORKORDERENCLOSURE_1,
                        "manual", operateId,30000L);
            }*/

            log.info("[repairRecordSave]end");
        } finally {
            // ***********
            // 释放分布式锁
            // ***********
//            if (lockGet && !startEndRepairService.unlock(command.getWoCode(), command.getOperateId())) {
//                // 释放锁失败，说明之前上的锁已过期，锁被别的线程抢得，当前事务放弃
//                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//            }
        }
        return result;    }

    @Override
    public HttpCommandResultWithData repairRecordDetail(RepairRecordDetailForm command) {
        log.info("****************repairRecordDetail Start*****************");
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());
        RepairRecordDetailDto dto = new RepairRecordDetailDto();
        /* 工单信息验证 */
        Map<String, Object> valueMap = repairRecordWorkCheck(result, command.getUserId(), command.getWoCode(), true);
        if ((Boolean) valueMap.get(VALIDATE_FLG)) {
            return result;
        }
        /* 维修项详情查询 */
        Map<String, Object> param = new HashMap<>(3);
        param.put(WO_CODE, command.getWoCode());
        param.put("operateId", command.getOperateId());
        param.put("operateCode", OperateCodeEnum.OP_REPAIR.code());
        param.put("status", ServiceStationVal.TRANS_FINISH);
        List<RepairRecordDetailPojo> pojoList = workOrderOperateMapper.selectRepairRecordDetail(param);
        if (pojoList != null && !pojoList.isEmpty()) {
            dto.setFaultDescribe(pojoList.get(0).getFaultDescribe());
            // 服务类型或费用类型有一项不为空
            if (pojoList.get(0).getServiceType() != null) {
                dto.setServiceType(String.valueOf(pojoList.get(0).getServiceType()));
                dto.setType1(dto.getServiceType());
            }
            if (pojoList.get(0).getChargeType() != null) {
                dto.setChargeType(String.valueOf(pojoList.get(0).getChargeType()));
                dto.setType1(dto.getChargeType());
            }
            // 处理方式或付费方式有一项不为空
            if (pojoList.get(0).getPayType() != null) {
                dto.setPayType(String.valueOf(pojoList.get(0).getPayType()));
                dto.setType2(dto.getPayType());
            }
            if (pojoList.get(0).getDealType() != null) {
                dto.setDealType(pojoList.get(0).getDealType());
                dto.setType2(String.valueOf(dto.getDealType()));
            }
            // 是否调件 0：否；1：是
            dto.setTransferParts(pojoList.get(0).getTransferParts());
            // 按照片类型分组 去重
            List<String> photoTypeList = pojoList.stream().map(RepairRecordDetailPojo::getPhotoType).distinct().collect(Collectors.toList());
            // 类型集合
            List<RepairRecordPhotoDetailDto> photoDetailDtoList = new ArrayList<>();
            for (String photoType : photoTypeList) {
                RepairRecordPhotoDetailDto photoDetailDto = new RepairRecordPhotoDetailDto();
                photoDetailDto.setPhotoType(photoType);
                // 每个类型的图片集合
                List<RepairRecordPhotoDetailDto.Photo> photoList = new ArrayList<>();
                if (StringUtil.isNotEmpty(photoType)) {
                    for (RepairRecordDetailPojo pojo : pojoList) {
                        if (photoType.equals(pojo.getPhotoType())) {
                            photoList.add(new RepairRecordPhotoDetailDto.Photo(pojo.getUrl()));
                        }
                    }
                }
                photoDetailDto.setPhotoList(photoList);
                photoDetailDtoList.add(photoDetailDto);
            }
            dto.setList(photoDetailDtoList);
        }

        // 处理配件码
        List<AccessoriesCode> accessoriesCodeList = accessoriesCodeMapper.selectListByWoCode(command.getWoCode(), command.getOperateId());
        if (accessoriesCodeList != null && !accessoriesCodeList.isEmpty()) {
            Set<String> allAcceType = accessoriesCodeList.stream().map(AccessoriesCode::getAcceType).collect(Collectors.toSet());
            List<RepairRecordDetailDto.PhotoType> partCodeList = new ArrayList<>();
            for (String acceType : allAcceType) {
                RepairRecordDetailDto.PhotoType pt = new RepairRecordDetailDto.PhotoType();
                pt.setPhotoTypeCode(acceType);
                List<RepairRecordDetailDto.PhotoType.Part> partList = accessoriesCodeList.stream().filter(acc -> acceType.equals(acc.getAcceType()))
                        .map(acc -> new RepairRecordDetailDto.PhotoType.Part(acc.getAcceCode())).collect(Collectors.toList());
                pt.setList(partList);
                partCodeList.add(pt);
            }
            dto.setPartCodeList(partCodeList);
        }

        result.setData(dto);
        log.info("****************repairRecordDetail End*****************");
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HttpCommandResultWithData timesRescue(TimesRescueForm command) throws JsonProcessingException {
        log.info("[timesRescue]start");
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());
        // 工单号
        String woCode = command.getWoCode();
        /* 工单信息验证 */
        Map<String, Object> valueMap = repairRecordWorkCheck(result, command.getUserId(), woCode, false);
        if ((Boolean) valueMap.get(VALIDATE_FLG)) {
            return result;
        }
        WorkOrder workOrder = (WorkOrder) valueMap.get(WORK_ORDER);
        /* 工单类型判断 */
        if (ServiceStationVal.OUTSIDE_RESCUE != workOrder.getWoType()) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("工单类型不是外出救援！");
            return result;
        }
        /* 申请原因判断 */
        DataDict dataDict = new DataDict();
        dataDict.setCode(ServiceStationVal.DATA_DICT_CODE_TIMES_RESCUE);
        dataDict.setValue(Integer.parseInt(command.getReasonCode()));
        dataDict = dataDictMapper.selectByCodeAndValue(dataDict);
        if (dataDict == null) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("未获取到申请原因");
            return result;
        }
        // 外出救援次数
        int timesRescueNumber = workOrder.getTimesRescueNumber() == null ? 1 : workOrder.getTimesRescueNumber();
        log.info("[timesRescue]timesRescueNumber = {}", timesRescueNumber);
        // 服务部需求：外出救援次数限制
        if (timesRescueNumber >= timesRescueMaxTimes) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("已有二次外出救援记录，无法再次申请！");
            return result;
        }

        // 系统时间
        Date date = new Date();

        /* 保存外出信息 */
        int res = saveOutDetail(workOrder, command, date);
        if (res != 0) {
            if (res == -1){
                result.setResultCode(ECode.CLIENT_ERROR.code());
                result.setMessage("数据提交失败！");
                return result;
            }
        }

        // 校验工单是否在外出救援路线轨迹点记录表里无记录
        RescueRoutePoint rescueRoutePoint = rescueRoutePointMapper.selectByWoCode(woCode);

        /* 备份外出救援轨迹数据 */
        if (rescueRoutePoint != null) {
            log.info("[timesRescue] insert rescueRoutePointHistory 备份外出救援轨迹数据");
            rescueRoutePointHistoryMapper.insertHistory(rescueRoutePoint, timesRescueNumber);
            /* 清空外出救援轨迹记录 */
            log.info("[timesRescue] delete rescueRoutePoint 清空外出救援轨迹记录");
            rescueRoutePointMapper.deleteByPrimaryKey(rescueRoutePoint.getId());
        } else {
            log.info("[timesRescue]  not rescueRoutePoint data 没有外出救援轨迹数据");
        }

        /* 清空外出救援预警工单记录 */
        log.info("[timesRescue] delete workOrderOvertime 清空外出救援预警工单记录");
        Map<String, Object> param = new HashMap<>(2);
        param.put("woCode", woCode);
        param.put("list", Arrays.asList(ServiceStationVal.WARN_TYPE_OUT_TAKEOFF, ServiceStationVal.WARN_TYPE_OUT_ARRIVE, ServiceStationVal.WARN_TYPE_OUT_REPAIR));
        workOrderOvertimeMapper.deleteByType(param);

        /* 备份工单数据 */
        log.info("[timesRescue] insert workOrderHistory 备份工单数据");
        workOrderHistoryMapper.insert(workOrder, timesRescueNumber);

        /* 更新工单表信息 */
        log.info("[timesRescue] update WorkOrder 更新工单表信息");
        WorkOrder updateWorkOrder = new WorkOrder();

        setClearUpdateEntity(updateWorkOrder, workOrder.getId(), timesRescueNumber, date, command.getTransferParts());
        workOrderMapper.updateNullByPrimaryKey(updateWorkOrder);

        /* 插入调件信息 */
        if (YES.equals(command.getTransferParts())) {
            List<WorkOrderTransferParts> transferParts = rescueTransferPartsService.getInsertEntities(command.getParamList(), workOrder);
            int insCnt = workOrderTransferPartsMapper.batchInsert(transferParts);
            log.info("[timesRescue]inserted:{}", insCnt);
        }

        /* 添加工单操作记录 */
        log.info("[timesRescue] insert workOrderOperate 添加工单操作记录");
        WorkOrderOperate workOrderOperate = new WorkOrderOperate();
        workOrderOperate.setWoCode(woCode);
        workOrderOperate.setOperateCode(OperateCodeEnum.OP_TIMES_RESCUE.code());
        workOrderOperate.setTitle(OperateCodeEnum.OP_TIMES_RESCUE.message());
        workOrderOperate.setUserId(command.getUserId());
        workOrderOperate.setTimesRescueNumber(timesRescueNumber);
        workOrderOperate.setCreateTime(date);
        workOrderOperate.setUpdateTime(date);
        // 服务记录显示内容用json字符串格式保存
        Map<String, String> jsonMap = new LinkedHashMap<>(3);
        jsonMap.put("申请原因：", dataDict.getName());
        if (YES.equals(command.getTransferParts())) {
            jsonMap.put("是否调件：", "是");
            int counter = 1;
            for (RescueTransferPartsListForm entity : command.getParamList()) {
                jsonMap.put("配件零件号" + counter + "：", entity.getPartsNo());
                jsonMap.put("配件名称" + counter + "：", entity.getPartsName());
                jsonMap.put("配件数量" + counter + "：", entity.getPartsNum());
                jsonMap.put("是否纯正配件" + counter + "：", YES.equals(entity.getPartsFlag()) ? "是" : "否");
                counter++;
            }
        } else {
            jsonMap.put("是否调件：", "否");
        }
        jsonMap.put("申请账号：", command.getUserId());
        jsonMap.put("申请时间：", DateUtil.getDatePattern(date.getTime()));
        workOrderOperate.setTextJson(JsonUtil.toJson(jsonMap));
        workOrderOperateMapper.insertSelective(workOrderOperate);
        /* 更新操作记录外出救援次数 以及维修项为显示 */
        log.info("[timesRescue] update WorkOrderOperate timesRescueNumber={} 更新操作记录外出救援次数", timesRescueNumber);
        WorkOrderOperate updateWorkOrderOperate = new WorkOrderOperate();
        updateWorkOrderOperate.setWoCode(woCode);
        updateWorkOrderOperate.setOperateCode(OperateCodeEnum.OP_REPAIR.code());
        updateWorkOrderOperate.setTimesRescueNumber(timesRescueNumber);
        workOrderOperateMapper.updateTimesRescueNumber(updateWorkOrderOperate);

        log.info("[timesRescue]end");
        return result;
    }

    /**
     * 工单信息验证
     *
     * @param result         结果
     * @param userId         用户id
     * @param woCode         工单号
     * @param roleCheckLoose 宽松角色校验
     *                       true：宽松、即使不是指派人，如果是管理员身份也可以；
     *                       false：严格、必须为指派人
     * @return true：校验有误；false：校验通过
     */
    public Map<String, Object> repairRecordWorkCheck(HttpCommandResultWithData result, String userId, String woCode, Boolean roleCheckLoose) {
        log.info("****************repairRecordWorkCheck Start*****************");
        WorkOrder workOrder = new WorkOrder();
        boolean validateFlg = false;
        UserInfoDto userInfoDto = queryUserInfoService.getUserInfoByUserId(userId, false);
        if (userInfoDto == null) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("此用户不存在！");
            validateFlg = true;
        } else {
            /* 查询工单信息 */
            Map<String, String> param = new HashMap<>(2);
            param.put(WO_CODE, woCode);
            param.put("stationId", userInfoDto.getServiceStationId());
            workOrder = workOrderMapper.selectByWoCode(param);
            if (workOrder == null) {
                result.setResultCode(ECode.CLIENT_ERROR.code());
                result.setMessage("工单不存在！");
                validateFlg = true;
            } else {
                /* 工单权限判断*/
                if (!userId.equals(workOrder.getAssignTo())) {
                    // 是否进行角色验证
                    /**
                     * 取消角色验证，接单员也可以提交不是本人的工单
                     //                    if (!roleCheckLoose || ServiceStationVal.JOB_TYPE_ADMIN != userInfoDto.getRoleCode()) {
                     //                        result.fillResult(ReturnCode.CLIENT_ERROR);
                     //                        result.setMessage("您不是此工单的指派人员！");
                     //                        validateFlg = true;
                     //                    }
                     */
                }
                /* 工单状态判断 */
                if (ServiceStationEnum.REPAIRING.code() != workOrder.getWoStatus()) {
                    result.setResultCode(ECode.CLIENT_ERROR.code());
                    result.setMessage("工单不是维修中状态！");
                    validateFlg = true;
                }
            }
        }
        Map<String, Object> valueMap = new HashMap<>(2);
        valueMap.put(VALIDATE_FLG, validateFlg);
        valueMap.put(WORK_ORDER, workOrder);
        log.info("****************repairRecordWorkCheck End*****************");
        return valueMap;
    }
    /**
     * @param workOrder
     * @Description: 设置工单信息
     * @method: pojoToDto
     * @Date: 2018/5/14 14:30
     * @authur: jiangcm
     */
    private RepairRecordsListDto setWorkOrderInfo(WorkOrder workOrder) {
        log.info("****************setWorkOrderInfo Start*****************");
        RepairRecordsListDto dto = new RepairRecordsListDto();
        String chassisNum = workOrder.getChassisNum();
        // 车牌号
        dto.setCarNumber(carMapper.queryCarNumberByVin(chassisNum));
        // 底盘号
        dto.setChassisNum(StringUtil.isEmpty(chassisNum) ? "" : chassisNum.substring(chassisNum.length() - 8, chassisNum.length()));
        // 车辆位置
        dto.setCarLocation(workOrder.getCarLocation());
        // 车辆纬度
        dto.setCarLat(workOrder.getCarLat());
        // 车辆经度
        dto.setCarLon(workOrder.getCarLon());
        // 工单号
        dto.setWoCode(workOrder.getWoCode());
        // 工单状态
        dto.setWoStatus(workOrder.getWoStatus().toString());
        // 工单类型
        dto.setWoType(workOrder.getWoType());
        // 预约方式 1： 400客服，2：司机App
        dto.setAppointmentType(workOrder.getAppoType());
        // 预约项目	1：维修项目，2：保养项目，3：维修和保养项目
        int appointmentItem = 1;
        if (StringUtil.isNotEmpty(workOrder.getMaintainItem()) && StringUtil.isNotEmpty(workOrder.getRepairItem())) {
            appointmentItem = 3;
        } else if (StringUtil.isNotEmpty(workOrder.getMaintainItem())) {
            appointmentItem = 2;
        }
        dto.setAppointmentItem(appointmentItem);
        // 待维修工单：检查结束时间
        dto.setWorkTime(workOrder.getTimeInspected() == null ? "" : DateUtil.format(DateUtil.time_pattern, workOrder.getTimeInspected()));
        // 维修方案确定时间
        dto.setHasRepairPhotoTime(workOrder.getTimeRepairPhoto() == null ? 0 : 1);
        log.info("****************setWorkOrderInfo End*****************");
        return dto;
    }
    /**
     * 根据前端传入的字典编码值自动设置服务类型/费用类型/处理方式/付费方式
     */
    private void setCommand(RepairRecordSaveForm command) {
        command.setServiceType(command.getType1());
//        boolean found = false;
//        for (ServiceTypeEnum e : ServiceTypeEnum.values()) {
//            if (StringUtil.isEq(e.code(), command.getType1())) {
//                found = true;
//                command.setServiceType(command.getType1());
//                break;
//            }
//        }

        if(StringUtils.equals(command.getType1(),"1") || StringUtils.equals(command.getType1(),"4")){
            command.setDealType(command.getType2());
        }else if(StringUtils.equals(command.getType1(),"2")) {
            command.setPayType(command.getType2());
        }

        /*if (!found) {
            for (ChargeTypeEnum e : ChargeTypeEnum.values()) {
                if (StringUtil.isEq(e.code(), command.getType1())) {
                    command.setChargeType(command.getType1());
                    break;
                }
            }
        }
        found = false;
        for (DealTypeEnum e : DealTypeEnum.values()) {
            if (StringUtil.isEq(e.code(), command.getType2())) {
                found = true;
                //command.setDealType(command.getType2());
                command.setPayType(command.getType2());
                break;
            }
        }*/
        /*if (!found) {
            for (PayTypeEnum e : PayTypeEnum.values()) {
                if (StringUtil.isEq(e.code(), command.getType2())) {
                    command.setPayType(command.getType2());
                    break;
                }
            }
        }*/
    }
    /**
     * 校验入参之间的必填/非必填关系
     */
    private String validateCommand(RepairRecordSaveForm command, int woType, int rescueType) {
        if (woType == ServiceStationVal.STATION_SERVICE) {
            // 进出站工单，服务类型必填
            if (StringUtil.isEmpty(command.getServiceType())) {
                return "服务类型不能为空";
            }
            // 服务类型为“保修”时，处理方式必填
//            if (StringUtil.isEq(command.getServiceType(), ServiceTypeEnum.SERVICE_TYPE_BAO_XIU.code())
//                    && StringUtil.isEmpty(command.getDealType())) {
//                return "处理方式不能为空";
//            }
        } else if (woType == ServiceStationVal.OUTSIDE_RESCUE) {
//            if(ServiceStationVal.OUTSIDE_ONCALL != rescueType ) {
//                // 外出救援工单，费用类型必填
//                if (StringUtil.isEmpty(command.getChargeType())) {
//                    return "费用类型不能为空";
//                }
//            }

//            // 费用类型为“保内”时，处理方式必填
//            if (StringUtil.isEq(command.getChargeType(), ChargeTypeEnum.CHARGE_TYPE_WARRANTY.code())
//                    && StringUtil.isEmpty(command.getDealType())) {
//                return "处理方式不能为空";
//            }
//            // 费用类型为“保外”时，付费方式必填
//            if (StringUtil.isEq(command.getChargeType(), ChargeTypeEnum.CHARGE_TYPE_OVER_WARRANTY.code())
//                    && StringUtil.isEmpty(command.getPayType())) {
//                return "付费方式不能为空";
//            }
            // 进出站工单，服务类型必填
            if (StringUtil.isEmpty(command.getServiceType())) {
                return "服务类型不能为空";
            }
            // 服务类型为“保修”时，处理方式必填
//            if (StringUtil.isEq(command.getServiceType(), ServiceTypeEnum.SERVICE_TYPE_BAO_XIU.code())
//                    && StringUtil.isEmpty(command.getDealType())) {
//                return "处理方式不能为空";
//            }
        }
        // 进站-里保 以外，故障简述必填
//        if (StringUtil.isNotEq(command.getServiceType(), ServiceTypeEnum.SERVICE_TYPE_LI_BAO.code())
//                && StringUtil.isEmpty(command.getFaultDescribe())) {
//            return "故障简述不能为空";
//        }
        return "";
    }
    /**
     * 提交维修项插入工单操作记录
     */
    private void insertRecord(RepairRecordSaveForm command) throws JsonProcessingException {
        // 提交标识
        String cMark = command.getCommitMark();

        WorkOrderOperate workOrderOperate = new WorkOrderOperate();
        Map<String, String> param = new HashMap<>();
        param.put("woCode", command.getWoCode());
        WorkOrder workOrder = workOrderMapper.selectByWoCode(param);
        if (ServiceStationVal.STATION_SERVICE == workOrder.getWoType()){
            if (null != command.getDeviceId() && !"".equals(command.getDeviceId())) {
                workOrder.setDeviceId(command.getDeviceId());
                workOrder.setUpdateTime(new Date());
                workOrderMapper.updateByPrimaryKey(workOrder);
            }
        }

        workOrderOperate.setWoCode(command.getWoCode());
        // 操作唯一标识
        workOrderOperate.setOperateId(command.getOperateId());
        // 工单操作状态
        workOrderOperate.setOperateCode(OperateCodeEnum.OP_REPAIR.code());
        // 对服务APP不可见标志 1：隐藏，默认0
        workOrderOperate.setIsHiddenToApp(0);
        // 费用类型
        if (StringUtil.isNotEmpty(command.getChargeType())) {
            workOrderOperate.setChargeType(Integer.parseInt(command.getChargeType()));
        }
        // 服务类型
        if (StringUtil.isNotEmpty(command.getServiceType())) {
            workOrderOperate.setServiceType(Integer.parseInt(command.getServiceType()));
        }
        // 处理方式
        if (StringUtil.isNotEmpty(command.getDealType())) {
            workOrderOperate.setDealType(Integer.parseInt(command.getDealType()));
        }
        // 付费方式
        if (StringUtil.isNotEmpty(command.getPayType())) {
            workOrderOperate.setPayType(Integer.parseInt(command.getPayType()));
        }
        // 描述
        workOrderOperate.setDescription(command.getFaultDescribe());
        // 照片总数量
        int photoNum = Integer.parseInt(command.getPhotoNum());
        workOrderOperate.setPhotoNum(photoNum);
        // 服务记录显示标题
        workOrderOperate.setTitle(OperateCodeEnum.OP_REPAIR.message());
        // 1：隐藏，默认0
        workOrderOperate.setHiddenFlg(1);
        workOrderOperate.setUserId(command.getUserId());
        Date date = new Timestamp(System.currentTimeMillis());
        workOrderOperate.setCreateTime(date);
        workOrderOperate.setUpdateTime(date);
        //服务记录显示内容用json字符串格式保存
        Map<String, String> map = new LinkedHashMap<>();
        if (StringUtil.isNotEmpty(command.getChargeType())) {
            map.put("费用类型：", ChargeTypeEnum.getMessage(workOrderOperate.getChargeType()));
        }
        if (StringUtil.isNotEmpty(command.getServiceType())) {
            map.put("处理方式：", ServiceTypeEnum.getMessage(workOrderOperate.getServiceType()));
//            if (ServiceStationVal.STATION_SERVICE == workOrder.getWoType()){
//                map.put("服务类型：", ServiceTypeEnum.getMessage(workOrderOperate.getServiceType()));
//                if(workOrderOperate.getServiceType() == 4){
//                    map.put("处理方式：", DealTypeEnum.getMessage(workOrderOperate.getDealType()));
//                }
//            }else {
//                map.put("服务类型：", workOrderOperate.getServiceType() == 1? "保内": "保外");
//                if(workOrderOperate.getServiceType() == 1){
//                    map.put("处理方式：", DealTypeEnum.getMessage(workOrderOperate.getDealType()));
//                }else {
//                    map.put("付费方式：", workOrderOperate.getPayType() == 11?"厂家付费": "用户自费");
//                }
//            }
        }

        if (StringUtil.isNotEmpty(command.getFaultDescribe())) {
            map.put("故障描述：", command.getFaultDescribe());
        }
        String mapKey = "维修过程拍照：";
//        if (StringUtil.isEq(command.getServiceType(), ServiceTypeEnum.SERVICE_TYPE_ZI_FEI.code())
//                || StringUtil.isEq(command.getServiceType(), ServiceTypeEnum.SERVICE_TYPE_ZOU_BAO.code())
//                || StringUtil.isEq(command.getServiceType(), ServiceTypeEnum.SERVICE_TYPE_LI_BAO.code())) {
//            mapKey = "车辆信息拍照：";
//        }
        if (photoNum > 0) {
            map.put(mapKey, ServiceStationVal.PHOTO_URL_PLACEHOLDER);
        }
        workOrderOperate.setTextJson(JsonUtil.toJson(map));
        if("1".equals(cMark)){
            // 保修保内分次提交标识
            workOrderOperate.setSubTwiceMark(1);
            workOrderOperate.setFinishedStatus(0);
        }else if("2".equals(cMark)){
            // 删除1次提交内容
            workOrderOperate.setSubTwiceMark(1);
            workOrderOperateMapper.delFirstCommitOperate(workOrderOperate);
            workOrderOperate.setFinishedStatus(1);
        }else if("3".equals(cMark)){
            workOrderOperate.setFinishedStatus(1);
        }
        workOrderOperateMapper.insertSelective(workOrderOperate);
    }
    public static String validateCommand(Object obj, Class... groups) {
        String message = null;//用于存储验证后的错误信息

        Validator validator = Validation.buildDefaultValidatorFactory()
                .getValidator();

        Set<ConstraintViolation<Object>> constraintViolations = validator
                .validate(obj, groups);//验证某个对象,，其实也可以只验证其中的某一个属性的

        Iterator<ConstraintViolation<Object>> iter = constraintViolations
                .iterator();
        while (iter.hasNext()) {
            String tempMessage = iter.next().getMessage();
            if(tempMessage != null && tempMessage.length() > 0)
            {
                message = tempMessage;
                break;
            }
        }

        return message;
    }
    private int saveOutDetail(WorkOrder workOrder, TimesRescueForm command, Date date) {
        WorkOrderOutDetail wood = this.workOrderOutDetailMapper.selectByWoCode(workOrder.getWoCode());
        if (wood != null) {
            wood.setEndAddress(command.getEndAddress());
            wood.setEndMileage(Float.valueOf(command.getEndMileage()));
            wood.seteOutDate(date);
            wood.setOutTimes(2);
            wood.setAppOutMileage(Float.valueOf(command.getAppOutMileage()));
            wood.setTwiceOutCause(command.getTwiceOutCause());
            this.workOrderOutDetailMapper.updateByPrimaryKey(wood);
            return 0;
        }else{
            return -1;
        }
    }
    /**
     * 设置工单表需要清除或更新的字段
     */
    private void setClearUpdateEntity(WorkOrder updateWorkOrder, long pk, int timesRescueNumber, Date updateDate, String isTransferParts) {
        // 出发时间
        updateWorkOrder.setTimeDepart(null);
        // 预计到达时间
        updateWorkOrder.setTimeArriveExpected(null);
        // 接车时间
        updateWorkOrder.setTimeReceive(null);
        // 开始检查时间
        updateWorkOrder.setTimeInspectBegin(null);
        // 检查结束时间
        updateWorkOrder.setTimeInspected(null);
        // 维修方案确定时间
        updateWorkOrder.setTimeRepairPhoto(null);
        // 工单结束时间
        updateWorkOrder.setTimeClose(null);
        // 申请拒单时间
        updateWorkOrder.setTimeApplyrefuse(null);
        // 申请修改时间
        updateWorkOrder.setTimeApplymodify(null);
        // 申请关闭时间
        updateWorkOrder.setTimeApplyclose(null);
        // 申请拒单类型编码
        updateWorkOrder.setRefuseType(null);
        // 申请拒单理由
        updateWorkOrder.setRefuseReason(null);
        // 申请拒单次数
        updateWorkOrder.setRefuseTimes(null);
        // 申请关闭类型编码
        updateWorkOrder.setCloseType(null);
        // 申请关闭理由
        updateWorkOrder.setCloseReason(null);
        // 申请修改理由
        updateWorkOrder.setModifyReason(null);
        // 接车异常标志
        updateWorkOrder.setIsAbnormalReceive(null);
        // 外出救援出发时设备ID
        updateWorkOrder.setDeviceId(null);
        // 催单次数
        updateWorkOrder.setRemindTimes(null);
        // 最新一次催单时间
        updateWorkOrder.setLastRemindTime(null);
        // 工单状态
        updateWorkOrder.setWoStatus(ServiceStationEnum.TO_TAKE_OFF.code());
        // 外出救援次数
        updateWorkOrder.setTimesRescueNumber(timesRescueNumber + 1);
        // 是否调件
        updateWorkOrder.setRescueIsTransferring(YES.equals(isTransferParts) ? 1 : 0);
        // 预计完工时间
        updateWorkOrder.setEstimateTime(null);
        // 预估费用
        updateWorkOrder.setEstimateFee(null);
        // 更新时间
        updateWorkOrder.setUpdateTime(updateDate);
        updateWorkOrder.setId(pk);
    }
}
