package com.nut.servicestation.app.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.nut.common.constant.OperateCodeEnum;
import com.nut.common.constant.ServiceStationVal;
import com.nut.common.enums.ServiceStationEnum;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.utils.DateUtil;
import com.nut.common.utils.JsonUtil;
import com.nut.common.utils.StringUtil;
import com.nut.servicestation.app.dao.WorkOrderDao;
import com.nut.servicestation.app.dao.WorkOrderOperateDao;
import com.nut.servicestation.app.domain.RescueRoutePoint;
import com.nut.servicestation.app.domain.WorkOrder;
import com.nut.servicestation.app.domain.WorkOrderOperate;
import com.nut.servicestation.app.dto.PointDto;
import com.nut.servicestation.app.dto.UserInfoDto;
import com.nut.servicestation.app.form.ApplyCloseWoForm;
import com.nut.servicestation.app.form.ApplyModifyWoForm;
import com.nut.servicestation.app.service.ApplyModifyOrCloseWoService;
import com.nut.servicestation.app.service.ScanReceiveService;
import com.nut.servicestation.app.service.UserService;
import com.nut.servicestation.common.constants.RefuseOrCloseEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/*
 *  @author wuhaotian 2021/7/6
 */
@Slf4j
@Service("ApplyModifyOrCloseWoService")
public class ApplyModifyOrCloseWoServiceImpl implements ApplyModifyOrCloseWoService {

    @Autowired
    private UserService userService;

    @Autowired
    private WorkOrderDao workOrderMapper;

    /** 最大申请关闭次数 */
    @Value("${close_max_times:2}")
    private Integer closeMaxTimes;

    @Autowired
    private ScanReceiveService scanReceiveService;

    @Value("${pointsToRedisKeyPrefix.one:196bba4fc}")
    private String pointsToRedisKeyPrefixOne;
    @Value("${pointsToRedisKeyPrefix.two:296bba4fc}")
    private String pointsToRedisKeyPrefixTwo;
    private static final String YES = "1";
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private WorkOrderOperateDao workOrderOperateMapper;
    /** 最大申请修改次数 */
    @Value("${modify_max_times:2}")
    private Integer modifyMaxTimes;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public HttpCommandResultWithData closeWo(ApplyCloseWoForm command) throws IOException {
        HttpCommandResultWithData<List> result = new HttpCommandResultWithData<>();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        /* 校验入参 */
        String error = validateCommand(command);
        if (StringUtil.isNotEmpty(error)) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage(error);
            return result;
        }

        //用户信息查询
        UserInfoDto userInfoDto = userService.getUserInfoByUserId(command.getUserId(), false);
        if (userInfoDto == null) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("此用户不存在！");
            return result;
        }

        Map<String, String> queryMap = new HashMap<>(4);
        queryMap.put("woCode", command.getWoCode());

        //根据角色判断是否有申请关闭权限
        //管理员有服务站下所有工单权限，普通员工只有修改自己名下工单权限
        WorkOrder workOrder;
        if (Integer.valueOf(ServiceStationVal.JOB_TYPE_ADMIN).equals(userInfoDto.getRoleCode())) {
            queryMap.put("stationId", userInfoDto.getServiceStationId());
            //查询工单信息
            workOrder = workOrderMapper.selectByWoCode(queryMap);
            if (workOrder == null) {
                result.setResultCode(ECode.CLIENT_ERROR.code());
                result.setMessage("此工单不存在或此工单不属于所在服务站！");
                return result;
            }
        } else {
            queryMap.put("assignTo", command.getUserId());
            workOrder = workOrderMapper.selectByWoCodeAndAssignTo(queryMap);
            if (workOrder == null) {
                result.setResultCode(ECode.CLIENT_ERROR.code());
                result.setMessage("此工单不存在或此工单不是您名下工单！");
                return result;
            }
        }

        // 校验是否超过关闭次数限制
        if (workOrder.getCloseTimes() != null && workOrder.getCloseTimes() >= closeMaxTimes) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("已超过申请关闭次数限制");
            return result;
        }

        //根据工单状态验证是否可申请关闭工单
        //可申请关闭工单返回工单的下一状态码
        Integer nextWoStatus = getCanCloseAndNextWoStatus(workOrder);
        log.info("当前工单状态 : {} || 下一工单状态 : {} || 服务站ID : {}", workOrder.getWoStatus(), nextWoStatus, userInfoDto.getServiceStationId());
        if (0 == nextWoStatus) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("此工单不能申请关闭，请确认工单状态！");
            return result;
        }
        log.info("关闭工单传过来的maxIndex{}", command.getRpMaxIndex());
        // 外出救援时校验轨迹点完整
        //V2.2.9.0追加：外出救援-待出发状态不进行轨迹点校验
        if (workOrder.getWoType() == ServiceStationVal.OUTSIDE_RESCUE && workOrder.getWoStatus() != ServiceStationVal.TO_TAKE_OFF) {
            RescueRoutePoint rescueRoutePoint = scanReceiveService.preSavePoint(command.getWoCode(), command.getLon(),
                    command.getLat(), command.getRpMaxIndex());
            if (rescueRoutePoint == null) {
                result.setResultCode(ECode.CLIENT_ERROR.code());
                result.setMessage("救援轨迹不存在");
                return result;
            }

            // 将一次或者二次的轨迹点json保存以便于计算距离使用
            String pointJson = rescueRoutePoint.getOriginalPoints();
            // 获取工单信息
            Map<String, String> param = new HashMap<>(1);
            param.put("woCode", command.getWoCode());
            WorkOrder order = workOrderMapper.selectByWoCode(param);

            Integer timesRescueNumber = 1;
            if(null != order.getTimesRescueNumber()){
                timesRescueNumber = order.getTimesRescueNumber();
            }
            // 一次和二次外出的redis的key
            String oneKey = pointsToRedisKeyPrefixOne+command.getWoCode();
            // 如果是二次外出，那么需要将第一次轨迹点合进来。
            if (timesRescueNumber > 1){
                List<String> range = redisTemplate.opsForList().range(oneKey, 0, -1);
                List<PointDto> pointDtos = new ArrayList<>();
                for (String str : range) {
                    pointDtos.add(JSONObject.parseObject(str, PointDto.class));
                }
                final Integer maxIndexFinal = rescueRoutePoint.getMaxIndex();
                List<PointDto> collect = pointDtos.stream().filter(item -> item.getIndex() <= maxIndexFinal).collect(Collectors.toList());
                List<PointDto> twoPoints = JsonUtil.toList(rescueRoutePoint.getOriginalPoints(), PointDto.class);
                pointDtos.addAll(twoPoints);
                rescueRoutePoint.setOriginalPoints(JsonUtil.toJson(pointDtos));
            }

            if (!YES.equals(command.getForceEnd())) {
                List<PointDto> originalPoints = JsonUtil.toList(rescueRoutePoint.getOriginalPoints(), PointDto.class);
                // 轨迹点完整性校验
                List<Integer> requiredIndexes = scanReceiveService.checkRoutePointNum(rescueRoutePoint.getMaxIndex(),
                        originalPoints);
                /*if (!requiredIndexes.isEmpty()) {
                    result.fillResult(ReturnCode.NEED_WAIT_FOR_POINT_UPLOAD);
                    result.setData(requiredIndexes);
                    return result;
                }*/
            }

            rescueRoutePoint.setOriginalPoints(pointJson);
            /* 计算救援距离 */
            scanReceiveService.calculateDistance(command.getWoCode() ,rescueRoutePoint, userInfoDto.getServiceStationLon(),
                    userInfoDto.getServiceStationLat(), workOrder.getTimeDepart(), false, null);
        }

        /* 更新状态 */
        updateWoInsertRecord(workOrder, command, nextWoStatus, command.getUserId());

        List<Map<String, Object>> data = new ArrayList<>();
        Map<String, Object> closeMap = new HashMap<>(3);
        int upCloseTimes = 0;
        if (workOrder.getCloseTimes() != null) {
            upCloseTimes = workOrder.getCloseTimes() + 1;
        } else {
            upCloseTimes = 1;
        }
        int canCloseCount = closeMaxTimes - upCloseTimes;
        closeMap.put("canCloseCount", canCloseCount);
        data.add(closeMap);
        result.setData(data);
        result.setMessage("已提交成功，您还剩" + (canCloseCount) + "次申请关闭机会！");

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HttpCommandResultWithData modifyWo(ApplyModifyWoForm command) throws JsonProcessingException {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        //用户信息查询
        UserInfoDto userInfoDto = userService.getUserInfoByUserId(command.getUserId(), false);
        if (userInfoDto == null) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("此用户不存在！");
            return result;
        }

        //用户权限校验
        //只有管理员有申请修改权限
        if (!Integer.valueOf(ServiceStationVal.JOB_TYPE_ADMIN).equals(userInfoDto.getRoleCode())) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("您没有权限申请修改此工单信息！");
            return result;
        }

        Map<String, String> queryMap = new HashMap<>(2);
        queryMap.put("woCode", command.getWoCode());
        queryMap.put("stationId", userInfoDto.getServiceStationId());

        //查询工单信息
        WorkOrder workOrder = workOrderMapper.selectByWoCode(queryMap);
        if (workOrder == null) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("此工单不存在或此工单不属于所在服务站！");
            return result;
        }

        // 校验是否超过修改次数限制
        if (workOrder.getModifyTimes() != null && workOrder.getModifyTimes() >= modifyMaxTimes) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("已超过申请修改次数限制");
            return result;
        }

        //根据工单类型和工单状态验证是否可申请修改工单
        //可修改工单返回工单的下一状态码
        //修改申请审核中-待接受	、修改申请审核中-待出发、修改申请审核中-待接车
        Integer nextWoStatus = getCanModifyAndNextWoStatus(workOrder);
        log.info("当前工单类型 : {} || 当前工单状态 : {} || 下一工单状态 : {} || 服务站ID : {}", workOrder.getWoType(), workOrder.getWoStatus(), nextWoStatus, userInfoDto.getServiceStationId());
        if (0 == nextWoStatus) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("此工单不能申请修改，请确认工单状态！");
            return result;
        }

        //系统时间
        Date date = new Date();

        //更新工单表信息
        WorkOrder upDataWorkOrder = new WorkOrder();
        upDataWorkOrder.setId(workOrder.getId());
        upDataWorkOrder.setWoStatus(nextWoStatus);
        upDataWorkOrder.setModifyReason(command.getModifyReason());
        upDataWorkOrder.setUpdateTime(date);
        upDataWorkOrder.setTimeApplymodify(date);
        if (workOrder.getModifyTimes() != null) {
            upDataWorkOrder.setModifyTimes(workOrder.getModifyTimes() + 1);
        } else {
            upDataWorkOrder.setModifyTimes(1);
        }
        workOrderMapper.updateByPrimaryKeySelective(upDataWorkOrder);

        //增操作记录信息
        WorkOrderOperate workOrderOperate = new WorkOrderOperate();
        workOrderOperate.setWoCode(workOrder.getWoCode());
        workOrderOperate.setOperateCode(OperateCodeEnum.OP_MODIFY_APPLY.code());
        workOrderOperate.setTitle(OperateCodeEnum.OP_MODIFY_APPLY.message());
        workOrderOperate.setUserId(userInfoDto.getAccountId());
        workOrderOperate.setCreateTime(date);
        workOrderOperate.setUpdateTime(date);
        //服务记录显示内容用json字符串格式保存
        Map<String, String> map = new LinkedHashMap<>(3);
        map.put("申请原因：", command.getModifyReason());
        map.put("申请账号：", userInfoDto.getAccountId());
        map.put("申请时间：", DateUtil.getDatePattern(date.getTime()));
        workOrderOperate.setTextJson(JsonUtil.toJson(map));
        workOrderOperateMapper.insertSelective(workOrderOperate);

        Map<String, Object> data = new HashMap<>(3);
        int canModifyCount = modifyMaxTimes - upDataWorkOrder.getModifyTimes();
        data.put("canModifyCount", canModifyCount);
        result.setData(data);
        result.setMessage("已提交成功，您还剩" + (canModifyCount) + "次修改机会！");

        return result;
    }

    /**
     * 校验入参
     */
    private String validateCommand(ApplyCloseWoForm command) {
        if (StringUtil.isNotEmpty(command.getLon())) {
            try {
                double lon = Double.parseDouble(command.getLon());
            } catch (NumberFormatException nfe) {
                return "经度格式不正确";
            }
        }
        if (StringUtil.isNotEmpty(command.getLat())) {
            try {
                double lat = Double.parseDouble(command.getLat());
            } catch (NumberFormatException nfe) {
                return "纬度格式不正确";
            }
        }
        return "";
    }
    /**
     * 判断此工单此时的状态是否可申请关闭
     * 可申请关闭时返回下一工单状态码
     * 规则：检查中、维修中、待出发（外出救援）、待接车状态的工单才能进行申请关闭操作
     */
    private Integer getCanCloseAndNextWoStatus(WorkOrder workOrder) {
        if (ServiceStationEnum.INSPECTING.code() == workOrder.getWoStatus()) {
            return ServiceStationEnum.CLOSE_APPLYING_INSPECT.code();
        } else if (ServiceStationEnum.REPAIRING.code() == workOrder.getWoStatus()) {
            return ServiceStationEnum.CLOSE_APPLYING_REPAIR.code();
        }else if (ServiceStationEnum.TO_RECEIVE.code() == workOrder.getWoStatus()) {
            //待接车
            return ServiceStationEnum.CLOSE_APPLYING_RECEIVE.code();
        }else if (workOrder.getWoType() == 2 && ServiceStationEnum.TO_TAKE_OFF.code() == workOrder.getWoStatus()) {
            //待出发
            return ServiceStationEnum.CLOSE_APPLYING_TAKEOFF.code();
        }
        return 0;
    }
    /**
     * 更新工单表、插入操作记录
     */
    private void updateWoInsertRecord(WorkOrder workOrder, ApplyCloseWoForm command, Integer nextWoStatus, String userId)
            throws JsonProcessingException {
        // 系统时间
        Date date = new Date();

        // 更新工单表信息
        WorkOrder upDataWorkOrder = new WorkOrder();
        upDataWorkOrder.setId(workOrder.getId());
        upDataWorkOrder.setWoStatus(nextWoStatus);
        upDataWorkOrder.setCloseType(Integer.valueOf(command.getCloseType()));
        upDataWorkOrder.setCloseReason(command.getCloseReason());
        log.info("被指派人的账号：{}",workOrder.getAssignTo());
        log.info("申请关闭人的账号：{}",userId);
        if (null != workOrder.getAssignTo() && !"".equals(workOrder.getAssignTo())) {
            log.info("管理员指派了维修人员，账号：{}",userId);
            if (workOrder.getAssignTo().equals(userId)) {
                if (null != command.getDeviceId() && !"".equals(command.getDeviceId())) {
                    upDataWorkOrder.setDeviceId(command.getDeviceId());
                }
            }
        }
        upDataWorkOrder.setUpdateTime(date);
        upDataWorkOrder.setTimeApplyclose(date);
        if (workOrder.getCloseTimes() != null) {
            upDataWorkOrder.setCloseTimes(workOrder.getCloseTimes() + 1);
        } else {
            upDataWorkOrder.setCloseTimes(1);
        }
        workOrderMapper.updateByPrimaryKeySelective(upDataWorkOrder);

        // 增操作记录信息
        WorkOrderOperate workOrderOperate = new WorkOrderOperate();
        workOrderOperate.setWoCode(workOrder.getWoCode());
        workOrderOperate.setOperateCode(OperateCodeEnum.OP_CLOSE_APPLY.code());
        workOrderOperate.setTitle(OperateCodeEnum.OP_CLOSE_APPLY.message());
        workOrderOperate.setUserId(userId);
        workOrderOperate.setCreateTime(date);
        workOrderOperate.setUpdateTime(date);
        // 服务记录显示内容用json字符串格式保存
        Map<String, String> map = new LinkedHashMap<>(4);
        map.put("关闭类型：", RefuseOrCloseEnum.getMessage(Integer.valueOf(command.getCloseType())));
        map.put("关闭原因：", command.getCloseReason());
        map.put("申请账号：", userId);
        map.put("申请时间：", DateUtil.getDatePattern(date.getTime()));
        workOrderOperate.setTextJson(JsonUtil.toJson(map));
        workOrderOperateMapper.insertSelective(workOrderOperate);
    }
    /**
     * 判断此工单此时的状态是否可申请修改
     * 可申请修改时返回下一工单状态码
     * 规则：待接受、待出发、待接车状态的工单才能进行申请修改操作
     */
    private Integer getCanModifyAndNextWoStatus(WorkOrder workOrder) {
        if (ServiceStationEnum.TO_BE_ACCEPTED.code() == workOrder.getWoStatus()) {
            return ServiceStationEnum.MODIFY_APPLYING_ACCEPT.code();
        } else if (ServiceStationEnum.TO_TAKE_OFF.code() == workOrder.getWoStatus()) {
            return ServiceStationEnum.MODIFY_APPLYING_TAKEOFF.code();
        } else if (ServiceStationEnum.TO_RECEIVE.code() == workOrder.getWoStatus()) {
            return ServiceStationEnum.MODIFY_APPLYING_RECEIVE.code();
        }
        return 0;
    }
}
