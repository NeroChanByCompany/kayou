package com.nut.servicestation.app.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.nut.common.assembler.ReverseGeoCodingClient;
import com.nut.common.constant.OperateCodeEnum;
import com.nut.common.constant.ServiceStationVal;
import com.nut.common.enums.ServiceStationEnum;
import com.nut.common.push.PushStaticLocarVal;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.utils.*;
import com.nut.locationservice.app.dto.CarLocationOutputDto;
import com.nut.servicestation.app.client.BigDataClient;
import com.nut.servicestation.app.dto.ServiceAarNoticeDto;
import com.nut.servicestation.app.form.*;
import com.nut.servicestation.common.assembler.*;
import com.nut.servicestation.common.handler.IntegralHandler;
import com.nut.servicestation.app.dao.*;
import com.nut.servicestation.app.domain.*;
import com.nut.servicestation.app.dto.PointDto;
import com.nut.servicestation.app.dto.UserInfoDto;
import com.nut.servicestation.app.service.*;
import com.nut.servicestation.common.constants.UptimeVal;
import com.nut.servicestation.common.utils.MailSenderUtil;
import com.nut.servicestation.common.utils.MathUtil;
import com.nut.servicestation.common.utils.MathUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

import static com.nut.common.constant.ServiceStationVal.OUTSIDE_RESCUE;
import static com.nut.common.constant.ServiceStationVal.STATION_SERVICE;

/*
 *  @author wuhaotian 2021/7/5
 */
@Slf4j
@Service("ScanReceiveService")
public class ScanReceiveServiceImpl implements ScanReceiveService {

    /**
     * 接车时间最小临界值 s
     */
    @Value("${scanReceiveTimeMin:120}")
    private int scanReceiveTimeMin;

    /**
     * 调用积分添加业务
     */
    @Value("${integral_rule_url}")
    private String integralRuleUrl;
    /**
     * 接车时间最大临界值 s
     */
    @Value("${scanReceiveTimeMax:3600}")
    private int scanReceiveTimeMax;
    /**
     * 接车距离限制 km
     */
    @Value("${scanReceiveDistance:1}")
    private double scanReceiveDistance;

    @Autowired
    private ExternalMapClient externalMapService;
    @Autowired
    private DistanceAnomalyService distanceAnomalyService;
    @Autowired
    private RescueRoutePointHistoryDao rescueRoutePointHistoryMapper;
    @Value("${big_data.dirver_base_url}")
    private String baseUrl;
    @Autowired
    private RescueRoutePointDao rescueRoutePointMapper;
    public static final String METERS = "METERS";
    public static final String MINUTES = "MINUTES";
    @Autowired
    private CarDao carMapper;
    @Autowired
    private AddWoStationService addWoStationService;
    @Autowired
    private NaviDistanceClient locationService;

    @Autowired
    private StartEndRepairService startEndRepairService;
    @Autowired
    private AsyPushMessageService asyPushMessageService;

    /**
     * 邮件环境区分标识
     */
    @Value("${envIdentifier:未配置环境名}")
    private String envIdentifier;

    private static final String YES = "1";
    /**
     * 人站距离限制区域，服务站省市编码  -1：全部区域。服务站省市编码,服务站省市编码,服务站省市编码
     * 530000 第一版只限制云南
     */
    @Value("${area_code_limit:530000}")
    private String areaCodeLimit;

    /**
     * 人站距离限制区域，202008新增
     * 620000 限制甘肃
     * 410000 限制河南
     */
    @Value("${area_code_limit2:620000,410000}")
    private String areaCodeLimit2;

    /**
     * 人站距离限制 10KM
     */
    @Value("${app_station_dis_limit:10000}")
    private String appStationDisLimit;

    /**
     * 人站距离限制 2KM
     * 甘肃、河南
     */
    @Value("${app_station_dis_limit2:2000}")
    private String appStationDisLimit2;

    /**
     * 发送邮件服务url
     */
    @Value("${sendEmailServerUrl}")
    private String sendEmailServerUrl;

    /**
     * 错误报告邮件接收人
     */
    @Value("${callBaiduApiErrEmails:dummy}")
    private String callBaiduApiErrEmails;
    /**
     * 人车距离限制 车上传时间 s
     */
    @Value("${app_car_upinfo_limit:600}")
    private int appCarUpinfoLimit;
    @Autowired
    private WorkOrderOperateDao workOrderOperateMapper;
    @Autowired
    private WorkOrderDao workOrderMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Value("${pointsToRedisKeyPrefix.one:196bba4fc}")
    private String pointsToRedisKeyPrefixOne;
    @Value("${pointsToRedisKeyPrefix.two:296bba4fc}")
    private String pointsToRedisKeyPrefixTwo;
    /**
     * 可以视为轨迹点完备的百分比
     */
    @Value("${routePointCompleteThreshold:0.9}")
    private double routePointCompleteThreshold;

    @Value("${app_car_dis_limit:10000}")
    private String appCarDisLimit;

    private static final DecimalFormat DISTANCE_FORMAT = new DecimalFormat("######0.0");

    @Autowired
    private BigDataClient bigDataClient;

    @Autowired
    private UserService queryUserInfoService;

    @Autowired
    private ApplysCheckService applysCheckService;
    @Autowired
    private ReverseGeoCodingClient queryGeographicalService;
    // 接车距离限制key
    private static final String SCAN_DIS_LIMIT = "scan_dis_limit_";
    // 接车人站距离限制key
    private static final String SCAN_APP_STATION_LIMIT = "app_station_limit";
    // 接车人车距离限制key
    private static final String SCAN_APP_CAR_LIMIT = "app_car_limit";
    @Value("${database_name}")
    private String DbName;
    @Value("${queryNoticeType:返修,活动}")
    private String queryNoticeType;
    @Autowired
    private AsyScanDistanceRecordSaveService asyScanDistanceRecordSaveService;
    @Autowired
    private UptimeService uptimeService;
    @Autowired
    private WarnInTheStationService warnInTheStationService;
    @Autowired
    private UserDao userServiceMapper;
    @Autowired
    private IntegralHandler integralHandler;
    @Autowired
    private AsySaveMeterMileageWoService asySaveMeterMileageWoService;
    @Autowired
    private ServiceAarNoticeDao serviceAarNoticeMapper;
    @Autowired
    private BaiduMapComponent baiduMapComponent;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void calculateDistance(String woCode, RescueRoutePoint rescueRoutePoint, String stationLon, String stationLat,
                                  Date timeDepart, boolean endRepairFlg, Map<String, Object> outParam) throws IOException {
        log.info("[calculateDistance]start");
        // 实际救援里程(米)
        Integer actualMileage = null;
        // 实际救援时间(分钟)
        Integer actualTime = null;
        if (timeDepart != null) {
            Long cTime = System.currentTimeMillis() - timeDepart.getTime();
            actualTime = Integer.parseInt(MathUtils.div(new BigDecimal(cTime + ""), new BigDecimal("60000")).setScale(0, RoundingMode.HALF_DOWN).toPlainString());
        }
        // 纠偏后的轨迹点
        List<PointDto> actualPoints = new ArrayList<>();

        // 救援轨迹信息
        if (rescueRoutePoint == null) {
            log.info("[calculateDistance]rescueRoutePoint is null");
            return;
        }

        /* 轨迹纠偏 */
        List<PointDto> originalPoints = JsonUtil.toList(rescueRoutePoint.getOriginalPoints(), PointDto.class);
        if (originalPoints != null && rescueRoutePoint.getMaxIndex() != null) {
            originalPoints = originalPoints.stream()
                    .filter(e -> e.getIndex() != null && e.getIndex() <= rescueRoutePoint.getMaxIndex())
                    .collect(Collectors.toList());
        }
        /**
         * 此方法未曾生效，注释换新方法 // TODO: 26/08/2021
         // 调用第三方接口
         RectifyTrackRequestAssembler.RectifyTrackResult rectifyTrackResult = externalMapService.rectifyTrack(originalPoints, endRepairFlg);
         */
        // TODO: 25/08/2021  将242行方法替换为：从大数据接口获取轨迹点数据，再调用百度轨迹纠偏API接口查询距离
        log.info("==============百度调用地图新方法start==================");
        QueryOrderPointForm form = new QueryOrderPointForm();
        LocalDate date = LocalDate.now();
        long miliMax = LocalDateTime.of(date, LocalTime.MAX) .toInstant(ZoneOffset.of("+8")).toEpochMilli() / 1000;
        long miliMin = LocalDateTime.of(date, LocalTime.MIN) .toInstant(ZoneOffset.of("+8")).toEpochMilli() / 1000;
        form.setStartTime(miliMin);
        form.setEndTime(miliMax);
        form.setTerminalId(woCode);
        log.info("表单数据：{}", form);
        JSONObject pointDataOld = bigDataClient.queryOrderPoint(form);
        log.info("大数据返回数据：{}" ,pointDataOld);
        try {
            JSONArray pointData = pointDataOld.getJSONArray("data");
            List<TrackPoint> listPoint = new ArrayList<>();
            for (int i = 0; i < pointData.size(); i++) {
                TrackPoint point = new TrackPoint();
                JSONObject object = pointData.getJSONObject(i);
                point.setLoc_time(Long.valueOf(object.get("gpsDate").toString()));
                point.setLatitude(exchangeNumber(object.get("latitude")));
                point.setLongitude(exchangeNumber(object.get("longitude")));
                point.setCoord_type_input("gcj02");
                listPoint.add(point);
            }
//            DistanceTrueForm form1 = new DistanceTrueForm();
//            form1.setList(listPoint);
            JSONObject json = new JSONObject();
            json.put("point_list", listPoint);
            JSONObject jsonObject = baiduMapComponent.distanceTrue(json);
            // TODO: 26/08/2021 数据待解析
            log.info("百度轨迹纠偏结果：{}", jsonObject);
            actualMileage = Double.valueOf(jsonObject.get("distance").toString()).intValue();
            log.info("actualMileage实际救援里程数为：{}", actualMileage);
            log.info("==============百度调用地图新方法end==================");
        }catch (Exception e){
            log.error("计算实际救援里程数出错");
        }


        /**
         * 此方法未生效，造成计算实际里程总为0，注销  // TODO: 26/08/2021
        if (rectifyTrackResult != null && rectifyTrackResult.getStatus() == 0) {
            // 实际救援里程(米)
            actualMileage = MathUtil.scale(rectifyTrackResult.getDistance(), 0).intValue();
            // 纠偏后的轨迹点
            int index = 1;
            for (RectifyTrackRequestAssembler.Point newPoint : rectifyTrackResult.getPoints()) {
                PointDto p = new PointDto();
                p.setIndex(index++);
                p.setLatitude(newPoint.getLatitude());
                p.setLongitude(newPoint.getLongitude());
                p.setTime(String.valueOf(newPoint.getLoc_time()));
                actualPoints.add(p);
            }
        } else {
            sendEmail(rescueRoutePoint.getWoCode(), "轨迹纠偏接口"
                    + (rectifyTrackResult == null ? "empty" : rectifyTrackResult.getMessage()));
        }
         */

        // 预计救援里程(米)
        int estimateMileage = 0;
        // 预计救援时间(分钟)
        int estimateTime = 0;
        // 预计救援轨迹点
        List<PointDto> estimatePoints = new ArrayList<>();
        if (!endRepairFlg) {
            String maxPointLat = null;
            String maxPointLon = null;
            // 2020-05-22 取立即出发时经纬度
            String minPointLat = stationLat;
            String minPointLon = stationLon;

            if (originalPoints != null) {
                PointDto maxPoint = originalPoints.stream()
                        .filter(e -> e.getIndex() != null && e.getIndex().equals(rescueRoutePoint.getMaxIndex()))
                        .findFirst()
                        .orElse(null);
                if (maxPoint != null && maxPoint.getLatitude() != null) {
                    maxPointLat = maxPoint.getLatitude().toString();
                }
                if (maxPoint != null && maxPoint.getLongitude() != null) {
                    maxPointLon = maxPoint.getLongitude().toString();
                }

                PointDto minPoint = originalPoints.stream()
                        .filter(e -> e.getIndex() != null && e.getIndex().equals(1))
                        .findFirst()
                        .orElse(null);
                if (minPoint != null && minPoint.getLatitude() != null) {
                    minPointLat = minPoint.getLatitude().toString();
                }
                if (minPoint != null && minPoint.getLongitude() != null) {
                    minPointLon = minPoint.getLongitude().toString();
                }
            }
            // TODO: 25/08/2021 影响程序报错，暂时注释
//            // 调用第三方接口
//            // 2020-05-22 修改原站经纬度到车经纬度预计路线，改为出发的经纬度到车经纬度预计路线
//            DirectionLiteRequestAssembler.DirectionLiteResult directionLiteResult = externalMapService.directionLite(
//                    minPointLat, minPointLon, maxPointLat, maxPointLon, endRepairFlg);
//            if (directionLiteResult != null && directionLiteResult.getStatus() == 0) {
//                // 预计救援里程(米)
//                estimateMileage = MathUtil.scale(directionLiteResult.getDistance(), 0).intValue();
//                // 预计救援时间(分钟)
//                estimateTime = MathUtil.scale((double) estimateMileage / 1000 / 40 * 60, 0).intValue();
//                // 预计救援轨迹点
//                int index = 1;
//                for (String lonLat : directionLiteResult.getLonLats()) {
//                    PointDto p = new PointDto();
//                    p.setIndex(index++);
//                    p.setLatitude(Double.parseDouble(lonLat.split(",")[1]));
//                    p.setLongitude(Double.parseDouble(lonLat.split(",")[0]));
//                    p.setTime(String.valueOf(System.currentTimeMillis()));
//                    estimatePoints.add(p);
//                }
//            } else {
//                if (directionLiteResult != null && directionLiteResult.getStatus() == -2) {
//                    log.error("points null");
//                } else {
//                    sendEmail(rescueRoutePoint.getWoCode(), "轻量级路线规划接口"
//                            + (directionLiteResult == null ? "empty" : directionLiteResult.getMessage()));
//                }
//            }
        }

        // 救援轨迹信息更新
        RescueRoutePoint point = new RescueRoutePoint();
        point.setId(rescueRoutePoint.getId());
        point.setMileage(actualMileage);
        point.setPoints(JsonUtil.toJson(actualPoints));
        if (!endRepairFlg) {
            point.setDuration(actualTime);
//            point.setEstimateMileage(estimateMileage);
//            point.setEstimateDuration(estimateTime);
            point.setEstimatePoints(JsonUtil.toJson(estimatePoints));
        }
        point.setUpdateTime(new Date());
        if (endRepairFlg) {
            // 当实际救援里程与推荐导航预计相差 在阈值范围内，客服确认里程取实际救援里程
            if (!distanceAnomalyService.isDistanceOverLimit(rescueRoutePoint.getEstimateMileage(), actualMileage)) {
                point.setConfirmMileage(actualMileage);

                // 如果有二次外出，一次外出的客服确认里程与二次的相同
                RescueRoutePointHistory rescueRoutePointHistory = rescueRoutePointHistoryMapper.queryHistoryRescueRoutePoint(rescueRoutePoint.getWoCode(), "1");
                if (rescueRoutePointHistory != null && rescueRoutePointHistory.getConfirmMileage() == null) {
                    RescueRoutePointHistory updateHistory = new RescueRoutePointHistory();
                    updateHistory.setId(rescueRoutePointHistory.getId());
                    updateHistory.setConfirmMileage(actualMileage);
                    rescueRoutePointHistoryMapper.updateByPrimaryKeySelective(updateHistory);
                }
            }
        }
        rescueRoutePointMapper.updateByPrimaryKeySelective(point);

        // 返回结果
        if (outParam != null) {
            outParam.put(METERS, actualMileage);
            outParam.put(MINUTES, actualTime);
        }
        log.info("[calculateDistance]end");
    }

    @Override
    public void receive(WorkOrder workOrder) throws Exception {
        /* 变更工单状态 检查中 */
        Date date = new Timestamp(System.currentTimeMillis());
        WorkOrder upDataWorkOrder = new WorkOrder();
        upDataWorkOrder.setId(workOrder.getId());
        // 工单状态
        upDataWorkOrder.setWoStatus(ServiceStationEnum.INSPECTING.code());
        // 接车时间
        upDataWorkOrder.setTimeReceive(date);
        // 接车异常标
        upDataWorkOrder.setIsAbnormalReceive(0);
        // 更新时间
        upDataWorkOrder.setUpdateTime(date);
        // 送修人姓名
        upDataWorkOrder.setSendToRepairName(workOrder.getSendToRepairName());
        // 送修人电话
        upDataWorkOrder.setSendToRepairPhone(workOrder.getSendToRepairPhone());

        workOrderMapper.updateByPrimaryKeySelective(upDataWorkOrder);

        /* 插入操作记录 */
        WorkOrderOperate workOrderOperate = new WorkOrderOperate();
        workOrderOperate.setWoCode(workOrder.getWoCode());
        workOrderOperate.setOperateCode(OperateCodeEnum.OP_SCAN_RECEIVE.code());
        workOrderOperate.setTitle(OperateCodeEnum.OP_SCAN_RECEIVE.message());
        workOrderOperate.setUserId(workOrder.getOperatorId());
        workOrderOperate.setCreateTime(date);
        workOrderOperate.setUpdateTime(date);


        // app服务流程Json
        Map<String, String> appJson = new LinkedHashMap<>(16);
        if (workOrder.getSendToRepairName() != null) {
            appJson.put("送修人姓名：", workOrder.getSendToRepairName());
        }
        if (workOrder.getSendToRepairPhone() != null) {
            appJson.put("送修人电话：", workOrder.getSendToRepairPhone());
        }
        appJson.put("接车位置：", "服务站建单，自动接车");
        appJson.put("接车时间：", DateUtil.getDatePattern(System.currentTimeMillis()));
        appJson.put("接车账号：", workOrder.getOperatorId());
        workOrderOperate.setTextJson(JsonUtil.toJson(appJson));
        // tboss服务流程Json
        Map<String, String> tbossJson = new LinkedHashMap<>(16);
        tbossJson.put("报修人姓名：", workOrder.getSendToRepairName());
        tbossJson.put("报修人电话：", workOrder.getSendToRepairPhone());
        tbossJson.put("接车位置：", "服务站建单，自动接车");
        tbossJson.put("接车时间：", DateUtil.getDatePattern(System.currentTimeMillis()));
        tbossJson.put("接车账号：", workOrder.getOperatorId());
        workOrderOperate.setTextJsonTb(JsonUtil.toJson(tbossJson));
        workOrderOperateMapper.insertSelective(workOrderOperate);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RescueRoutePoint preSavePoint(String woCode, String phoneLon, String phoneLat, Integer maxIndex) throws IOException {
        // 救援轨迹信息查询
        RescueRoutePoint rescueRoutePoint = rescueRoutePointMapper.selectByWoCode(woCode);
        if (rescueRoutePoint == null) {
            log.info("[preSavePoint]rescueRoutePoint is null");
            return null;
        }
        RescueRoutePoint point = new RescueRoutePoint();
        // 一次和二次外出的redis的key
        String oneKey = pointsToRedisKeyPrefixOne + woCode;
        String twoKey = pointsToRedisKeyPrefixTwo + woCode;

        String key = "";

        if (StringUtil.isNotEmpty(phoneLon) && StringUtil.isNotEmpty(phoneLat) && maxIndex != null) {
            // 把最终点并入原始点
            PointDto finalPoint = new PointDto();
            finalPoint.setIndex(maxIndex);
            finalPoint.setLongitude(Double.parseDouble(phoneLon));
            finalPoint.setLatitude(Double.parseDouble(phoneLat));
            finalPoint.setTime(String.valueOf(System.currentTimeMillis()));
            finalPoint.setRadius(1.0);

            // 获取工单信息
            Map<String, String> param = new HashMap<>(1);
            param.put("woCode", woCode);
            WorkOrder workOrder = workOrderMapper.selectByWoCode(param);

            Integer timesRescueNumber = 1;
            if (null != workOrder.getTimesRescueNumber()) {
                timesRescueNumber = workOrder.getTimesRescueNumber();
            }
            if (timesRescueNumber > 1) {
                key = twoKey;
            } else {
                key = oneKey;
            }

            List<String> range = redisTemplate.opsForList().range(key, 0, -1);
            List<PointDto> pointDtos = new ArrayList<>();
            for (String str : range) {
                pointDtos.add(JSONObject.parseObject(str, PointDto.class));
            }
            final Integer maxIndexFinal = maxIndex;
            List<PointDto> collect = pointDtos.stream().filter(item -> item.getIndex() < maxIndexFinal).collect(Collectors.toList());
            // 检查最后的轨迹点是否正确
            try {
                boolean phoneLatLonCheck = externalMapService.isValidLatLon(phoneLat, phoneLon);
                if (!phoneLatLonCheck) {
                    if (collect != null) {
                        PointDto lastPt = collect.get(collect.size() - 1);
                        if (lastPt != null) {
                            if (DateUtil.diffNDate(Long.parseLong(lastPt.getTime()) * 1000) <= appCarUpinfoLimit) {
                                finalPoint.setLatitude(lastPt.getLatitude());
                                finalPoint.setLongitude(lastPt.getLongitude());
                            }
                        }
                    }

                }
            } catch (Exception e) {
                log.error("[preSavePoint]check lat lon error:", e);
            }

            redisTemplate.opsForList().rightPush(key, JsonUtil.toJson(finalPoint));
            collect.add(finalPoint);

            point.setOriginalPoints(JsonUtil.toJson(collect));
            rescueRoutePoint.setOriginalPoints(JsonUtil.toJson(collect));
        }
        /**
         * 当申请关闭时，APP页面已经传来轨迹点最大序列号并入库，当被驳回，
         * 继续救援并确认接车时，APP又传了一个最大轨迹点序列号，此时有鱼之前已经入库最大轨迹点序列号，所以造成计算里程出错。
         * 暂时先将其注释。
         */
//        if (rescueRoutePoint.getMaxIndex() != null) {
//            log.info("[preSavePoint]already saved");
//            return rescueRoutePoint;
//        }
        // 救援轨迹信息更新
        point.setId(rescueRoutePoint.getId());
        point.setMaxIndex(maxIndex);
//        List<PointDto> originalPoints = JsonUtil.toList(rescueRoutePoint.getOriginalPoints(), PointDto.class);

        point.setUpdateTime(new Date());
        rescueRoutePointMapper.updateByPrimaryKeySelective(point);
        point.setWoCode(rescueRoutePoint.getWoCode());

        log.info("[preSavePoint]updated");
        return point;
    }

    @Override
    public List<Integer> checkRoutePointNum(Integer maxIndex, List<PointDto> originalPoints) {
        log.info("[checkRoutePointNum]start");
        List<Integer> emptyList = new ArrayList<>();
        if (maxIndex == null || maxIndex == 0) {
            return emptyList;
        }
        // 保存未接收到的序号
        Set<Integer> requireIndexes = new HashSet<>();
        for (int i = 1; i < maxIndex; i++) {
            // 1到最大值的所有整数
            requireIndexes.add(i);
        }
        int i = 0;
        if (originalPoints != null) {
            for (PointDto point : originalPoints) {
                // 移除已经收到的
                i++;
                requireIndexes.remove(point.getIndex() + (i - 1));
            }
        }
        // 未收到的序号小于一定阈值时，认为轨迹点已经完备
        if ((double) requireIndexes.size() / maxIndex <= 1 - routePointCompleteThreshold) {
            log.info("[checkRoutePointNum]ok||size:{}", requireIndexes.size());
            return emptyList;
        }
        log.info("[checkRoutePointNum]end||size:{}", requireIndexes.size());
        return new ArrayList<>(requireIndexes);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HttpCommandResultWithData rescueCancel(RescueCancelForm command) throws IOException {
        log.info("[rescueCancel]start");
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        /* 校验入参 */
        String error = validateCommand(command);
        if (StringUtil.isNotEmpty(error)) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage(error);
            return result;
        }

        /* 校验状态 */
        UserInfoDto userInfoDto = queryUserInfoService.getUserInfoByUserId(command.getUserId(), false);
        if (userInfoDto == null) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("此用户不存在！");
            return result;
        }
        String woCode = command.getWoCode();
        // 查询工单信息
        Map<String, String> param = new HashMap<>(3);
        param.put("woCode", woCode);
        WorkOrder workOrder = workOrderMapper.selectByWoCode(param);
        if (workOrder == null) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("工单不存在！");
            return result;
        }
        // 工单当前处理人有“取消救援”权限
        if (StringUtil.isNotEq(command.getUserId(), workOrder.getAssignTo())) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("您不是此工单的指派外出人员，无法取消救援");
            return result;
        }
        // 工单状态判断
        if (ServiceStationEnum.TO_RECEIVE.code() != workOrder.getWoStatus()) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("工单不是待接车状态");
            return result;
        }

        RescueRoutePoint rescueRoutePoint = preSavePoint(woCode, command.getLon(), command.getLat(), command.getRpMaxIndex());
        if (rescueRoutePoint == null) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("救援轨迹不存在");
            return result;
        }

        // 将一次或者二次的轨迹点json保存以便于计算距离使用
        String pointJson = rescueRoutePoint.getOriginalPoints();
        // 获取工单信息
        Map<String, String> queryParam = new HashMap<>(1);
        queryParam.put("woCode", command.getWoCode());
        WorkOrder order = workOrderMapper.selectByWoCode(queryParam);

        Integer timesRescueNumber = 1;
        if (null != order.getTimesRescueNumber()) {
            timesRescueNumber = order.getTimesRescueNumber();
        }
        // 一次和二次外出的redis的key
        String oneKey = pointsToRedisKeyPrefixOne + command.getWoCode();
        // 如果是二次外出，那么需要将第一次轨迹点合进来。
        if (timesRescueNumber > 1) {
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

     /* if (!YES.equals(command.getForceEnd())) {
            List<PointDto> originalPoints = JsonUtil.toList(rescueRoutePoint.getOriginalPoints(), PointDto.class);
            // 轨迹点完整性校验
            List<Integer> requiredIndexes = checkRoutePointNum(rescueRoutePoint.getMaxIndex(), originalPoints);
            if (!requiredIndexes.isEmpty()) {
                result.setResultCode(ECode.NEED_WAIT_FOR_POINT_UPLOAD.code());
                result.setData(requiredIndexes);
                return result;
            }
        }*/

        rescueRoutePoint.setOriginalPoints(pointJson);
        /* 计算救援距离 */
        /*calculateDistance(rescueRoutePoint, userInfoDto.getServiceStationLon(), userInfoDto.getServiceStationLat(),
                workOrder.getTimeDepart(), false, null);*/
        /* 更新工单状态 */
        updateWoStatusClose(workOrder.getId());
        /* 插入工单操作记录 */
        insertOperateRecordCancel(command);
        /* 推送车队版 */
        applysCheckService.pushWoClose(workOrder, command.getUserId());
        log.info("[rescueCancel]end");
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HttpCommandResultWithData scanReceive(ScanReceiveForm command, Map<String, String> mileageMap) throws Exception {
        log.info("[scanReceive] start");
        log.info("form:{}", command);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());
        // 手机经纬度
        String phoneLon = command.getPhoneLon();
        String phoneLat = command.getPhoneLat();
        String woCode = command.getWoCode();
        UserInfoDto userInfoDto = queryUserInfoService.getUserInfoByUserId(command.getUserId(), false);
        if (userInfoDto == null) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("此用户不存在！");
            return result;
        }
        String stationId = userInfoDto.getServiceStationId();
        /* 查询工单信息 */
        Map<String, String> param = new HashMap<>(2);
        param.put("woCode", woCode);
        param.put("stationId", stationId);
        WorkOrder workOrder = workOrderMapper.selectByWoCode(param);
        if (workOrder == null) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("工单不存在！");
            return result;
        }

        // 校验指派人员
        // bug【ID1000022】，前端调查出问题真因之前，临时修改=>(null)视为空
        /*if (workOrder.getWoType() == STATION_SERVICE
                && (StringUtil.isEmpty(command.getAssignTo()) || "(null)".equals(command.getAssignTo()))) {
            result.fillResult(ReturnCode.CLIENT_ERROR);
            result.setMessage("请选择指派人员");
            return result;
        }*/

        // 服务站建的工单，送修人姓名及送修人电话不再是必填字段，其他情况仍然必填
        if (ServiceStationVal.APPO_TYPE_STA != workOrder.getAppoType()) {
            if (StringUtil.isEmpty(command.getShipperName())) {
                result.setResultCode(ECode.CLIENT_ERROR.code());
                result.setMessage("送修人姓名不能为空");
                return result;
            }
            if (StringUtil.isEmpty(command.getShipperTel())) {
                result.setResultCode(ECode.CLIENT_ERROR.code());
                result.setMessage("送修人电话不能为空");
                return result;
            }
        } else {
            // 服务站建的工单自动用报修人姓名和报修人电话作为送修人姓名及送修人电话
            command.setShipperName(workOrder.getAppoUserName());
            command.setShipperTel(workOrder.getAppoUserPhone());
        }

        /* 底盘号判断*/
        String chassisNum = StringUtil.isEmpty(workOrder.getChassisNum()) ? "" : workOrder.getChassisNum();
        if (!command.getChassisNum().equals(chassisNum.substring(chassisNum.length() - 8, chassisNum.length()))) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("底盘号不正确！");
            return result;
        }
        /* 工单权限判断*/
        int woStatus = workOrder.getWoStatus();
        int woType = workOrder.getWoType();
        /* 权限：进出站-接车，是只有管理员才可以接车。*/
        if (STATION_SERVICE == woType && ServiceStationVal.JOB_TYPE_ADMIN != userInfoDto.getRoleCode()) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("只有管理员可以接车！");
            return result;
        }
        /* 权限：外出救援-接车，只有接受工单时指派的业务员才可以接车。*/
        if (OUTSIDE_RESCUE == woType && !command.getUserId().equals(workOrder.getAssignTo())) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("您不是此工单的指派外出人员，不能接车！");
            return result;
        }
        /* 工单状态判断 */
        if (ServiceStationEnum.TO_RECEIVE.code() != woStatus) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("工单已不是待接车状态！");
            return result;
        }

        // 接车异常标志 1：接车异常；0：正常
        int isAbnormalReceive = 0;
        // 无距离限制接车
        boolean notCheckDistance = false;
        // 车辆位置的上报时间差=当车辆位置的上报时间-当前系统时间 秒
        long carLocationReportTime = 0;
        // 接车距离 m
        double distance = 0;
        // 接车次数
        int receiveCarCount = 0;
        /* 获取车辆位置上报信息 */


        CarLocationOutputDto dto = null;
        Car car = carMapper.selectCarByCarVin(chassisNum);
        if (car != null && StringUtils.isNotBlank(car.getAutoTerminal())) {
            dto = addWoStationService.getCarUpInfo(command.getChassisNum());
        }


        // 20-03新增判断逻辑 start----------------------------
        // 接车时站内维修APP与服务站矩离要限制，APP和站的距离10公里，公里数可以调整
        ScanDistanceRecord scanDistanceRecord = new ScanDistanceRecord();
        scanDistanceRecord.setWoCode(woCode);
        scanDistanceRecord.setAppLat(phoneLat);
        scanDistanceRecord.setAppLon(phoneLon);
        scanDistanceRecord.setScanTime(new Timestamp(System.currentTimeMillis()));

        // 距离限制去掉
//        if (workOrder.getAppStationLimit() != null || workOrder.getAppCarLimit() != null) {
//            int appStatLm = workOrder.getAppStationLimit() == null ? -1 : workOrder.getAppStationLimit();
//            int appCarLm = workOrder.getAppCarLimit() == null ? -1 : workOrder.getAppCarLimit();
//            log.info("[scanReceive]limit not null :{},{}", appStatLm, appCarLm);
//            if (2 == appStatLm && 2 != appCarLm) {
//                result.fillResult(ReturnCode.APP_CAR_STATION_LIMIT);
//                result.setMessage("请在所属服务站内进行接车，如有疑问联系客服！");
//                return result;
//            } else if (2 != appStatLm && 2 == appCarLm) {
//                result.fillResult(ReturnCode.APP_CAR_STATION_LIMIT);
//                result.setMessage("距离车辆太远，请靠近车辆尝试或联系客服！");
//                return result;
//            } else if (2 == appStatLm && 2 == appCarLm) {
//                result.fillResult(ReturnCode.APP_CAR_STATION_LIMIT);
//                result.setMessage("人与车辆、服务站太远，请靠近尝试或联系客服！");
//                return result;
//            }
//        }
        boolean checkGPSError = true;
        try {
            if (StringUtil.isEmpty(phoneLon) || StringUtil.isEmpty(phoneLat) || Double.parseDouble(phoneLon) == 0
                    || Double.parseDouble(phoneLat) == 0 || "4.9E-324".equals(phoneLon) || "4.9E-324".equals(phoneLat)) {
                checkGPSError = false;
            }
        } catch (NumberFormatException e) {
            checkGPSError = false;
        }
        log.info("[scanReceive]wo:{},checkGPS:{}", woCode, checkGPSError);
        String scanLimitRk = SCAN_DIS_LIMIT + woCode;
        int oldExcpCountRed = 0;
        int oldExcpAppCarCountRed = 0;
        // 是否是限制区域的服务站
        boolean isLimitArea = StringUtil.isNotEmpty(workOrder.getAreaCode()) && (StringUtil.containMark(areaCodeLimit, workOrder.getAreaCode()) || "-1".equals(areaCodeLimit) || StringUtil.containMark(areaCodeLimit2, workOrder.getAreaCode()));
        if (isLimitArea && STATION_SERVICE == workOrder.getWoType() && checkGPSError) {
            //如果服务站是限制的区域，并且是进站工单
            if (workOrder.getAppStationLimit() == null || 1 == workOrder.getAppStationLimit()) {
                try {
                    log.info("[scanReceive]checkAppStation limti:1");
                    log.info("[scanReceive]checkAppStation gps:{},{},{},{}", Double.valueOf(phoneLat), Double.valueOf(phoneLon), Double.valueOf(userInfoDto.getServiceStationLat()), Double.valueOf(userInfoDto.getServiceStationLon()));
                    scanDistanceRecord.setStationLat(userInfoDto.getServiceStationLat());
                    scanDistanceRecord.setStationLon(userInfoDto.getServiceStationLon());
                    double appStationDis = LonLatUtil.getDistance(Double.valueOf(phoneLat), Double.valueOf(phoneLon), Double.valueOf(userInfoDto.getServiceStationLat()), Double.valueOf(userInfoDto.getServiceStationLon()));
                    log.info("[scanReceive]checkAppStation dis:{}", appStationDis);
                    scanDistanceRecord.setAppStationDistance(Double.valueOf(String.format("%.2f", appStationDis)));
                    // 202008 单独处理甘肃、河南
                    String appStationDisLimitStr = appStationDisLimit;
                    if (StringUtil.containMark(areaCodeLimit2, workOrder.getAreaCode())) {
                        appStationDisLimitStr = appStationDisLimit2;
                    }

                    if (appStationDis > Double.parseDouble(appStationDisLimitStr)) {
                        String excpCountRed = (String) redisTemplate.opsForHash().get(scanLimitRk, SCAN_APP_STATION_LIMIT);
                        oldExcpCountRed = StringUtil.isEmpty(excpCountRed) ? 0 : Integer.parseInt(excpCountRed);
                        log.info("[scanReceive]checkAppStation oldExcpCountRed:{}", oldExcpCountRed);
                        if (oldExcpCountRed == 0) {
                            // 增加次数
                            oldExcpCountRed += 1;
                            redisTemplate.opsForHash().put(scanLimitRk, SCAN_APP_STATION_LIMIT, String.valueOf(oldExcpCountRed));
                        } else if (oldExcpCountRed == 1) {
                            oldExcpCountRed = 2;
                            // 当连续2次不满足接车条件，向crm推送接车异常预警，并在后续接车中只提示信息不做距离校验也不再向crm重复推送异常预警
                            redisTemplate.opsForHash().delete(scanLimitRk, SCAN_APP_STATION_LIMIT);
                            WorkOrder updateWorkOrder = new WorkOrder();
                            updateWorkOrder.setId(workOrder.getId());
                            updateWorkOrder.setAppStationLimit(2);
                            workOrderMapper.updateByPrimaryKeySelective(updateWorkOrder);
                        }
                    }
                } catch (Exception e) {
                    log.error("[scanReceive] app station dis error" + e);
                }
            } else if (3 == workOrder.getAppStationLimit()) {
                isAbnormalReceive = 1;
            }
        }

        // 若满足站内限制条件，则判断接车距离，当车辆最后上报位置小于等于当前10分钟，限制接车距离小于等于10km
        // 非Tbox车辆或Tbox车辆但最后上报位置大于10分钟，则保持现有逻辑
        boolean checkOldLogic = true;
        if ((workOrder.getAppCarLimit() == null || 1 == workOrder.getAppCarLimit()) && checkGPSError) {
            try {
                log.info("[scanReceive]checkAppCar limti:1");
                // 判断是否是t-box
                int terType = carMapper.selectTerminalTypeFromTsp(command.getChassisNum(), DbName);
                log.info("[scanReceive]checkAppCar terType:{}", terType);
                if (terType == 0) {
                    // 判断最后上传位置信息
                    if (dto != null && dto.getUpTime() != null && dto.getLat() != null && dto.getLon() != null && dto.getLat() != 0 && dto.getLon() != 0) {
                        log.info("[scanReceive]checkAppCar CarLocationOutputDto:{},{},{}", dto.getUpTime(), dto.getLat(), dto.getLon());
                        scanDistanceRecord.setCarLat(dto.getLat().toString());
                        scanDistanceRecord.setCarLon(dto.getLon().toString());
                        if (DateUtil.diffNDate(dto.getUpTime() * 1000) <= appCarUpinfoLimit) {
                            checkOldLogic = false;
                            //当车辆最后上报位置小于等于当前10分钟，限制接车距离小于等于10km
                            double appCarDis = LonLatUtil.getDistance(Double.valueOf(phoneLat), Double.valueOf(phoneLon), dto.getLat(), dto.getLon());
                            log.info("[scanReceive]checkAppCar appCarDis:{}", appCarDis);
                            scanDistanceRecord.setAppCarDistance(Double.valueOf(String.format("%.2f", appCarDis)));
                            if (appCarDis > Double.parseDouble(appCarDisLimit)) {
                                String excpAppCarCountRed = (String) redisTemplate.opsForHash().get(scanLimitRk, SCAN_APP_CAR_LIMIT);
                                oldExcpAppCarCountRed = StringUtil.isEmpty(excpAppCarCountRed) ? 0 : Integer.parseInt(excpAppCarCountRed);
                                if (oldExcpAppCarCountRed == 0) {
                                    // 增加次数
                                    oldExcpAppCarCountRed += 1;
                                    redisTemplate.opsForHash().put(scanLimitRk, SCAN_APP_CAR_LIMIT, String.valueOf(oldExcpAppCarCountRed));
                                } else if (oldExcpAppCarCountRed == 1) {
                                    oldExcpAppCarCountRed = 2;
                                    // 当连续2次不满足接车条件，向crm推送接车异常预警，并在后续接车中只提示信息不做距离校验也不再向crm重复推送异常预警
                                    redisTemplate.opsForHash().delete(scanLimitRk, SCAN_APP_CAR_LIMIT);
                                    WorkOrder updateWorkOrder = new WorkOrder();
                                    updateWorkOrder.setId(workOrder.getId());
                                    updateWorkOrder.setAppCarLimit(2);
                                    workOrderMapper.updateByPrimaryKeySelective(updateWorkOrder);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error("[scanReceive] app car dis error" + e);
            }
        } else if (3 == workOrder.getAppCarLimit()) {
            checkOldLogic = false;
            isAbnormalReceive = 1;
        }

        // 保存接车记录
        asyScanDistanceRecordSaveService.sacnDistanceRecordSave(scanDistanceRecord);

        log.info("[scanReceive] oldExcpCountRed oldExcpAppCarCountRed:{},{}", oldExcpCountRed, oldExcpAppCarCountRed);
        if (oldExcpCountRed > 0 || oldExcpAppCarCountRed > 0) {
            if (oldExcpCountRed > 0 && oldExcpAppCarCountRed == 0) {
                if (oldExcpCountRed == 2) {
                    // 同步crm
                    HttpCommandResultWithData resultTri = new HttpCommandResultWithData();
                    resultTri.setResultCode(ECode.SUCCESS.code());
                    resultTri.setMessage(ECode.SUCCESS.message());
                    uptimeService.trigger(resultTri, woCode, ServiceStationVal.WEB_SERVICE_ABNORMALWORKORDERAPPLY,
                            UptimeVal.APPLY_TYPE_APPSTAEXC, "");
                }
                result.setResultCode(ECode.APP_CAR_STATION_LIMIT.code());
                result.setMessage("请在所属服务站内进行接车，如有疑问联系客服！");
                return result;
            } else if (oldExcpCountRed == 0 && oldExcpAppCarCountRed > 0) {
                if (oldExcpAppCarCountRed == 2) {
                    // 同步crm
                    HttpCommandResultWithData resultTri = new HttpCommandResultWithData();
                    resultTri.setResultCode(ECode.SUCCESS.code());
                    resultTri.setMessage(ECode.SUCCESS.message());
                    uptimeService.trigger(resultTri, woCode, ServiceStationVal.WEB_SERVICE_ABNORMALWORKORDERAPPLY,
                            UptimeVal.APPLY_TYPE_APPCAREXC, "");
                }
                result.setResultCode(ECode.APP_CAR_STATION_LIMIT.code());
                result.setMessage("距离车辆太远，请靠近车辆尝试或联系客服！");
                return result;
            } else if (oldExcpCountRed > 0 && oldExcpAppCarCountRed > 0) {
                if (oldExcpCountRed == 2 && oldExcpAppCarCountRed == 1) {
                    // 同步crm
                    HttpCommandResultWithData resultTri = new HttpCommandResultWithData();
                    resultTri.setResultCode(ECode.SUCCESS.code());
                    resultTri.setMessage(ECode.SUCCESS.message());
                    uptimeService.trigger(resultTri, woCode, ServiceStationVal.WEB_SERVICE_ABNORMALWORKORDERAPPLY,
                            UptimeVal.APPLY_TYPE_APPSTAEXC, "");
                    result.setResultCode(ECode.APP_CAR_STATION_LIMIT.code());
                    result.setMessage("请在所属服务站内进行接车，如有疑问联系客服！");
                    return result;
                } else if (oldExcpCountRed == 1 && oldExcpAppCarCountRed == 2) {
                    // 同步crm
                    HttpCommandResultWithData resultTri = new HttpCommandResultWithData();
                    resultTri.setResultCode(ECode.SUCCESS.code());
                    resultTri.setMessage(ECode.SUCCESS.message());
                    uptimeService.trigger(resultTri, woCode, ServiceStationVal.WEB_SERVICE_ABNORMALWORKORDERAPPLY,
                            UptimeVal.APPLY_TYPE_APPCAREXC, "");
                    result.setResultCode(ECode.APP_CAR_STATION_LIMIT.code());
                    result.setMessage("距离车辆太远，请靠近车辆尝试或联系客服！");
                    return result;
                } else if (oldExcpCountRed == 2 && oldExcpAppCarCountRed == 2) {
                    // 同步crm
                    HttpCommandResultWithData resultTri = new HttpCommandResultWithData();
                    resultTri.setResultCode(ECode.SUCCESS.code());
                    resultTri.setMessage(ECode.SUCCESS.message());
                    uptimeService.trigger(resultTri, woCode, ServiceStationVal.WEB_SERVICE_ABNORMALWORKORDERAPPLY,
                            UptimeVal.APPLY_TYPE_APPSTACAREXC, "");
                    result.setResultCode(ECode.APP_CAR_STATION_LIMIT.code());
                    result.setMessage("人与车辆、服务站太远，请靠近尝试或联系客服！");
                    return result;
                } else {
                    result.setResultCode(ECode.APP_CAR_STATION_LIMIT.code());
                    result.setMessage("人与车辆、服务站太远，请靠近尝试或联系客服！");
                    return result;
                }
            }
        }

        // 20-03新增判断逻辑 end----------------------------

        if (checkOldLogic) {
            if (dto == null || dto.getUpTime() == null || dto.getLat() == null || dto.getLon() == null) {
                log.info("[scanReceive getCarUpInfo() No information is reported on the vehicle location]");
                receiveCarCount = 1;
                notCheckDistance = true;
            } else {
                // 车辆位置的上报时间差=当车辆位置的上报时间-当前系统时间 秒
                carLocationReportTime = DateUtil.diffNDate(dto.getUpTime() * 1000);
                log.info("[scanReceive]carLocationReportTime:{}", carLocationReportTime);
                // 接车距离 km
                distance = locationService.getDistance(dto.getLon().toString(), dto.getLat().toString(), phoneLon, phoneLat);
                distance = Double.parseDouble(DISTANCE_FORMAT.format(distance / 1000));
                log.info("[scanReceive]distance:{} km", distance);
                // 校验车辆与接车手机距离
                if (scanReceiveDistanceCheck(carLocationReportTime, distance)) {
                    notCheckDistance = true;
                    // 接车次数
                    if (carLocationReportTime < scanReceiveTimeMax) {
                        receiveCarCount = 1;
                    } else {
                        receiveCarCount = 1;
                    }
                }
            }
            // 接车次数验证
            /*if (notCheckDistance) {
                if (scanReceiveCountCheck(stationId, woCode, receiveCarCount)) {
                    result.fillResult(ReturnCode.CLIENT_ERROR);
                    result.setMessage("距离车辆太远，请靠近车辆接车！");
                    return result;
                }
            }*/
            // 扫码接车时，车辆位置上报时间大于当前时间2分钟以上，接车距离大于1000，或通过无距离限制接车成功的。均标记为异常接车，
            if ((carLocationReportTime > scanReceiveTimeMin && distance > scanReceiveDistance) || notCheckDistance) {
                isAbnormalReceive = 1;
                log.info("[scanReceive isAbnormalReceive]carLocationReportTime:{},distance:{} km ,notCheckDistance:{}",
                        carLocationReportTime, distance, notCheckDistance);
            }
        }

        /* 变更工单状态 检查中 */
        Date date = new Timestamp(System.currentTimeMillis());
        WorkOrder upDataWorkOrder = new WorkOrder();
        upDataWorkOrder.setId(workOrder.getId());
        // 工单状态
        upDataWorkOrder.setWoStatus(ServiceStationEnum.INSPECTING.code());
        // 接车时间
        upDataWorkOrder.setTimeReceive(date);
        // 接车异常标
        upDataWorkOrder.setIsAbnormalReceive(isAbnormalReceive);
        // 更新时间
        upDataWorkOrder.setUpdateTime(date);
        // 送修人姓名
        upDataWorkOrder.setSendToRepairName(command.getShipperName());
        // 送修人电话
        upDataWorkOrder.setSendToRepairPhone(command.getShipperTel());

        if (workOrder.getWoType() == STATION_SERVICE) {
            // 进出站工单时，更新指派人
            upDataWorkOrder.setAssignTo(command.getAssignTo());
        }
        upDataWorkOrder.setMileage(command.getMilage4fault());
        workOrderMapper.updateByPrimaryKeySelective(upDataWorkOrder);

        /* 插入操作记录 */
        WorkOrderOperate workOrderOperate = new WorkOrderOperate();
        workOrderOperate.setWoCode(workOrder.getWoCode());
        workOrderOperate.setOperateCode(OperateCodeEnum.OP_SCAN_RECEIVE.code());
        workOrderOperate.setTitle(OperateCodeEnum.OP_SCAN_RECEIVE.message());
        workOrderOperate.setLongitude(phoneLon);
        workOrderOperate.setLatitude(phoneLat);
        workOrderOperate.setUserId(userInfoDto.getAccountId());
        workOrderOperate.setCreateTime(date);
        workOrderOperate.setUpdateTime(date);

        //外出接车时，保存operateId和photoNum
        if (workOrder.getWoType() == OUTSIDE_RESCUE) {
            workOrderOperate.setPhotoNum(command.getPhotoNum());
            workOrderOperate.setOperateId(command.getOperateId());
        }

        /* 更新救援里程、时间 */
        Integer actualMileage = 0;
        Integer actualTime = 0;
        if (OUTSIDE_RESCUE == woType) {
            //  RescueRoutePoint rescueRoutePoint = preSavePoint(woCode, phoneLon, phoneLat, command.getRpMaxIndex());
            RescueRoutePoint rescueRoutePoint = assemblyData(woCode, phoneLon, phoneLat, command.getRpMaxIndex());
            Map<String, Object> outParam = new HashMap<>(7);
            calculateDistance(command.getWoCode() ,rescueRoutePoint, userInfoDto.getServiceStationLon(), userInfoDto.getServiceStationLat(),
                    workOrder.getTimeDepart(), false, outParam);
            // 实际救援里程导航距离 米
            actualMileage = (Integer) outParam.get(METERS);
            if (actualMileage == null) {
                actualMileage = 0;
            }
            // 实际救援用时 分钟
            actualTime = (Integer) outParam.get(MINUTES);
            if (actualTime == null) {
                actualTime = 0;
            }
        }

        // 逆地理调用
        String location = queryGeographicalService.getPosition(phoneLat, phoneLon);
        // 实际救援里程 千米
        String actualMileageKm = MathUtil.scale((double) actualMileage / 1000, 1).toString();
        // 设备标识是否一致
        boolean deviceIdFlg = true;
        // 当外出救援工单扫码接车时，判断出发时照片的手机识别码和接车时手机识别码是否一致，
        // 一致则TBOSS和服务APP正常显示实际救援里程。
        // 不一致时,TBOSS端正常显示实际救援里程，服务APP“实际救援里程”显示为：数据异常
        if (OUTSIDE_RESCUE == woType && StringUtil.isNotEmpty(workOrder.getDeviceId()) &&
                StringUtil.isNotEmpty(command.getPhoneId()) && !workOrder.getDeviceId().equals(command.getPhoneId())) {
            deviceIdFlg = false;
        }

        // app服务流程Json
        Map<String, String> appJson = new LinkedHashMap<>(16);
        if (command.getShipperName() != null) {
            appJson.put("送修人姓名：", command.getShipperName());
        }
        if (command.getShipperTel() != null) {
            appJson.put("送修人电话：", command.getShipperTel());
        }
        appJson.put("接车位置：", location);
        if (OUTSIDE_RESCUE == woType) {
            appJson.put("实际救援里程：", deviceIdFlg ? actualMileageKm + "km" : "数据异常");
            appJson.put("实际救援用时：", actualTime + "分钟");
        }
        appJson.put("接车时间：", DateUtil.getDatePattern(System.currentTimeMillis()));
        appJson.put("接车账号：", userInfoDto.getAccountId());
        workOrderOperate.setTextJson(JsonUtil.toJson(appJson));
        // tboss服务流程Json
        Map<String, String> tbossJson = new LinkedHashMap<>(16);
        tbossJson.put("报修人姓名：", command.getShipperName());
        tbossJson.put("报修人电话：", command.getShipperTel());
        tbossJson.put("接车位置：", location);
        if (OUTSIDE_RESCUE == woType) {
            tbossJson.put("实际救援里程：", actualMileageKm + "km");
            tbossJson.put("实际救援用时：", actualTime + "分钟");
        }
        tbossJson.put("接车时间：", DateUtil.getDatePattern(System.currentTimeMillis()));
        tbossJson.put("接车账号：", userInfoDto.getAccountId());
        tbossJson.put("设备标识：", deviceIdFlg ? "设备一致" : "设备不一致");
        if (isAbnormalReceive == 1) {
            tbossJson.put("超距离接车：", "是");
        } else {
            tbossJson.put("超距离接车：", "否");
        }
        workOrderOperate.setTextJsonTb(JsonUtil.toJson(tbossJson));
        workOrderOperateMapper.insertSelective(workOrderOperate);

        // 进站工单是否存在进站预警，存在时确认接单后解除预警
        if (workOrder.getWoType() == STATION_SERVICE) {
            // 由底盘号查询车辆ID
            String carId = carMapper.queryCarIdByVin(workOrder.getChassisNum());
            warnInTheStationService.updWarningStatus(carId, workOrder.getStationCode(), ServiceStationVal.INQ_RECEIVE);
        }
        /* 推送 */
        pushReceive(workOrder, command.getUserId());

        /* 触发报单流程 */
        startEndRepairService.trySendKafka(woCode, ServiceStationVal.WEB_SERVICE_CREATESMORDER_2, "确认接车");
        if (mileageMap != null && dto != null && dto.getMileage() != null) {
            mileageMap.put("mileage", dto.getMileage().toString());
        }

        try {
            //埋点处理调用
            if (result.getResultCode() == 200) {


                List<String> phoneList = new ArrayList<>();
                phoneList.add(workOrder.getAppoUserPhone());
                String userId = userServiceMapper.queryUserIdByPhone(phoneList);
                // 用预约人id替换command的userId，加分时使用
                command.setUcId(userId);
                command.setUserId(userId);
                integralHandler.toIntegral(command, result);

                /**
                 * 用户预约进站/外出救援接车后添加积分
                 */
                log.info("[用户预约并接车添加积分]start");

                String ucIdForAdd = userServiceMapper.queryUcIdByWorkCode(woCode);

                IntegralForReceiveForm integralForReceiveCommand = new IntegralForReceiveForm();
                integralForReceiveCommand.setUcId(ucIdForAdd);
                integralForReceiveCommand.setAddType(6);
                integralForReceiveCommand.setToken(command.getToken());
                String jsonStr = JsonUtil.toJson(integralForReceiveCommand);
                log.info(jsonStr);
                Map<String, String> paramMap = new HashMap<>();
                Map<String, String> header = new HashMap<>();
                //paramMap.put("msg", jsonStr);
                paramMap.put("ucId", ucIdForAdd);
                paramMap.put("operationId", "0");
                paramMap.put("addType", "6");
                HttpUtil.postWithRequestIntegral(integralRuleUrl, paramMap, header);


                log.info("[用户预约并接车添加积分]end");

            }
        } catch (Exception acce) {
            log.info("[scanReceive] acc error" + acce);
        }

        // 接车成功后删除redis key
        if (redisTemplate.hasKey(scanLimitRk)) {
            redisTemplate.delete(scanLimitRk);
        }
        // 保存仪表里程
        double scanReceviceMg = 0;
        if (dto != null) {
            scanReceviceMg = dto.getMileage() == null ? 0 : dto.getMileage();
        }
        asySaveMeterMileageWoService.saveScanReceiveMeterMileage(woCode, chassisNum, scanReceviceMg, date);

        Map<String, Object> resultMap = new HashMap<>();
        List<ServiceAarNoticeDto> servicesList = serviceAarNoticeMapper.queryServiceAarNotices(workOrder.getStationCode(), chassisNum, Arrays.asList(queryNoticeType.split(",")));
        if (servicesList == null) {
            servicesList = new ArrayList<>();
        }
        resultMap.put("serviceActivities", servicesList);
        result.setData(resultMap);

        log.info("[scanReceive] end");
        return result;
    }

    /**
     * 插入工单操作记录
     */
    private void insertOperateRecordCancel(RescueCancelForm command) throws JsonProcessingException {
        WorkOrderOperate insertEntity = new WorkOrderOperate();
        insertEntity.setWoCode(command.getWoCode());
        insertEntity.setOperateCode(OperateCodeEnum.OP_ABORT_RESCUE.code());
        insertEntity.setDescription(command.getReason());
        insertEntity.setTitle(OperateCodeEnum.OP_ABORT_RESCUE.message());
        Map<String, String> jsonMap = new LinkedHashMap<>(7);
        jsonMap.put("取消原因:", command.getReason());
        jsonMap.put("取消账号:", command.getUserId());
        Date now = new Date();
        jsonMap.put("取消时间:", DateUtil.formatDate(DateUtil.TIME_PATTERN_MINUTE, now));
        // 逆地理调用
        String location = queryGeographicalService.getPosition(command.getLat(), command.getLon());
        jsonMap.put("取消时地址:", location);
        insertEntity.setTextJson(JsonUtil.toJson(jsonMap));
        insertEntity.setHiddenFlg(0);
        insertEntity.setLongitude(command.getLon());
        insertEntity.setLatitude(command.getLat());
        insertEntity.setUserId(command.getUserId());
        insertEntity.setCreateTime(now);
        insertEntity.setUpdateTime(now);
        workOrderOperateMapper.insertSelective(insertEntity);
    }

    /**
     * 更新工单状态
     */
    private void updateWoStatusClose(Long id) {
        WorkOrder updateEntity = new WorkOrder();
        updateEntity.setId(id);
        // 工单状态
        updateEntity.setWoStatus(ServiceStationEnum.CLOSE_RESCUE.code());
        Date now = new Date();
        // 关闭时间
        updateEntity.setTimeClose(now);
        // 更新时间
        updateEntity.setUpdateTime(now);
        workOrderMapper.updateByPrimaryKeySelective(updateEntity);
    }

    /**
     * 校验入参
     */
    private String validateCommand(RescueCancelForm command) {
        try {
            double lon = Double.parseDouble(command.getLon());
        } catch (NumberFormatException nfe) {
            return "取消救援经度格式不正确";
        }
        try {
            double lat = Double.parseDouble(command.getLat());
        } catch (NumberFormatException nfe) {
            return "取消救援纬度格式不正确";
        }
        return "";
    }

    /**
     * 发送邮件通知
     *
     * @param woCode  工单号
     * @param content 额外内容
     */
    private void sendEmail(String woCode, String content) {
        if (StringUtil.isNotEq("dummy", callBaiduApiErrEmails)) {
            String ip;
            try {
                ip = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                ip = "未知IP";
            }
            String identifier = "【" + envIdentifier + "】【" + ip + "】";
            boolean flag = MailSenderUtil.sendCommonEmailList(
                    sendEmailServerUrl,
                    identifier + "调用百度接口异常告警",
                    "调用失败的工单号为：【" + woCode + "】" + "  额外信息：" + content,
                    callBaiduApiErrEmails);
            if (!flag) {
                log.error("发送邮件失败，邮件内容为：【{}】【{}】", woCode, content);
            }
        }
    }

    /**
     * 校验车辆与接车手机距离
     *
     * @param carLocationReportTime 秒
     * @param distance              km
     * @return true：校验不通过；false：校验通过
     */
    private boolean scanReceiveDistanceCheck(long carLocationReportTime, double distance) {
        log.info("[scanReceiveDistanceCheck]start");
        boolean checkResult = false;
        // 1.当车辆位置的上报时间<2分钟。限制接车距离1000米才能接车成功。如果连续5次接车失败后，取消接车距离限制。”
        if (carLocationReportTime < scanReceiveTimeMin && distance > scanReceiveDistance) {
            checkResult = true;
        } else if (carLocationReportTime >= scanReceiveTimeMin && carLocationReportTime < scanReceiveTimeMax) {
            // 2.若车辆最后一次位置上报时间与当前接车时间相差2分钟以上,60分钟以下(（ 2m≥X<60m)，接车距离L=1+90/60*X，接车距离≤L才能接车成功。
            // 1单位KM，代表默认接车距离1000M；90为车辆平均时速90KM，X为车辆位置上报时间与当前接车时间的时差，
            // X=当前接车时间-车辆位置上报时间。X单位分钟，时间差不足1分钟部分按1分钟计算。
            // 如果连续5次接车失败后，取消接车距离限制。
            double travelTime = MathUtil.scale((double) carLocationReportTime / 60, 1).doubleValue();
            double modifyDistance = scanReceiveDistance + 1.5 * travelTime;
            if (distance > modifyDistance) {
                checkResult = true;
            }
        } else if (carLocationReportTime >= scanReceiveTimeMax) {
            // 3.若车辆最后一次位置的上报时间大于当前接车时间60分钟以上，尝试接车2次后取消接车距离限制。
            checkResult = true;
        }
        log.info("[scanReceiveDistanceCheck] end");
        return checkResult;
    }

    @Transactional(rollbackFor = Exception.class)
    public RescueRoutePoint assemblyData(String woCode, String phoneLon, String phoneLat, Integer maxIndex) throws Exception {

        // 救援轨迹信息查询
        RescueRoutePoint rescueRoutePoint = rescueRoutePointMapper.selectByWoCode(woCode);
        if (rescueRoutePoint == null) {
            log.info("[preSavePoint]rescueRoutePoint is null");
            return null;
        }
        RescueRoutePoint point = new RescueRoutePoint();


        if (StringUtil.isNotEmpty(phoneLon) && StringUtil.isNotEmpty(phoneLat) && maxIndex != null) {
            // 把最终点并入原始点
            PointDto finalPoint = new PointDto();
            finalPoint.setIndex(maxIndex);
            finalPoint.setLongitude(Double.parseDouble(phoneLon));
            finalPoint.setLatitude(Double.parseDouble(phoneLat));
            finalPoint.setTime(String.valueOf(System.currentTimeMillis()));
            finalPoint.setRadius(1.0);
            // 获取工单信息
            Map<String, String> param = new HashMap<>(1);

            String url = baseUrl + "/location/track/queryOrder";
            Map<String, Object> map = new HashMap<>();
            param.put("woCode", woCode);
            WorkOrder workOrder = workOrderMapper.selectByWoCode(param);
//            map.put("terminalId","IZS024012021040715430002");
            map.put("terminalId", workOrder.getWoCode());
            map.put("startTime", workOrder.getCreateTime().getTime() / 1000);
            map.put("endTime", System.currentTimeMillis() / 1000);
            String res = com.nut.servicestation.common.utils.HttpUtil.postHttps(url, net.sf.json.JSONObject.fromObject(map));
            Map<String, Object> resMap = JsonUtil.toMap(res);
            List<PointDto> pointDtos = new ArrayList<>();
            if (Objects.nonNull(resMap.get("resultCode")) && resMap.get("resultCode").toString().equals("200") && resMap.get("data") != null) {
                String JSONStr = JSON.toJSONString(resMap.get("data"));
                JSONArray jsonArray = JSONArray.parseArray(JSONStr);
//                JSONArray jsonArray = JSONArray.fromObject(JSONStr);
                for (int i = 0; i < jsonArray.size(); i++) {
                    PointDto pointDto = (PointDto) net.sf.json.JSONObject.toBean(net.sf.json.JSONObject.fromObject(jsonArray.get(i)), PointDto.class);
                    pointDto.setTime(net.sf.json.JSONObject.fromObject(jsonArray.get(i)).get("gpsDate").toString() + "000");
                    if (pointDto.getRadius() == null) {
                        pointDto.setRadius(new Double("1"));
                    }
                    pointDtos.add(pointDto);
                }
            }

            final Integer maxIndexFinal = maxIndex;
            List<PointDto> collect = pointDtos.stream().filter(item -> item.getIndex() < maxIndexFinal).collect(Collectors.toList());
            // 检查最后的轨迹点是否正确
            try {
                boolean phoneLatLonCheck = externalMapService.isValidLatLon(phoneLat, phoneLon);
                if (!phoneLatLonCheck) {
                    if (collect != null) {
                        PointDto lastPt = collect.get(collect.size() - 1);
                        if (lastPt != null) {
                            if (DateUtil.diffNDate(Long.parseLong(lastPt.getTime()) * 1000) <= appCarUpinfoLimit) {
                                finalPoint.setLatitude(lastPt.getLatitude());
                                finalPoint.setLongitude(lastPt.getLongitude());
                            }
                        }
                    }

                }
            } catch (Exception e) {
                log.error("[preSavePoint]check lat lon error:", e);
            }
            collect.add(finalPoint);
            point.setOriginalPoints(JsonUtil.toJson(collect));
            rescueRoutePoint.setOriginalPoints(JsonUtil.toJson(collect));
        }
        /**
         * 当申请关闭时，APP页面已经传来轨迹点最大序列号并入库，当被驳回，
         * 继续救援并确认接车时，APP又传了一个最大轨迹点序列号，此时有鱼之前已经入库最大轨迹点序列号，所以造成计算里程出错。
         * 暂时先将其注释。
         */
        // 救援轨迹信息更新
        point.setId(rescueRoutePoint.getId());
        point.setMaxIndex(maxIndex);
        List<PointDto> originalPoints = JsonUtil.toList(rescueRoutePoint.getOriginalPoints(), PointDto.class);

        point.setUpdateTime(new Date());
        rescueRoutePointMapper.updateByPrimaryKeySelective(point);
        point.setWoCode(rescueRoutePoint.getWoCode());

        log.info("[preSavePoint]updated");
        return point;
    }

    /**
     * 接车推送
     */
    private void pushReceive(WorkOrder workOrder, String senderId) {
        log.info("[pushReceive]start");
        try {
            // 通配符替换
            Map<String, String> wildcardMap = new HashMap<>(7);
            String chassisNum = workOrder.getChassisNum();
            String carNumber = carMapper.queryCarNumberByVin(chassisNum);
            wildcardMap.put("{优先车牌号}", StringUtil.isNotEmpty(carNumber) ? carNumber : chassisNum);
            wildcardMap.put("{工单号}", workOrder.getWoCode());
            String stype;
            if (STATION_SERVICE == workOrder.getWoType()) {
                // 进出站
                wildcardMap.put("{服务站名称}", workOrder.getStationName());
                stype = PushStaticLocarVal.PUSH_TYPE_TEN_STYPE_RECEIVE_IN;
            } else {
                // 外出救援
                stype = PushStaticLocarVal.PUSH_TYPE_TEN_STYPE_RECEIVE_OUT;
            }
            String wildcard = JsonUtil.toJson(wildcardMap);
            // 扩展信息
            Map<String, String> messageExtraMap = new HashMap<>(3);
            // 工单号
            messageExtraMap.put(PushStaticLocarVal.MESSAGE_EXTRA_WO_CODE, workOrder.getWoCode());
            String messageExtra = JsonUtil.toJson(messageExtraMap);
            asyPushMessageService.pushToDriverAndOwner(stype, wildcard, messageExtra, senderId, null, chassisNum);
        } catch (Exception e) {
            log.info("[pushReceive]Exception:", e);
        }
        log.info("[pushReceive]end");
    }

    public String exchangeNumber(Object obj){
        Long number = Long.parseLong(obj.toString());
        BigDecimal a = new BigDecimal(number);
        BigDecimal b = new BigDecimal(1000000L);
        return a.divide(b).toString();
    }

}
