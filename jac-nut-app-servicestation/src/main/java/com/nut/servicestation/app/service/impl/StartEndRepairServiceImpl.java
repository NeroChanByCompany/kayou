package com.nut.servicestation.app.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.nut.common.constant.OperateCodeEnum;
import com.nut.common.constant.ServiceStationVal;
import com.nut.common.enums.ServiceStationEnum;
import com.nut.common.push.PushStaticLocarVal;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.utils.DateUtil;
import com.nut.common.utils.JsonUtil;
import com.nut.common.utils.StringUtil;
import com.nut.servicestation.app.client.ToolsClient;
import com.nut.servicestation.app.dao.*;
import com.nut.servicestation.app.domain.*;
import com.nut.servicestation.app.dto.PointDto;
import com.nut.servicestation.app.form.EndRepairForm;
import com.nut.servicestation.app.form.StartInspectForm;
import com.nut.servicestation.app.pojo.WoRepairPhotoPojo;
import com.nut.servicestation.app.service.*;
import com.nut.servicestation.common.assembler.RedisDistributedLock;
import com.nut.servicestation.app.dto.UserInfoDto;
import com.nut.servicestation.app.form.StartRepairForm;
import com.nut.servicestation.common.utils.MailSenderUtil;
import com.nut.tools.app.form.SendSmsForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.nut.common.constant.ServiceStationVal.*;

/*
 *  @author wuhaotian 2021/7/2
 */
@Slf4j
@Service("StartEndRepairService")
public class StartEndRepairServiceImpl implements StartEndRepairService {

    /**
     * 输入描述最多字符个数
     */
    private static final int DESC_MAX_LENGTH = 300;
    private static final int DEFAULT_VALUE = -1;
    private static final String YES = "1";
    private static final String REDIX_LOCK_KEY = "distlock_op_";


    @Value("${distributedLockEnable:true}")
    private Boolean distributedLockEnable;
    @Autowired
    private RedisDistributedLock redisDistributedLock;
    @Autowired
    private UserService userService;
    @Autowired
    private WorkOrderDao workOrderMapper;
    @Autowired
    private WorkOrderOperatePhotoDao workOrderOperatePhotoMapper;
    @Autowired
    private WorkOrderOperateDao workOrderOperateMapper;
    @Autowired
    private CarDao carMapper;
    @Autowired
    private AsyPushMessageService asyPushMessageService;
    @Value("${pointsToRedisKeyPrefix.two:296bba4fc}")
    private String pointsToRedisKeyPrefixTwo;
    /** 调用crm系统接口开关 */
    @Value("${syncWorkToCrmSwitch:false}")
    private boolean syncWorkToCrmSwitch;
  /*  @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;*/
    @Value("${kafka.producer.topic.crm.synchronize.info}")
    private String workOrderToCrmTopic;
    /** 错误报告邮件接收人 */
    @Value("${syncWorkToCrmErrEmails:dummy}")
    private String syncWorkToCrmErrEmails;
    /** 发送邮件服务url */
    @Value("${sendEmailServerUrl}")
    private String sendEmailServerUrl;
    /** 邮件环境区分标识 */
    @Value("${envIdentifier:未配置环境名}")
    private String envIdentifier;
    @Autowired
    private WorkOrderOutDetailDao workOrderOutDetailMapper;
    @Autowired
    private RescueRoutePointDao rescueRoutePointMapper;
    @Value("${pointsToRedisKeyPrefix.one:196bba4fc}")
    private String pointsToRedisKeyPrefixOne;
    @Autowired
    private RescueRoutePointHistoryDao rescueRoutePointHistoryMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ScanReceiveService scanReceiveService;
    @Autowired
    private UserDao userMapper;
    @Autowired
    private ToolsClient toolsClient;
    @Autowired
    private DistanceAnomalyService distanceAnomalyService;
    @Autowired
    private AsyCheckPointCompletenessService asyCheckPointCompletenessService;
    @Autowired
    private AsySaveMeterMileageWoService asySaveMeterMileageWoService;
    /**
     * 短信模板
     */
    @Value("${sms.template.end.repair:车辆{优先车牌号}，工单{工单号}已在{服务站名称}维修完毕，请对服务站本次的服务进行评价，您认真的评价是我们前进的动力。}")
    private String smsTemplate;
    /** 同步完好率中心接口开关 */
    @Value("${syncUptimeSwitch:false}")
    private boolean uptimeSwitch;



    @Override
    public boolean lock(String woCode, String timestamp) {
        log.info("[lock]woCode:{}||timestamp:{}||swt:{}", woCode, timestamp, distributedLockEnable);
        if (!distributedLockEnable) {
            return true;
        }
        // 锁超时时间4分钟
        return redisDistributedLock.lock(REDIX_LOCK_KEY + woCode + timestamp, 4 * 60 * 1000, 0, 100);
    }

    @Override
    @Transactional(timeout = 180,rollbackFor = Exception.class)
    public HttpCommandResultWithData startRepair(StartRepairForm command) throws JsonProcessingException {
        log.info("[startRepair]start");
        HttpCommandResultWithData<List<Long>> result = new HttpCommandResultWithData<>();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        // 入参校验
        String error = validateCommand(command);
        if (StringUtil.isNotEmpty(error)) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage(error);
            return result;
        }

        // 获取用户信息和工单信息
        UserInfoDto userInfo = userService.getUserInfoByUserId(command.getUserId(), false);
        if (userInfo == null) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("未获取到用户信息");
            return result;
        }
        Map<String, String> param = new HashMap<>(1);
        param.put("woCode", command.getWoCode());
        WorkOrder workOrder = workOrderMapper.selectByWoCode(param);
        if (workOrder == null) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("未获取到该工单信息");
            return result;
        }

        // 数据校验
        error = validateData(command, userInfo, workOrder);
        if (StringUtil.isNotEmpty(error)) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage(error);
            return result;
        }

        // 工单类型为进出站，且登录用户的权限为经理账户时，校验图片的总数量，及各种类型照片的数量
        if (userInfo.getRoleCode() == JOB_TYPE_ADMIN && workOrder.getWoType() == STATION_SERVICE) {
            Map<String, Object> photoParam = new HashMap<>(2);
            photoParam.put("woCode", command.getWoCode());
            photoParam.put("operateId", command.getOperateId());
            List<WorkOrderOperatePhoto> photos = workOrderOperatePhotoMapper.selectByWoCodeAndOperateId(photoParam);

            // 当接收到的照片数为0，且照片总数不为0时，直接返回
            if (photos.isEmpty() && command.getPhotoNum() != 0) {
                result.setResultCode(ECode.NEED_WAIT_FOR_PIC_UPLOAD.code());
                result.setData(new ArrayList<>());
                return result;
            }

            int rescueTypeInt = 1;
            if (workOrder.getRescueType() != null) {
                rescueTypeInt = workOrder.getRescueType();
            }

            // 检查完备性（检查过程处理方式用“-1”）
//            List<Long> checkResult = checkPhotoNumOld(photos, command.getPhotoNum(), workOrder.getWoType(),
//                    defaultVal(null), defaultVal(null), defaultVal(null), defaultVal(null), rescueTypeInt);
//            if (!checkResult.isEmpty()) {
//                result.fillResult(ReturnCode.NEED_WAIT_FOR_PIC_UPLOAD);
//                result.setData(checkResult);
//                return result;
//            }
        }

        boolean lockGet = false;
        try {
            // ***********
            // 获得分布式锁
            // ***********
//            if (!lock(command.getWoCode(), command.getOperateId())) {
//                result.setResultCode(ReturnCode.CLIENT_ERROR.code());
//                result.setMessage("处理中，请稍后");
//                return result;
//            }
            lockGet = true;
//            sleep();

            /* 操作唯一标识 唯一性验证 */
            WorkOrderOperate orderOperate = new WorkOrderOperate();
            orderOperate.setWoCode(command.getWoCode());
            orderOperate.setOperateId(command.getOperateId());
            Integer count = workOrderOperateMapper.selectByOperateId(orderOperate);
            if (count != null && count > 0) {
                // 并发时，如果已经收到此记录，直接返回OK
                log.info("[startRepair]already got data");
                return result;
            }

            // 插入操作记录，更新工单状态
            workOrder.setAssignTo(command.getAssignTo());
            insertRecord(command, workOrder);
            /* 推送 */
            pushStartRepair(workOrder, command.getUserId());

            /* 触发报单流程 */
            trySendKafka(command.getWoCode(), ServiceStationVal.WEB_SERVICE_ONGOINGSMAPPOINTMENT, "开始维修");
            log.info("[startRepair]end");
        }
        finally {
            // ***********
            // 释放分布式锁
            // ***********
//            if (lockGet && !unlock(command.getWoCode(), command.getOperateId())) {
//                // 释放锁失败，说明之前上的锁已过期，锁被别的线程抢得，当前事务放弃
//                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//            }
        }
        return result;
    }

    @Override
    public boolean unlock(String woCode, String timestamp) {
        log.info("[unlock]woCode:{}||timestamp:{}||swt:{}", woCode, timestamp, distributedLockEnable);
        if (!distributedLockEnable) {
            return true;
        }
        return redisDistributedLock.unlock(REDIX_LOCK_KEY + woCode + timestamp);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HttpCommandResultWithData endRepair(EndRepairForm command) throws IOException {
        log.info("[endRepair]start");
        HttpCommandResultWithData result = new HttpCommandResultWithData<>();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());
        //记录图片数量不符合情况总数
        int totalPicNum = 0;
        String woCode = command.getWoCode();
        // 获取工单信息
        Map<String, String> param = new HashMap<>(1);
        param.put("woCode", woCode);
        WorkOrder workOrder = workOrderMapper.selectByWoCode(param);
        if (workOrder == null) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("未获取到该工单信息");
            return result;
        }

        // 数据校验
        String error = validateData(command, workOrder);
        if (StringUtil.isNotEmpty(error)) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage(error);
            return result;
        }

        // 查询所有维修的图片
        List<WoRepairPhotoPojo> repairPhotos = workOrderOperateMapper.selectRepairPhotoByWoCode(workOrder.getWoCode());
        int repairNum = workOrderOperateMapper.selectRepairPhotoNumByWoCode(workOrder.getWoCode());
        totalPicNum += repairNum;
        if (repairPhotos.isEmpty()) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("无已提交的维修项，无法结束维修");
            return result;
        }

        try {
            // 判断是否有未完成的维修项
            boolean checkNotFinished = repairPhotos.stream().anyMatch(e -> e.getFinishedStatus() == 0);
            if (checkNotFinished) {
                result.setResultCode(ECode.CLIENT_ERROR.code());
                result.setMessage("有未完成维修项，请确保所有维修都已完成");
                return result;
            }
        } catch (Exception e) {
            log.info("[endRepair]check finish error");
        }


        // 系统时间
        Date date = new Date();

        /* 保存外出信息 */
        if (workOrder.getWoType() == 2) {
            int res = saveOutDetail(workOrder, command, date);
            if (res != 0) {
                if (res == -1){
                    result.setResultCode(ECode.CLIENT_ERROR.code());
                    result.setMessage("数据提交失败！");
                    return result;
                }
            }
        }

        // 工单类型为外出救援时，检查阶段的图片也纳入校验范围
        if (workOrder.getWoType() == OUTSIDE_RESCUE) {
            List<WoRepairPhotoPojo> inspectPhotos = workOrderOperateMapper.selectInspectPhotoByWoCode(workOrder.getWoCode());
            int inspectNum = workOrderOperateMapper.selectInspectPhotoNumByWoCode(workOrder.getWoCode());
            totalPicNum += inspectNum;
            repairPhotos.addAll(inspectPhotos);
        }

        // 已经接收到的图片时间戳
        // 注意是循环校验，不管循环到哪次时校验不通过，返回的时间戳都是全体的
        List<Long> savedTimestamps = repairPhotos.stream()
                .filter(e -> StringUtil.isNotEmpty(e.getType()))
                .map(WoRepairPhotoPojo::getTimestamp)
                .collect(Collectors.toList());

        // 图片按操作唯一标识分组
        Map<String, List<WoRepairPhotoPojo>> repairPhotosByOperateId = repairPhotos.stream()
                .collect(Collectors.groupingBy(WoRepairPhotoPojo::getOperateId));


        RescueRoutePoint rescueRoutePoint = null;
        Map<String, Object> outParam = new HashMap<>(7);
        if (workOrder.getWoType() == OUTSIDE_RESCUE) {
            // 救援轨迹信息查询
            rescueRoutePoint = rescueRoutePointMapper.selectByWoCode(woCode);
            if (rescueRoutePoint == null) {
                result.setResultCode(ECode.CLIENT_ERROR.code());
                result.setMessage("救援轨迹不存在");
                return result;
            }

            String key = "";
            Integer maxIndexOne = 0;
            Integer maxIndexTwo = 0;
            // 取出第一次外出的所有轨迹点并筛选落盘
            key = pointsToRedisKeyPrefixOne + workOrder.getWoCode();
            RescueRoutePointHistory rescueRoutePointHistory = new RescueRoutePointHistory();

            Integer timesRescueNumber = 1;
            if(null != workOrder.getTimesRescueNumber()){
                timesRescueNumber = workOrder.getTimesRescueNumber();
            }
            // 如果是二次外出，那么一次外出的maxIndex在history表中，否则在rescue_route_point表中
            if (timesRescueNumber > 1){
                rescueRoutePointHistory = rescueRoutePointHistoryMapper.queryHistoryRescueRoutePoint(workOrder.getWoCode(), "1");
                maxIndexOne = rescueRoutePointHistory.getMaxIndex();
            }else {
                maxIndexOne = rescueRoutePointMapper.selectByWoCode(woCode).getMaxIndex();
            }
            // 查询一次外出所有轨迹点
            List<String> pointsList = redisTemplate.opsForList().range(key, 0, -1);
            List<PointDto> pointDtos = new ArrayList<>();
            for (String str : pointsList) {
                pointDtos.add(JSONObject.parseObject(str, PointDto.class));
            }
            final Integer maxIndexFinal = maxIndexOne;
            // 如果轨迹点的index大于maxIndex则过滤掉
            List<PointDto> collect = pointDtos.stream().filter(item -> item.getIndex() <= maxIndexFinal).collect(Collectors.toList());
            // 如果是二次外出则一次外出轨迹点落到history表中，否则落到rescue_route_point表中
            if (timesRescueNumber > 1){
                rescueRoutePointHistory.setUpdateTime(new Date());
                rescueRoutePointHistory.setOriginalPoints(JsonUtil.toJson(collect));
                // 轨迹点json落入mysql
                rescueRoutePointHistoryMapper.updateByPrimaryKeySelective(rescueRoutePointHistory);
            }else {
                RescueRoutePoint upData = new RescueRoutePoint();
                upData.setId(rescueRoutePoint.getId());
                upData.setUpdateTime(new Date());
                upData.setUserId(command.getUserId());
                upData.setIsLogout(0);
                upData.setOriginalPoints(JsonUtil.toJson(collect));
                // 轨迹点json落入mysql
                rescueRoutePointMapper.updateByPrimaryKeySelective(upData);
            }
            String originalPointsJson = JsonUtil.toJson(collect);
            List<PointDto> collect2 = new ArrayList<>();
            List<PointDto> pointDtos2 = new ArrayList<>();
            // 判断是否是二次外出
            if (timesRescueNumber > 1){
                // 如果有二次外出，将计算救援时用的轨迹点变为二次外出
                key = pointsToRedisKeyPrefixTwo + workOrder.getWoCode();
                maxIndexTwo = rescueRoutePoint.getMaxIndex();
                List<String> pointsList2 = redisTemplate.opsForList().range(key, 0, -1);

                for (String str : pointsList2) {
                    pointDtos2.add(JSONObject.parseObject(str, PointDto.class));
                }
                final Integer maxIndexTwoFinal = maxIndexTwo;
                // 如果轨迹点的index大于maxIndex则过滤掉
                collect2 = pointDtos2.stream().filter(item -> item.getIndex() <= maxIndexTwoFinal).collect(Collectors.toList());
                RescueRoutePoint upData = new RescueRoutePoint();
                upData.setId(rescueRoutePoint.getId());
                upData.setUpdateTime(new Date());
                upData.setUserId(command.getUserId());
                upData.setIsLogout(0);
                upData.setOriginalPoints(JsonUtil.toJson(collect2));
                // 轨迹点json落入mysql
                rescueRoutePointMapper.updateByPrimaryKeySelective(upData);
                originalPointsJson = JsonUtil.toJson(collect2);
            }


            if(collect2.size()>0){
                pointDtos.addAll(pointDtos2);
                originalPointsJson = JsonUtil.toJson(pointDtos);
            }
            rescueRoutePoint.setOriginalPoints(originalPointsJson);


            if(collect2.size()>0){
                rescueRoutePoint.setOriginalPoints(JsonUtil.toJson(collect2));
            }else {
                rescueRoutePoint.setOriginalPoints(JsonUtil.toJson(collect));
            }

            /* 计算救援距离 */
            scanReceiveService.calculateDistance(command.getWoCode() ,rescueRoutePoint, null, null, null, true, outParam);
        }

        // 更新每个操作记录为可见，更新工单状态
        Date woTimeClose = new Date();
        updateRecordVisible(repairPhotos, workOrder, woTimeClose);

        /* 推送 or 短信 */
        pushOrSms(workOrder.getSendToRepairPhone(), workOrder, command.getUserId());

        // 最后删除缓存
        redisTemplate.delete(pointsToRedisKeyPrefixOne + workOrder.getWoCode());
        redisTemplate.delete(pointsToRedisKeyPrefixTwo + workOrder.getWoCode());

        /* 触发报单流程 */
        if (syncWorkToCrmSwitch) {
            // 工单类型为外出救援时，需要获取预计救援里程和实际救援里程进行比较
            if (workOrder.getWoType() == OUTSIDE_RESCUE) {
                if (rescueRoutePoint == null
                        || (rescueRoutePoint.getConfirmMileage() == null
                        && distanceAnomalyService.isDistanceOverLimit(rescueRoutePoint.getEstimateMileage(), (Integer) outParam.get(ScanReceiveServiceImpl.METERS)))) {
                    log.info("[endRepair]no synchronize");
                    return result;
                }
            }
            trySendKafka(woCode, ServiceStationVal.WEB_SERVICE_ALL_INTERFACE, "结束维修");
        }
        if (uptimeSwitch) {
            /* 12小时后同步离站时间 */
            redisTemplate.opsForValue().set("DRIVE_OUT_TIME:" + woCode, woCode, 12, TimeUnit.HOURS);
            log.info("[endRepair]redis expire set");
        }

        // 计算轨迹完整性 异步
        asyCheckPointCompletenessService.checkPointCompleteness(woCode);
        // 保存仪表里程
        asySaveMeterMileageWoService.saveEndRepairMeterMileage(woCode, workOrder.getChassisNum(), woTimeClose);

        log.info("[endRepair]end");
        return result;
    }
    private int saveOutDetail(WorkOrder workOrder, EndRepairForm command, Date date) {
        WorkOrderOutDetail wood = this.workOrderOutDetailMapper.selectByWoCode(workOrder.getWoCode());
        if (wood != null) {
            if (wood.getOutTimes() == 1) {
                wood.setEndAddress(command.getEndAddress());
                wood.setEndMileage(Float.valueOf(command.getEndMileage()));
                wood.seteOutDate(date);
                wood.setAppOutMileage(Float.valueOf(command.getAppOutMileage()));
            }else if (wood.getOutTimes() == 2) {
                wood.setTwiceEndAddress(command.getEndAddress());
                wood.setTwiceEndMileage(Float.valueOf(command.getEndMileage()));
                wood.setTwiceEOutDate(date);
                wood.setTwiceAppOutMileage(Float.valueOf(command.getAppOutMileage()));
            }
            this.workOrderOutDetailMapper.updateByPrimaryKey(wood);
            return 0;
        }else{
            return -1;
        }
    }

    /**
     * 验证入参
     */
    private String validateCommand(StartRepairForm command) {
        // 校验检查结果的长度
        if (StringUtil.isNotEmpty(command.getInspectResult()) && command.getInspectResult().length() > DESC_MAX_LENGTH) {
            return "预检信息最多可输入300个字";
        }
        return "";
    }
    /**
     * 验证数据
     */
    private String validateData(StartRepairForm command, UserInfoDto userInfo, WorkOrder workOrder) {
        log.info("[validateData]roleCode:{}||woType:{}", userInfo.getRoleCode(), workOrder.getWoType());
        // 进出站工单校验账号角色
        if (workOrder.getWoType() == STATION_SERVICE && userInfo.getRoleCode() != JOB_TYPE_ADMIN) {
            return "此账号没有操作权限";
        }

        log.info("[validateData]woStatus:{}", workOrder.getWoStatus());

        // 校验工单状态（并发时，工单可能已经变成维修中状态，放行）
        // 如果工单状态不是检查中状态，不可以开始维修
        if (workOrder.getWoStatus() != ServiceStationEnum.INSPECTING.code()) {
            return "当前工单状态无法开始维修";
        }

        return "";
    }
    /**
     * 插入操作记录，更新工单状态
     */
    private void insertRecord(StartRepairForm command, WorkOrder workOrder) throws JsonProcessingException {
        // 插入工单操作记录“检查过程（服务站）”
        WorkOrderOperate insertEntity = new WorkOrderOperate();
        insertEntity.setWoCode(workOrder.getWoCode());
        insertEntity.setOperateCode(OperateCodeEnum.OP_INSPECT.code());
        insertEntity.setOperateId(command.getOperateId());
        insertEntity.setIsHiddenToApp(0);
        insertEntity.setDescription(command.getInspectResult());
        insertEntity.setPhotoNum(command.getPhotoNum());
        insertEntity.setTitle(OperateCodeEnum.OP_INSPECT.message());
        Map<String, String> jsonMap = new LinkedHashMap<>();
        jsonMap.put("预检信息：", command.getInspectResult());
        jsonMap.put("检查账号：", command.getUserId());
        Date now = new Date();
        jsonMap.put("检查结束时间：", new SimpleDateFormat(DateUtil.TIME_PATTERN_MINUTE).format(now));
        jsonMap.put("检查照片：", PHOTO_URL_PLACEHOLDER);
        insertEntity.setTextJson(JsonUtil.toJson(jsonMap));
        insertEntity.setHiddenFlg(0);
        insertEntity.setUserId(command.getUserId());
        insertEntity.setCreateTime(now);
        insertEntity.setUpdateTime(now);
        workOrderOperateMapper.insertSelective(insertEntity);

        // 更新工单状态为“维修中”
        WorkOrder updateEntity = new WorkOrder();
        updateEntity.setId(workOrder.getId());
        updateEntity.setWoStatus(ServiceStationEnum.REPAIRING.code());

        updateEntity.setTimeInspected(now);
        updateEntity.setUpdateTime(now);
        if (null != command.getEstimateTime()){
            updateEntity.setEstimateTime(new Date(command.getEstimateTime()));
        }

        updateEntity.setEstimateFee(command.getEstimateFee());
        updateEntity.setAssignTo(workOrder.getAssignTo());
        workOrderMapper.updateByPrimaryKeySelective(updateEntity);
    }
    /**
     * 检查结束推送
     */
    private void pushStartRepair(WorkOrder workOrder, String senderId) {
        log.info("[pushStartRepair]start");
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
                wildcardMap.put("{服务站名称}", workOrder.getStationName());
                stype = PushStaticLocarVal.PUSH_TYPE_TEN_STYPE_START_REPAIR_IN;
            } else {
                // 外出救援
                stype = PushStaticLocarVal.PUSH_TYPE_TEN_STYPE_START_REPAIR_OUT;
            }
            String wildcard = JsonUtil.toJson(wildcardMap);
            // 扩展信息
            Map<String, String> messageExtraMap = new HashMap<>(3);
            // 工单号
            messageExtraMap.put(PushStaticLocarVal.MESSAGE_EXTRA_WO_CODE, workOrder.getWoCode());
            String messageExtra = JsonUtil.toJson(messageExtraMap);
            asyPushMessageService.pushToDriverAndOwner(stype, wildcard, messageExtra, senderId, null, chassisNum);
        } catch (Exception e) {
            log.info("[pushStartRepair]Exception:", e);
        }
        log.info("[pushStartRepair]end");
    }
    /**
     * 尝试触发报单流程，失败时发送邮件
     */
    @Override
    public void trySendKafka(String woCode, String crmType, String functionStr) {
        if (syncWorkToCrmSwitch) {
            try {
                log.info("[trySendKafka]send to kafka start");
                Map<String, Object> map = new HashMap<>(3);
                map.put("crmType", crmType);
                //todo 后续配置好kafka之后取消注释
                //kafkaTemplate.send(workOrderToCrmTopic, woCode, JsonUtil.toJson(map));
                log.info("[trySendKafka]send to kafka end");
            } catch (Exception e) {
                log.error("[trySendKafka]send to kafka exception", e);
                if (StringUtil.isNotEq("dummy", syncWorkToCrmErrEmails)) {
                    String ip;
                    try {
                        ip = InetAddress.getLocalHost().getHostAddress();
                    } catch (UnknownHostException ex) {
                        ip = "未知IP";
                    }
                    String identifier = "【" + envIdentifier + "】【" + ip + "】";
                    boolean flag = MailSenderUtil.sendCommonEmailList(
                            sendEmailServerUrl,
                            identifier + functionStr + " 触发同步工单信息到CRM失败告警",
                            "同步失败的工单号为：【" + woCode + "】",
                            syncWorkToCrmErrEmails);
                    if (!flag) {
                        log.error("发送邮件失败，邮件内容为：【{}】", woCode);
                    }
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HttpCommandResultWithData startInspect(StartInspectForm command) {
        log.info("[startInspect]start");
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        Map<String, String> param = new HashMap<>(3);
        param.put("woCode", command.getWoCode());
        WorkOrder workOrder = workOrderMapper.selectByWoCode(param);
        if (workOrder == null) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("未获取到该工单信息");
            return result;
        }

        // 校验工单状态是否为检查中
        if (workOrder.getWoStatus() != ServiceStationEnum.INSPECTING.code()) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("当前工单状态无法开始检查");
            return result;
        }

        // 开始检查时间不为空时，直接返回OK
        // （此处本来应该返回特殊code表示重复调用，但因为仅是一个开始检查时间字段的更新，做了简化处理返回200）
        if (workOrder.getTimeInspectBegin() != null) {
            log.info("[startInspect]already set");
            return result;
        }

        // 更新开始检查时间
        WorkOrder updateEntity = new WorkOrder();
        updateEntity.setId(workOrder.getId());
        Date now = new Date();
        updateEntity.setTimeInspectBegin(now);
        updateEntity.setUpdateTime(now);

        workOrderMapper.updateByPrimaryKeySelective(updateEntity);
        log.info("[startInspect]end");
        return result;
    }

    /**
     * 验证数据
     */
    private String validateData(EndRepairForm command, WorkOrder workOrder) {
        log.info("[validateData]woStatus:{}", workOrder.getWoStatus());
        // 用户信息查询
        UserInfoDto userInfoDto = userService.getUserInfoByUserId(command.getUserId(), false);
        // 校验工单状态
        if (workOrder.getWoStatus() != ServiceStationEnum.REPAIRING.code()) {
            return "当前工单状态无法结束维修";
        }
        log.info("[validateData]assignTo:{}", workOrder.getAssignTo());
        // 校验指派人员
        /**
         * 20210518 接单员可以操作任何工单
         */
        if (userInfoDto.getRoleCode() != ServiceStationVal.JOB_TYPE_ADMIN){
            if (StringUtil.isNotEq(workOrder.getAssignTo(), command.getUserId())) {
                return "您不是此工单的指派人员！";
            }
        }
        return "";
    }
    /**
     * 更新操作记录为可见，更新工单状态
     */
    private void updateRecordVisible(List<WoRepairPhotoPojo> repairPhotos, WorkOrder workOrder, Date woTimeClose) {
        // 更新记录为可见
        workOrderOperateMapper.updateRecordVisible(repairPhotos.stream().map(WoRepairPhotoPojo::getId).collect(Collectors.toList()));

        // 更新工单状态为“已完成”
        WorkOrder updateEntity = new WorkOrder();
        updateEntity.setId(workOrder.getId());
        updateEntity.setWoStatus(ServiceStationEnum.WORK_DONE.code());
        updateEntity.setTimeClose(woTimeClose);
        updateEntity.setUpdateTime(woTimeClose);
        workOrderMapper.updateByPrimaryKeySelective(updateEntity);
    }
    /**
     * 推送或者发送短信
     */
    private void pushOrSms(String phone, WorkOrder workOrder, String senderId) {
        if (StringUtil.isEmpty(phone)) {
            log.info("[pushOrSms]phone is empty");
            return;
        }
        User queryUser = userMapper.selectByPhone(phone);
        String chassisNum = workOrder.getChassisNum();
        String carNumber = carMapper.queryCarNumberByVin(chassisNum);
        if (queryUser != null && StringUtil.isNotEmpty(queryUser.getUcId())) {
            log.info("[pushOrSms]push");
            // 推送
            try {
                // 通配符替换
                Map<String, String> wildcardMap = new HashMap<>(7);
                wildcardMap.put("{优先车牌号}", StringUtil.isNotEmpty(carNumber) ? carNumber : chassisNum);
                wildcardMap.put("{工单号}", workOrder.getWoCode());
                String stype;
                if (ServiceStationVal.STATION_SERVICE == workOrder.getWoType()) {
                    // 进出站
                    wildcardMap.put("{服务站名称}", workOrder.getStationName());
                    stype = PushStaticLocarVal.PUSH_TYPE_TEN_STYPE_EVALUATE_IN;
                } else {
                    // 外出救援
                    stype = PushStaticLocarVal.PUSH_TYPE_TEN_STYPE_EVALUATE_OUT;
                }
                String wildcard = JsonUtil.toJson(wildcardMap);
                // 扩展信息
                Map<String, String> messageExtraMap = new HashMap<>(3);
                // 工单号
                messageExtraMap.put(PushStaticLocarVal.MESSAGE_EXTRA_WO_CODE, workOrder.getWoCode());
                String messageExtra = JsonUtil.toJson(messageExtraMap);
                asyPushMessageService.pushToDriverAndOwner(stype, wildcard, messageExtra, senderId, phone, null);
            } catch (Exception e) {
                log.info("[pushOrSms]push exception:" + e.getMessage(), e);
            }
        } else {
            log.info("[pushOrSms]sms");
            // 短信
            SendSmsForm sendSmsCommand = new SendSmsForm();
            String content = smsTemplate
                    .replace("{优先车牌号}", StringUtil.isNotEmpty(carNumber) ? carNumber : chassisNum)
                    .replace("{工单号}", workOrder.getWoCode())
                    .replace("{服务站名称}", workOrder.getStationName());
            log.info("[pushOrSms]sms content:{}", content);
            sendSmsCommand.setContent(content);
            sendSmsCommand.setPhone(phone);
            try {
                toolsClient.sendSms(sendSmsCommand);
            } catch (Exception e) {
                log.info("[pushOrSms]sms exception:" + e.getMessage(), e);
            }
        }
    }

}
