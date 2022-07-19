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
     * 外出人员姓名最大字节长度
     */
    private static final int BYTE_LENGTH = 40;

    /**
     * 外出人员姓名最多汉字个数
     */
    private static final int CHINESE_MAX = 20;

    /**
     * 计算字节长度所设置的字符集
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
     * 进出站 最大拒绝接单次数
     */
    @Value("${refuse_max_times:3}")
    private Integer refuseMaxTimes;

    /**
     * 外出救援 最大拒绝接单次数
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
        // 参数校验
        String errMsg = validateCommand(command);
        if (StringUtil.isNotEmpty(errMsg)) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage(errMsg);
            return result;
        }
        // 用户信息查询
        UserInfoDto userInfoDto = userService.getUserInfoByUserId(command.getUserId(), false);
        if (userInfoDto == null) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("此用户不存在");
            return result;
        }
        /**
         * 2021-05-17:权限注释，跳过管理员与维修员的权限判断

         if (userInfoDto.getRoleCode() != ServiceStationVal.JOB_TYPE_SALESMAN) {
         result.setResultCode(ReturnCode.CLIENT_ERROR.code());
         result.setMessage("此用户没有操作权限");
         return result;
         }
         */
        Map<String, String> queryMap = new HashMap<>(2);
        queryMap.put("woCode", command.getWoCode());
        queryMap.put("stationId", userInfoDto.getServiceStationId());
        // 查询工单信息
        WorkOrder workOrder = workOrderMapper.selectByWoCode(queryMap);
        if (workOrder == null) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("未获取到该工单信息");
            return result;
        }
        /**
         * 2021-05-24 角色判断，如果是此工单的维修员，则可以接车，否者提示不是
         */
        if (!command.getUserId().equals(workOrder.getAssignTo())) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("您不是当前工单的指派人员，无权操作");
            return result;
        }
        // 校验状态
        errMsg = validateState(workOrder, command.getUserId(), userInfoDto.getServiceCode());
        if (StringUtil.isNotEmpty(errMsg)) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage(errMsg);
            return result;
        }
        // 系统时间
        Date date = new Date();
        /* 插入轨迹点数据 */
        Map<String, Integer> outParam = new HashMap<>(4);
        insertRescueRoutePoint(workOrder, command, command.getUserId(), date, outParam);
        // 预计救援里程导航距离
        int actualMileage = outParam.get(METERS);
        // 预计救援用时（分钟）
        int actualMinute = outParam.get(MINUTES);
        /* 添加工单操作记录 */
        Map<String, String> outParam2 = new HashMap<>(4);
        insertWorkOrderOperate(workOrder, command, command.getUserId(), date, actualMileage, actualMinute, outParam2);
        String cueRange = outParam2.get(CUERANGE);
        String cueTime = outParam2.get(CUETIME);
        /* 更新工单表信息 */
        updateWorkOrder(workOrder, command, date, cueTime);

        /* 保存外出信息 */
        saveOutDetail(workOrder, command, date);

        /* 推送 */
        pushRescueTakeOff(workOrder, command.getUserId());
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());
        StringBuilder builder = new StringBuilder();
        builder.append("距离故障地点");
        builder.append(cueRange);
        builder.append("km，预计");
        builder.append(cueTime);
        builder.append("到达。");
        result.setMessage(builder.toString());
        return result;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public HttpCommandResultWithData refuseOrder(RefuseOrderForm command) throws JsonProcessingException {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        // 用户信息查询
        UserInfoDto userInfoDto = userService.getUserInfoByUserId(command.getUserId(), false);
        if (userInfoDto != null) {
            if (userInfoDto.getRoleCode() != ServiceStationVal.JOB_TYPE_ADMIN) {
                result.setResultCode(ECode.CLIENT_ERROR.code());
                result.setMessage("此用户没有操作权限");
                return result;
            }
            Map<String, String> queryMap = new HashMap<>(2);
            queryMap.put("woCode", command.getWoCode());
            queryMap.put("stationId", userInfoDto.getServiceStationId());
            // 查询工单信息
            WorkOrder workOrder = workOrderMapper.selectByWoCode(queryMap);
            if (workOrder == null) {
                result.setResultCode(ECode.CLIENT_ERROR.code());
                result.setMessage("未获取到该工单信息");
                return result;
            }
            // 校验工单状态是否为待接受
            if (workOrder.getWoStatus() != ServiceStationEnum.TO_BE_ACCEPTED.code()) {
                result.setResultCode(ECode.CLIENT_ERROR.code());
                result.setMessage("当前工单状态无法拒绝接单");
                return result;
            }
            // 校验是否超过拒单次数限制
            int limit = 99;
            if (workOrder.getWoType() == ServiceStationVal.STATION_SERVICE) {
                limit = refuseMaxTimes;
            } else if (workOrder.getWoType() == ServiceStationVal.OUTSIDE_RESCUE) {
                limit = refuseMaxTimesRescue;
            }
            if (workOrder.getRefuseTimes() != null && workOrder.getRefuseTimes() >= limit) {
                result.setResultCode(ECode.CLIENT_ERROR.code());
                result.setMessage("已超过拒绝接单次数限制");
                return result;
            }
            // 系统时间
            Date date = new Date();
            // 添加工单操作记录
            WorkOrderOperate workOrderOperate = new WorkOrderOperate();
            workOrderOperate.setWoCode(workOrder.getWoCode());
            workOrderOperate.setOperateCode(OperateCodeEnum.OP_REFUSE_APPLY.code());
            workOrderOperate.setTitle(OperateCodeEnum.OP_REFUSE_APPLY.message());
            workOrderOperate.setUserId(userInfoDto.getAccountId());
            workOrderOperate.setCreateTime(date);
            workOrderOperate.setUpdateTime(date);
            // 服务记录显示内容用json字符串格式保存
            Map<String, String> jsonMap = new LinkedHashMap<>(5);
            jsonMap.put("拒单类型：", RefuseOrCloseEnum.getMessage(Integer.valueOf(command.getRefuseType())));
            jsonMap.put("拒绝理由：", command.getRefuseReason());
            jsonMap.put("拒单账号：", userInfoDto.getAccountId());
            jsonMap.put("拒单时间：", DateUtil.getDatePattern(date.getTime()));
            jsonMap.put("拒单服务站：", workOrder.getStationName());
            workOrderOperate.setTextJson(JsonUtil.toJson(jsonMap));
            workOrderOperateMapper.insertSelective(workOrderOperate);
            // 更新工单表信息
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

            // 进站工单是否存在进站预警，存在时确认接单后解除预警
            if (workOrder.getWoType() == ServiceStationVal.STATION_SERVICE) {
                // 由底盘号查询车辆ID
                String carId = carMapper.queryCarIdByVin(workOrder.getChassisNum());
                warnInTheStationService.updWarningStatus(carId, workOrder.getStationCode(), ServiceStationVal.INQ_REFUSE);
            }

            result.setResultCode(ECode.SUCCESS.code());
            result.setMessage(ECode.SUCCESS.message());
            Map<String, Object> data = new HashMap<>(3);
            int canRefuseCount = limit - updateWorkOrder.getRefuseTimes();
            data.put("canRefuseCount", canRefuseCount);
            result.setData(data);
            result.setMessage("已提交成功，您还剩" + (canRefuseCount) + "次拒单机会！");
        } else {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("此用户不存在");
        }
        return result;
    }

    @Override
    public HttpCommandResultWithData scanWo(ScanWoForm command) {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        // 用户信息查询
        UserInfoDto userInfoDto = userService.getUserInfoByUserId(command.getUserId(), false);
        if (userInfoDto != null) {
            PagingInfo<QueryWoListByStatusDto> resultPageInfo = new PagingInfo<>();
            Map<String, String> queryMap = new HashMap<>(2);
            queryMap.put("stationId", userInfoDto.getServiceStationId());
            queryMap.put("chassisNum", command.getChassisNum());
            getPage(command, true);
            Page<QueryWoListByStatusPojo> pojoPageList = workOrderMapper.queryWorkOrderByVin(queryMap);
            /* dto转换 */
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
            result.setMessage("此用户不存在");
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public HttpCommandResultWithData acceptOrder(AcceptOrderForm command) throws JsonProcessingException {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        // 用户信息查询
        UserInfoDto userInfoDto = userService.getUserInfoByUserId(command.getUserId(), false);
        if (userInfoDto != null) {
            if (userInfoDto.getRoleCode() != ServiceStationVal.JOB_TYPE_ADMIN) {
                result.setResultCode(ECode.CLIENT_ERROR.code());
                result.setMessage("此用户没有操作权限");
                return result;
            }
            //查询是否是抢单信息，如果是抢单则更新抢单信息内容
            log.info("抢单操作开始");
            WorkOrder order = workOrderMapper.queryWorkOrderStation(command.getWoCode(), userInfoDto.getServiceStationId());
            if (order != null) {
                //更新工抢单信息为已绑定
                workOrderMapper.updateWorkOrderStationBind(order);
                //更新抢单信息为已被其他服务站绑定
                workOrderMapper.updateWorkOrderStationNoBind(order);
                //更新工单信息
                workOrderMapper.updateWorkOrder(order);
                log.info("抢单操作结束 param:{}", order);
            }

            Map<String, String> queryMap = new HashMap<>(2);
            queryMap.put("woCode", command.getWoCode());
            queryMap.put("stationId", userInfoDto.getServiceStationId());
            // 查询工单信息
            WorkOrder workOrder = workOrderMapper.selectByWoCode(queryMap);
            if (workOrder == null) {
                result.setResultCode(ECode.CLIENT_ERROR.code());
                result.setMessage("未获取到该工单信息");
                return result;
            }
            // 校验工单状态是否为待接受
            if (workOrder.getWoStatus() != ServiceStationEnum.TO_BE_ACCEPTED.code()) {
                result.setResultCode(ECode.CLIENT_ERROR.code());
                result.setMessage("当前工单状态无法确认接单");
                return result;
            }
            UserInfoDto assignUserInfo = null;
            // 校验外出救援工单时付费类型和指派人员ID是否为空
            if (workOrder.getWoType() == ServiceStationVal.OUTSIDE_RESCUE) {
                if (StringUtil.isEmpty(command.getAssignTo())) {
                    result.setResultCode(ECode.CLIENT_ERROR.code());
                    result.setMessage("请选择指派人员");
                    return result;
                } else {
                    assignUserInfo = userService.getUserInfoByUserId(command.getAssignTo(), false);
                    if (assignUserInfo != null) {
                        if (!assignUserInfo.getServiceStationId().equals(workOrder.getStationId())) {
                            result.setResultCode(ECode.CLIENT_ERROR.code());
                            result.setMessage("所选外出人员不在该工单服务站下");
                            return result;
                        }
                        /**
                         * 2021-05-24 维修员可以成为所选外出角色
                         */
//                        if (assignUserInfo.getRoleCode() != ServiceStationVal.JOB_TYPE_SALESMAN) {
//                            result.setResultCode(ReturnCode.CLIENT_ERROR.code());
//                            result.setMessage("所选外出人员角色错误");
//                            return result;
//                        }
                    } else {
                        result.setResultCode(ECode.CLIENT_ERROR.code());
                        result.setMessage("所选外出人员不存在");
                        return result;
                    }
                }
            }
            // 系统时间
            Date date = new Date();
            // 添加工单操作记录
            WorkOrderOperate workOrderOperate = new WorkOrderOperate();
            workOrderOperate.setWoCode(workOrder.getWoCode());
            workOrderOperate.setOperateCode(OperateCodeEnum.OP_ACCEPT.code());
            workOrderOperate.setTitle(OperateCodeEnum.OP_ACCEPT.message());
            workOrderOperate.setUserId(userInfoDto.getAccountId());
            workOrderOperate.setCreateTime(date);
            workOrderOperate.setUpdateTime(date);
            // 服务记录显示内容用json字符串格式保存
            Map<String, String> jsonMap = new LinkedHashMap<>(6);
            if (workOrder.getWoType() == ServiceStationVal.OUTSIDE_RESCUE && assignUserInfo != null) {
                jsonMap.put("指派人员：", assignUserInfo.getAccountId());
            }
            jsonMap.put("接单账号：", userInfoDto.getAccountId());
            jsonMap.put("接单时间：", DateUtil.getDatePattern(date.getTime()));
            jsonMap.put("接单服务站：", workOrder.getStationName());
            workOrderOperate.setTextJson(JsonUtil.toJson(jsonMap));
            workOrderOperateMapper.insertSelective(workOrderOperate);
            // 更新工单表信息
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

            // 进站工单是否存在进站预警，存在时确认接单后解除预警
            if (workOrder.getWoType() == ServiceStationVal.STATION_SERVICE) {
                // 由底盘号查询车辆ID
                String carId = carMapper.queryCarIdByVin(workOrder.getChassisNum());
                warnInTheStationService.updWarningStatus(carId, workOrder.getStationCode(), ServiceStationVal.INQ_ACCEPT);
            }

            /* 推送 */
            pushAcceptOrder(workOrder, command.getUserId());

            /* 触发报单流程 */
            startEndRepairService.trySendKafka(workOrder.getWoCode(), ServiceStationVal.WEB_SERVICE_CREATESMORDER, "确认接单");
            //todo 用于延迟队列，暂时功能隐藏
      /*      //删除剩余消费通知
            List<DelayMessageEntity> messageEntities = queueComponent.pull();
            messageEntities.forEach(msg ->{
                //获取所有消息
                DelayMessageEntity.MessageBody messageBody = JsonUtil.fromJson(msg.getBody(), DelayMessageEntity.MessageBody.class);
                //拿到工单号
                Map map = JsonUtil.fromJson(messageBody.getMessageExtra(), Map.class);
                //将工单号转换为Map<String, Object>
                Map<String, Object> convert = Convert.convert(new TypeReference<Map<String, Object>>() {}, map);
                //拿到工单号
                String woCode = String.valueOf(convert.get(PushStaticLocarVal.MESSAGE_EXTRA_WO_CODE));
                //对比当前工单，如果有就删除当前消息，避免二次通知
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
            result.setMessage("此用户不存在");
        }
        return result;
    }

    @Override
    public HttpCommandResultWithData scanOrder(ScanOrderForm form) {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        // 用户信息查询
        UserInfoDto userInfoDto = userService.getUserInfoByUserId(form.getUserId(), false);
        if (userInfoDto != null) {
            String vin = form.getChassisNum();  // 底盘号
            String assignTo = userInfoDto.getAccountId();  // 指派人员
            String stationId = userInfoDto.getServiceStationId();  // 服务站ID
            List<Map<String, Object>> list1 = new ArrayList<>();
            List<Map<String, Object>> list2 = new ArrayList<>();
            Integer roleCode = userInfoDto.getRoleCode();
            List<Map<String, Object>> listRuning = workOrderMapper.queryWoCodeByVin(vin, stationId);
            if (roleCode == 1) {
                // 接单员
                if (listRuning.size() > 0) {
                    for (Map<String, Object> map : listRuning) {
                        // 工单筛选-获取属于接单员可操作的工单
                        String woCode = map.get("woStatus").toString();
                        // 针对待接工单
                        if (woCode.equals("100") || woCode.equals("140") || woCode.equals("220")) {
                            list1.add(map);  // 接单员可操作性工单
                            continue;
                        }
                        // 针对指定角色
                        Object name = map.get("assignTo");
                        if (name == null) {
                            list2.add(map); // 接单员不可操作工单
                            continue;
                        }
                        if (assignTo.equals(name.toString())) {
                            list1.add(map);  // 接单员可操作性工单
                            continue;
                        } else {
                            list2.add(map);
                            continue;
                        }
                    }
                    //  当接单员可操作工单数大于等于1时，返回最新工单信息给前台
                    if (list1.size() >= 1) {
                        String woCode = list1.get(0).get("woCode").toString();
                        log.info("查询到的最近工单为：{}", woCode);
                        // 获取工单信息
                        List<QueryWoListByStatusPojo> list = new ArrayList<>();
                        QueryWoListByStatusPojo pojo = workOrderMapper.queryWorkOrderByWoCodeNew(woCode);
                        list.add(pojo);
                        Map<String, Object> map = new HashMap<>();
                        map.put("numberType", 1);
                        map.put("list", list);
                        result.setData(map);
                        return result;
                    } else {
                        // 接单员不可以操作，但此车辆处于进行中的工单
                        // 获取工单信息
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
                    // 接单员没有正在进行中的工单
                    List<Map<String, Object>> listNotRuning = workOrderMapper.queryWoCodeByVin2(vin, stationId);
                    if (listNotRuning.size() != 0) {
                        // 当存在非进行中状态的工单
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
                        // 当不存在非进行中状态的工单
                        Map<String, Object> map = new HashMap<>();
                        map.put("numberType", 0);
                        map.put("list", null);
                        result.setData(map);
                        result.setMessage("暂无工单");
                        return result;
                    }
                }
            } else if (roleCode == 2) {
                if (listRuning.size() > 0) {
                    for (Map<String, Object> map : listRuning) {
                        // 工单筛选-获取属于接单员可操作的工单
                        // 针对指定角色
                        Object name = map.get("assignTo");
                        if (name == null) {
                            continue;
                        }
                        if (assignTo.equals(name.toString())) {
                            list1.add(map);  // 接单员可操作性工单
                            continue;
                        }
                    }
                    //  当维修员可操作工单数大于等于1时，返回最新工单信息给前台
                    if (list1.size() >= 1) {
                        String woCode = list1.get(0).get("woCode").toString();
                        log.info("查询到的最近工单为：{}", woCode);
                        // 获取工单信息
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
                    // 维修员没有正在进行中的工单
                    List<Map<String, Object>> listNotRuning = workOrderMapper.queryWoCodeByVin2(vin, stationId);
                    if (listNotRuning.size() != 0) {
                        // 当存在非进行中状态的工单
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
                        // 当不存在非进行中状态的工单
                        Map<String, Object> map = new HashMap<>();
                        map.put("numberType", 0);
                        map.put("list", null);
                        result.setData(map);
                        result.setMessage("暂无工单");
                        return result;
                    }
                }
            }
        } else {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("此用户不存在");
        }

        return result;
    }

    /**
     * 接单推送
     */
    private void pushAcceptOrder(WorkOrder workOrder, String senderId) {
        log.info("[pushAcceptOrder]start");
        try {
            // 通配符替换
            Map<String, String> wildcardMap = new HashMap<>(7);
            String chassisNum = workOrder.getChassisNum();
            String carNumber = carMapper.queryCarNumberByVin(chassisNum);
            wildcardMap.put("{优先车牌号}", StringUtil.isNotEmpty(carNumber) ? carNumber : chassisNum);
            wildcardMap.put("{工单号}", workOrder.getWoCode());
            String stype;
            if (ServiceStationVal.STATION_SERVICE == workOrder.getWoType()) {
                // 进出站
                wildcardMap.put("{预约时间}", DateUtil.formatDate(DateUtil.time_pattern, workOrder.getAppoArriveTime()));
                wildcardMap.put("{服务站名称}", workOrder.getStationName());
                stype = PushStaticLocarVal.PUSH_TYPE_TEN_STYPE_ACCEPT_IN;
            } else {
                // 外出救援
                stype = PushStaticLocarVal.PUSH_TYPE_TEN_STYPE_ACCEPT_OUT;
            }
            String wildcard = JsonUtil.toJson(wildcardMap);
            // 扩展信息
            Map<String, String> messageExtraMap = new HashMap<>(3);
            // 工单号
            messageExtraMap.put(PushStaticLocarVal.MESSAGE_EXTRA_WO_CODE, workOrder.getWoCode());
            String messageExtra = JsonUtil.toJson(messageExtraMap);


            asyPushMessageService.pushToDriverAndOwner(stype, wildcard, messageExtra, senderId, null, chassisNum);
        } catch (Exception e) {
            log.info("[pushAcceptOrder]Exception:", e);
        }
        log.info("[pushAcceptOrder]end");
    }

    /**
     * 工单pojo转dto
     *
     * @param pojo 工单pojo
     * @return QueryWoListByStatusDto 出参对象dto
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
        // 指派人员
        dto.setAssignTo(pojo.getAssignTo());
//        dto.setProtocolMark(pojo.getProtocolMark());
        return dto;
    }

    /**
     * 根据工单状态和工单类型选择工单时间
     * <h2>【注意！】增加新的工单状态枚举时需要密切关注的代码！搜tag: ATTENTION_add_new_woStatus_DOUBLE_CHECK</h2>
     *
     * @param pojo 工单pojo
     * @return String 工单时间
     */
    private static String getTimeByStatus(QueryWoListByStatusPojo pojo) {
        String time = null;
        int status = pojo.getWoStatus();
        if (ServiceStationEnum.TO_BE_ACCEPTED.code() == status
                || ServiceStationEnum.REFUSE_APPLYING.code() == status
                || ServiceStationEnum.MODIFY_APPLYING_ACCEPT.code() == status) {
            // 创建时间
            time = DateUtil.formatDate(DateUtil.time_pattern, pojo.getTimeCreate());

        } else if (ServiceStationEnum.TO_TAKE_OFF.code() == status
                || ServiceStationEnum.MODIFY_APPLYING_TAKEOFF.code() == status
                || ServiceStationEnum.CLOSE_APPLYING_TAKEOFF.code() == status) {
            // 接单时间
            time = DateUtil.formatDate(DateUtil.time_pattern, pojo.getTimeAccept());

        } else if (ServiceStationEnum.TO_RECEIVE.code() == status
                || ServiceStationEnum.MODIFY_APPLYING_RECEIVE.code() == status
                || ServiceStationEnum.CLOSE_APPLYING_RECEIVE.code() == status) {
            if (ServiceStationVal.STATION_SERVICE == pojo.getWoType()) {
                // 接单时间
                time = DateUtil.formatDate(DateUtil.time_pattern, pojo.getTimeAccept());
            } else if (ServiceStationVal.OUTSIDE_RESCUE == pojo.getWoType()) {
                // 出发时间
                time = DateUtil.formatDate(DateUtil.time_pattern, pojo.getTimeDepart());
            }

        } else if (ServiceStationEnum.CLOSE_APPLYING_INSPECT.code() == status
                || ServiceStationEnum.INSPECTING.code() == status) {
            // 接车时间
            time = DateUtil.formatDate(DateUtil.time_pattern, pojo.getTimeReceive());

        } else if (ServiceStationEnum.CLOSE_APPLYING_REPAIR.code() == status
                || ServiceStationEnum.REPAIRING.code() == status) {
            // 检查结束时间
            time = DateUtil.formatDate(DateUtil.time_pattern, pojo.getTimeInspected());

        } else if (ServiceStationEnum.CLOSE_REFUSED.code() == status
                || ServiceStationEnum.CLOSE_INSPECT.code() == status
                || ServiceStationEnum.CLOSE_REPAIR.code() == status
                || ServiceStationEnum.CLOSE_RESCUE.code() == status
                || ServiceStationEnum.WORK_DONE.code() == status
                || ServiceStationEnum.EVALUATED.code() == status
                || ServiceStationEnum.CLOSE_TAKEOFF.code() == status
                || ServiceStationEnum.CLOSE_RECEIVE.code() == status) {
            // 关闭时间
            time = DateUtil.formatDate(DateUtil.time_pattern, pojo.getTimeClose());
        }
        return time;
    }

    /**
     * 入参校验
     */
    private String validateCommand(RescueGoForm command) {
        // 校验参数外出人员姓名：最多可输入20个汉字，或40个字节。
        String name = command.getName();
        if (StringUtil.isNotEmpty(name)) {
            if (name.getBytes(Charset.forName(CHAR_SET)).length > BYTE_LENGTH) {
                return "最多可输入20个汉字，或40个字节。";
            } else {
                if (StringUtil.findNotAscill(name) > CHINESE_MAX) {
                    return "最多可输入20个汉字，或40个字节。";
                }
            }
        }
        return "";
    }

    /**
     * 校验状态
     */
    private String validateState(WorkOrder workOrder, String userId, String stationCode) {
        // 校验工单状态是否为待出发
        if (workOrder.getWoStatus() != ServiceStationEnum.TO_TAKE_OFF.code()) {
            return "当前工单状态无法确认出发";
        }
        // 校验工单是否有未到货确认的调件信息
        List<WorkOrderTransferParts> transferParts = workOrderTransferPartsMapper.selectByWoCodeAndOperateId(workOrder.getWoCode(), null);
        if (transferParts.size() > 0) {
            return "有未到货的调件信息，请确认！";
        }
        // 校验工单是否在外出救援路线轨迹点记录表里无记录
        if (rescueRoutePointMapper.selectByWoCode(workOrder.getWoCode()) != null) {
            return "当前工单状态异常";
        }
        // 校验此用户是否有在途的外出工单
        List<Integer> woStatusList = Arrays.asList(ServiceStationEnum.MODIFY_APPLYING_RECEIVE.code(),
                ServiceStationEnum.TO_RECEIVE.code(), ServiceStationEnum.INSPECTING.code(),
                ServiceStationEnum.CLOSE_APPLYING_INSPECT.code(), ServiceStationEnum.REPAIRING.code(),
                ServiceStationEnum.CLOSE_APPLYING_REPAIR.code(), ServiceStationEnum.CLOSE_APPLYING_RECEIVE.code());
        Long woCount = workOrderMapper.countAlreadyDepartWo(userId, stationCode, ServiceStationVal.OUTSIDE_RESCUE, woStatusList);
        if (woCount != null && woCount > 0) {
            return "有未结束的外出救援工单，请结束后再开始新的工单！";
        }
        return "";
    }

    /**
     * 插入轨迹点数据
     */
    private void insertRescueRoutePoint(WorkOrder workOrder, RescueGoForm command, String userId, Date date,
                                        Map<String, Integer> outParam) throws JsonProcessingException {
        log.info("[insertRescueRoutePoint]start");
        RescueRoutePoint rescueRoutePoint = new RescueRoutePoint();
        rescueRoutePoint.setWoCode(workOrder.getWoCode());
        rescueRoutePoint.setUserId(userId);
        rescueRoutePoint.setCreateTime(date);
        rescueRoutePoint.setUpdateTime(date);
        // 目的地的经纬度
        String carLon = workOrder.getCarLon();
        String carLat = workOrder.getCarLat();
        // 预计救援里程导航距离
        int actualMileage = 0;
        // 预计救援用时（分钟）
        int actualMinute = 0;
        if (StringUtil.isNotEmpty(carLon) && StringUtil.isNotEmpty(carLat)) {
            JSONObject jsonObject = baiduMapComponent.distance2point(
                    command.getLat(), command.getLon(), carLat, carLon, "bd09ll")
                    .getJSONObject("result");
            String distance = jsonObject.getJSONArray("routes").getJSONObject(0).get("distance").toString();
            log.info("预计里程：{}", distance);
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
        // 点信息用json字符串格式保存
        LatLngDto latLngDto = new LatLngDto();
        // 出发位置点默认index:1
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
            log.info("工单：{}，二次外出立即出发", workOrder.getWoCode());
            redisKey = pointsToRedisKeyPrefixTwo + workOrder.getWoCode();
        } else {
            log.info("工单：{}，一次外出立即出发", workOrder.getWoCode());
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
     * 插入工单操作记录
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
        // 服务记录显示内容用json字符串格式保存
        Map<String, String> jsonMap = new LinkedHashMap<>(16);
        String cueRange = StringUtil.valueOf(MathUtil.scale((double) actualMileage / 1000, 1).doubleValue());
        String cueTime = DateUtil.getDatePattern(date.getTime() + actualMinute * 60 * 1000);
        jsonMap.put("外出人员：", command.getName());
        jsonMap.put("外出人员电话：", command.getPhone());
        jsonMap.put("外出人数：", command.getNum());
        jsonMap.put("GPS设备号：", StringUtil.isEmpty(command.getGpsDeviceNo()) ? "无GPS设备" : command.getGpsDeviceNo());
        jsonMap.put("预计行驶里程：", cueRange + "km");
        jsonMap.put("预计到达时间：", cueTime);
        jsonMap.put("救援账号：", userId);
        jsonMap.put("出发时间：", DateUtil.getDatePattern(date.getTime()));
        jsonMap.put("出发位置：", queryGeographicalService.getPosition(command.getLat(), command.getLon()));
        workOrderOperate.setTextJson(JsonUtil.toJson(jsonMap));
        workOrderOperateMapper.insertSelective(workOrderOperate);

        if (outParam != null) {
            outParam.put(CUERANGE, cueRange);
            outParam.put(CUETIME, cueTime);
        }
        log.info("[insertWorkOrderOperate]end");
    }

    /**
     * 更新工单信息
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
        // 轨迹点是否完整初始态
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
     * 推送
     */
    private void pushRescueTakeOff(WorkOrder workOrder, String senderId) {
        log.info("[pushRescueTakeOff]start");
        try {
            // 通配符替换
            Map<String, String> wildcardMap = new HashMap<>(4);
            String chassisNum = workOrder.getChassisNum();
            String carNumber = carMapper.queryCarNumberByVin(chassisNum);
            wildcardMap.put("{优先车牌号}", StringUtil.isNotEmpty(carNumber) ? carNumber : chassisNum);
            wildcardMap.put("{工单号}", workOrder.getWoCode());
            String wildcard = JsonUtil.toJson(wildcardMap);
            // 扩展信息
            Map<String, String> messageExtraMap = new HashMap<>(3);
            // 工单号
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
