package com.nut.driver.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.dozermapper.core.Mapper;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.PagingInfo;
import com.nut.common.utils.DateUtil;
import com.nut.common.utils.HttpUtil;
import com.nut.common.utils.JsonUtil;
import com.nut.common.utils.StringUtil;
import com.nut.driver.app.dao.*;
import com.nut.driver.app.dto.*;
import com.nut.driver.app.entity.CarAnalyseEntity;
import com.nut.driver.app.entity.FltFleetCarMappingEntity;
import com.nut.driver.app.form.*;
import com.nut.driver.app.pojo.*;
import com.nut.driver.app.service.StatisticsService;
import com.nut.driver.common.utils.PageUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

/**
 * @author liuBing
 * @Classname StatisticsServiceImpl
 * @Description TODO
 * @Date 2021/6/19 17:11
 */
@Slf4j
@Service("StatisticsService")
@Transactional(rollbackFor = Exception.class, readOnly = true)
public class StatisticsServiceImpl extends ServiceImpl<StatisticsDao, CarAnalyseEntity> implements StatisticsService {


    /**
     * 不良驾驶行为名称，注意顺序与索引值一一对应
     */
    private static final String[] BAD_BEHAVIOR_NAME = {
            "超速", "急加速", "急减速", "急转弯", "超长怠速", "冷车运行", "夜晚开车", "引擎高转速", "全油门", "大油门",
            "空挡滑行", "熄火滑行"};
    /**
     * bean 转换工具
     */
    @Resource
    private Mapper convert;

    @Autowired
    private FltFleetUserMappingDao fltFleetUserMappingMapper;
    /**
     * 查询范围
     */
    @Value("${query_scope:60}")
    private long queryScope;
    /**
     * 最大查询范围
     */
    @Value("${max_query_scope:365}")
    private long maxQueryScope;

    @Value("${localcloud.getdata.url}")
    private String getBiDataUrl;

    @Value("${getTripAnalysisListUrl}")
    private String getTripAnalysisList;

    @Autowired
    private FltCarOwnerMappingDao fltCarOwnerMappingMapper;

    @Autowired
    private StatisticalDao statisticalMapper;

    @Autowired
    private CarDao carMapper;

    @Autowired
    private PageUtil pageUtil;
    @Autowired
    private FltFleetCarMappingDao fltFleetCarMappingDao;

    @Override
    public HttpCommandResultWithData carReport(StatisticsCarReportForm command) {


        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        // 查询时间校验
        String checkResult = checkQueryTime(command.getBeginDate(), command.getEndDate());
        if (StringUtil.isNotEmpty(checkResult)) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage(checkResult);
            return result;
        }
        List<FltStatisticalPojo> totalList = new ArrayList<>();

        List<FltStatisticalPojo> sameDayList = null;
        List<FltStatisticalPojo> nonSameDayList = null;

        // 查询作为车主的车辆
        List<String> carIds = fltCarOwnerMappingMapper.queryOwnerCars(command.getAutoIncreaseId());

        // 包含当天日期
        if (DateUtil.diffNowDate(command.getEndDate()) == 3) {
            sameDayList = queryStatisticalOfMilAndFuelFromLocationCloud(command.getAutoIncreaseId(), carIds);
        }
        // 非当天日期
        if (DateUtil.diffNowDate(command.getBeginDate()) == 2) {
            try {
                nonSameDayList = queryStatisticalByFeetId(DateUtil.dateFormatConversion(command.getBeginDate(), DateUtil.date_s_pattern), DateUtil.dateFormatConversion(command.getEndDate(), DateUtil.date_s_pattern), command.getAutoIncreaseId(), carIds);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (sameDayList != null && sameDayList.size() > 0) {
            totalList.addAll(sameDayList);
        }
        if (nonSameDayList != null && nonSameDayList.size() > 0) {
            totalList.addAll(nonSameDayList);
        }

        // 数据统计时间范围
        long dateInterval = DateUtil.diffByDay(command.getBeginDate(), command.getEndDate(), "") + 1;
        try {
            log.info("统计集合:{}", JsonUtil.toJson(totalList));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        // 统计
        result.setData(statisticsCarReport(totalList, dateInterval));
        log.info("carReport end return:{}", result.getData());
        return result;
    }

    /**
     * 查询车队下车辆里程油耗不良驾驶行为信息
     * 注：非当天数据
     *
     * @param startTime
     * @param endTime
     * @param userId
     * @return
     * @throws Exception
     */
    public List<FltStatisticalPojo> queryStatisticalByFeetId(Date startTime, Date endTime, Long userId, List<String> carIds) throws Exception {
        List<FltStatisticalPojo> resultList = new ArrayList<>();

        // 查询所在车队
        List<Long> fleetIds = fltFleetUserMappingMapper.queryFleetIdsByUserId(userId);

        if (carIds.size() == 0 && fleetIds.size() == 0) {
            return null;
        }
        // 作为除车主外的车辆数据
        List<FltStatisticalPojo> nonOwnerList = null;
        // 作为车主的车辆的数据
        List<FltStatisticalPojo> ownerList = null;
        if (carIds.size() > 0) {
            ownerList = statisticalMapper.queryOwnerCarsStatistical(startTime, endTime, carIds);
        }

        if (fleetIds.size() > 0) {
            nonOwnerList = queryCarAnalyseByDateAndTeamIds(startTime, endTime, fleetIds);
        }

        if (nonOwnerList != null && nonOwnerList.size() > 0) {
            resultList.addAll(nonOwnerList);
        }
        if (ownerList != null && ownerList.size() > 0) {
            List<CarInfoPojo> carList = carMapper.selectByCarIdIn(carIds);
            if (CollectionUtils.isNotEmpty(carList)) {
                Map<String, CarInfoPojo> carMap = carList.stream().collect(Collectors.toMap(CarInfoPojo::getCarId, car -> car));
                for (FltStatisticalPojo pojo : ownerList) {
                    CarInfoPojo cp = carMap.get(pojo.getCarId());
                    if (cp != null) {
                        pojo.setCarNum(StringUtil.isEmpty(cp.getCarNumber()) ? cp.getCarVin() : cp.getCarNumber());
                        pojo.setAutoTerminal(cp.getAutoTerminal());
                        pojo.setCarVin(cp.getCarVin());
                    }
                }
            }
            resultList.addAll(ownerList);
        }
        // 按车辆ID和日期去重
        resultList = resultList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getCarId() + ";" + o.getStatisDate()))), ArrayList::new));
        return resultList;
    }

    private List<FltStatisticalPojo> queryCarAnalyseByDateAndTeamIds(Date startTime, Date endTime, List<Long> fleetIds) {
        log.info("[queryCarAnalyseByDateAndTeamIds]start");
        List<FltStatisticalPojo> resultList = statisticalMapper.queryStatisticalByFleetIds(startTime, endTime, fleetIds);
        if (resultList.isEmpty()) {
            log.info("[queryCarAnalyseByDateAndTeamIds]return empty");
            return new ArrayList<>();
        }
        // 单独查car表
        List<CarInfoPojo> carInfoPojos = carMapper.selectByCarIdIn(
                resultList.stream().map(FltStatisticalPojo::getCarId).collect(Collectors.toList()));
        Map<String, CarInfoPojo> carInfoMap = carInfoPojos.stream().collect(Collectors.toMap(CarInfoPojo::getCarId, e -> e));
        // 合并信息
        for (FltStatisticalPojo result : resultList) {
            CarInfoPojo c = carInfoMap.get(result.getCarId());
            if (c != null) {
                result.setCarNum(StringUtil.isEmpty(c.getCarNumber()) ? c.getCarVin() : c.getCarNumber());
                result.setAutoTerminal(c.getAutoTerminal());
                result.setCarVin(c.getCarVin());
            }
        }
        log.info("[queryCarAnalyseByDateAndTeamIds]end");
        return resultList;
    }

    /**
     * 查询当日里程油耗不良驾驶行为数据
     * 注：当日数据
     *
     * @param userId
     * @return
     */
    public List<FltStatisticalPojo> queryStatisticalOfMilAndFuelFromLocationCloud(Long userId, List<String> carIds) {

        // 查询用户所在车队的所有车辆
        List<String> carIdsTeam = carMapper.queryAllCarIdByUserId(userId);

        if (carIdsTeam.size() > 0) {
            carIds.addAll(carIdsTeam);
        }

        if (carIds.size() == 0) {
            return null;
        }

        // 车辆ID去重
        carIds = carIds.stream().distinct().collect(Collectors.toList());

        List<FltStatisticalPojo> cars = carMapper.queryUserRelatedCarsByCarIds(carIds);
        if (cars.isEmpty()) {
            return cars;

        }
        List<String> autoTerminals = cars.stream().map(FltStatisticalPojo::getAutoTerminal).collect(Collectors.toList());

        // 当日里程油耗等信息
        List<MileageAndOilPojo> milAndOilList = getCurrentDayEntityList(autoTerminals);
        Map<String, MileageAndOilPojo> milAndOilMap = new HashMap<>();
        if (milAndOilList != null && !milAndOilList.isEmpty()) {
            milAndOilMap = milAndOilList.stream().collect(Collectors.toMap(MileageAndOilPojo::getTerminalId, Function.identity()));
        }
        // 当日不良驾驶行为信息
        List<FltStatisticalPojo> badDrivingList = querySameDayBadDriving(carIds);//querySameDayBadDriving(autoTerminals);
        Map<String, FltStatisticalPojo> badDrivingMap = new HashMap<>();
        if (!badDrivingList.isEmpty()) {
            badDrivingMap = badDrivingList.stream().collect(Collectors.toMap(FltStatisticalPojo::getAutoTerminal, Function.identity()));
        }

        // 将里程、油耗、不良驾驶行为等数据合并到车辆信息中
        for (FltStatisticalPojo fltStatisticalPojo : cars) {
            fltStatisticalPojo.setStatisDate(DateUtil.getNowDate_yyyyMMdd());
            // 车牌号为空时，将车牌号变为底盘号
            if (StringUtil.isEmpty(fltStatisticalPojo.getCarNum())) {
                fltStatisticalPojo.setCarNum(fltStatisticalPojo.getCarVin());
            }
            // 合并里程油耗
            if (!milAndOilMap.isEmpty()) {
                MileageAndOilPojo mileageAndOilPojo = milAndOilMap.get(fltStatisticalPojo.getAutoTerminal());
                if (mileageAndOilPojo != null) {
                    fltStatisticalPojo.setMileage((float) mileageAndOilPojo.getmMilage());
                    fltStatisticalPojo.setOilwear((float) mileageAndOilPojo.getFuel());
                    fltStatisticalPojo.setRunOil((float) mileageAndOilPojo.getRunFuel());
                    fltStatisticalPojo.setIdlingOil((float) mileageAndOilPojo.getIdlingFuel());
                    fltStatisticalPojo.setTimeTotal((long) (mileageAndOilPojo.getWorkHours() * 1000));
                    fltStatisticalPojo.setIdleTime((long) (mileageAndOilPojo.getIdleHours() * 1000));
                    fltStatisticalPojo.setSpeedSum(mileageAndOilPojo.getSpeedSum());
                    fltStatisticalPojo.setSpeedCount(mileageAndOilPojo.getSpeedNum());
                }
            }
            // 合并不良驾驶行为
            if (!badDrivingMap.isEmpty()) {
                FltStatisticalPojo badFsp = badDrivingMap.get(fltStatisticalPojo.getAutoTerminal());
                if (badFsp != null) {
                    // 超速
                    fltStatisticalPojo.setOverSpeedCnt(badFsp.getOverSpeedCnt());
                    // 急加速
                    fltStatisticalPojo.setRaCnt(badFsp.getRaCnt());
                    // 急减速
                    fltStatisticalPojo.setRdCnt(badFsp.getRdCnt());
                    // 急转弯
                    fltStatisticalPojo.setSharpTurnCnt(badFsp.getSharpTurnCnt());
                    // 超长怠速
                    fltStatisticalPojo.setIdleTimeoutCnt(badFsp.getIdleTimeoutCnt());
                    // 冷车启动
                    fltStatisticalPojo.setColdRunCnt(badFsp.getColdRunCnt());
                    // 夜晚开车
                    fltStatisticalPojo.setNightRunCnt(badFsp.getNightRunCnt());
                    // 引擎高转速
                    fltStatisticalPojo.setLowGrHighSpdCnt(badFsp.getLowGrHighSpdCnt());
                    // 全油门
                    fltStatisticalPojo.setFullThrottleCnt(badFsp.getFullThrottleCnt());
                    // 大油门
                    fltStatisticalPojo.setRoughThrottleCnt(badFsp.getRoughThrottleCnt());
                    // 空挡滑行
                    fltStatisticalPojo.setNeutralGearCoastCnt(badFsp.getNeutralGearCoastCnt());
                    // 熄火滑行在当天的统计数据中无法统计
                    fltStatisticalPojo.setStallCoastCnt(badFsp.getStallCoastCnt());
                }
            }
        }
        return cars;
    }


    /**
     * 查询车队下车辆里程油耗不良驾驶行为信息
     * 注：非当天数据
     *
     * @param startTime
     * @param endTime
     * @param userId
     * @return
     * @throws Exception
     */
    public List<FltStatisticalPojo> queryStatisticalByFeetId(Date startTime, Date endTime,  List<String> carIds) throws Exception {
        List<FltStatisticalPojo> resultList = new ArrayList<>();

        // 作为除车主外的车辆数据
        List<FltStatisticalPojo> nonOwnerList = null;
        // 作为车主的车辆的数据
        List<FltStatisticalPojo> ownerList = null;
        if (carIds.size() > 0) {
            ownerList = statisticalMapper.querLineOwnerCarsStatistical(startTime, endTime, carIds);
        }

        if (nonOwnerList != null && nonOwnerList.size() > 0) {
            resultList.addAll(nonOwnerList);
        }
        if (ownerList != null && ownerList.size() > 0) {
            List<CarInfoPojo> carList = carMapper.selectByCarIdIn(carIds);
            if (CollectionUtils.isNotEmpty(carList)) {
                Map<String, CarInfoPojo> carMap = carList.stream().collect(Collectors.toMap(CarInfoPojo::getCarId, car -> car));
                for (FltStatisticalPojo pojo : ownerList) {
                    CarInfoPojo cp = carMap.get(pojo.getCarId());
                    if (cp != null) {
                        pojo.setCarNum(StringUtil.isEmpty(cp.getCarNumber()) ? cp.getCarVin() : cp.getCarNumber());
                        pojo.setAutoTerminal(cp.getAutoTerminal());
                        pojo.setCarVin(cp.getCarVin());
                    }
                }
            }
            resultList.addAll(ownerList);
        }
        // 按车辆ID和日期去重
        resultList = resultList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getCarId() + ";" + o.getStatisDate()))), ArrayList::new));
        return resultList;
    }

    /**
     * 查询当日里程油耗不良驾驶行为数据
     * 注：当日数据
     *
     * @return
     */
    public List<FltStatisticalPojo> queryStatisticalOfMilAndFuelFromLocationCloud( List<String> carIds) {

        List<FltStatisticalPojo> cars = carMapper.queryUserRelatedCarsByCarIds(carIds);
        if (cars.isEmpty()) {
            return cars;

        }
        List<String> autoTerminals = cars.stream().map(FltStatisticalPojo::getAutoTerminal).collect(Collectors.toList());

        // 当日里程油耗等信息
        List<MileageAndOilPojo> milAndOilList = getCurrentDayEntityList(autoTerminals);
        Map<String, MileageAndOilPojo> milAndOilMap = new HashMap<>();
        if (milAndOilList != null && !milAndOilList.isEmpty()) {
            milAndOilMap = milAndOilList.stream().collect(Collectors.toMap(MileageAndOilPojo::getTerminalId, Function.identity()));
        }
        // 当日不良驾驶行为信息
        List<FltStatisticalPojo> badDrivingList = querySameDayBadDriving(carIds);//querySameDayBadDriving(autoTerminals);
        Map<String, FltStatisticalPojo> badDrivingMap = new HashMap<>();
        if (!badDrivingList.isEmpty()) {
            badDrivingMap = badDrivingList.stream().collect(Collectors.toMap(FltStatisticalPojo::getAutoTerminal, Function.identity()));
        }

        // 将里程、油耗、不良驾驶行为等数据合并到车辆信息中
        for (FltStatisticalPojo fltStatisticalPojo : cars) {
            fltStatisticalPojo.setStatisDate(DateUtil.getNowDate_yyyyMMdd());
            // 车牌号为空时，将车牌号变为底盘号
            if (StringUtil.isEmpty(fltStatisticalPojo.getCarNum())) {
                fltStatisticalPojo.setCarNum(fltStatisticalPojo.getCarVin());
            }
            // 合并里程油耗
            if (!milAndOilMap.isEmpty()) {
                MileageAndOilPojo mileageAndOilPojo = milAndOilMap.get(fltStatisticalPojo.getAutoTerminal());
                if (mileageAndOilPojo != null) {
                    fltStatisticalPojo.setMileage((float) mileageAndOilPojo.getmMilage());
                    fltStatisticalPojo.setOilwear((float) mileageAndOilPojo.getFuel());
                    fltStatisticalPojo.setRunOil((float) mileageAndOilPojo.getRunFuel());
                    fltStatisticalPojo.setIdlingOil((float) mileageAndOilPojo.getIdlingFuel());
                    fltStatisticalPojo.setTimeTotal((long) (mileageAndOilPojo.getWorkHours() * 1000));
                    fltStatisticalPojo.setIdleTime((long) (mileageAndOilPojo.getIdleHours() * 1000));
                    fltStatisticalPojo.setSpeedSum(mileageAndOilPojo.getSpeedSum());
                    fltStatisticalPojo.setSpeedCount(mileageAndOilPojo.getSpeedNum());
                }
            }
            // 合并不良驾驶行为
            if (!badDrivingMap.isEmpty()) {
                FltStatisticalPojo badFsp = badDrivingMap.get(fltStatisticalPojo.getAutoTerminal());
                if (badFsp != null) {
                    // 超速
                    fltStatisticalPojo.setOverSpeedCnt(badFsp.getOverSpeedCnt());
                    // 急加速
                    fltStatisticalPojo.setRaCnt(badFsp.getRaCnt());
                    // 急减速
                    fltStatisticalPojo.setRdCnt(badFsp.getRdCnt());
                    // 急转弯
                    fltStatisticalPojo.setSharpTurnCnt(badFsp.getSharpTurnCnt());
                    // 超长怠速
                    fltStatisticalPojo.setIdleTimeoutCnt(badFsp.getIdleTimeoutCnt());
                    // 冷车启动
                    fltStatisticalPojo.setColdRunCnt(badFsp.getColdRunCnt());
                    // 夜晚开车
                    fltStatisticalPojo.setNightRunCnt(badFsp.getNightRunCnt());
                    // 引擎高转速
                    fltStatisticalPojo.setLowGrHighSpdCnt(badFsp.getLowGrHighSpdCnt());
                    // 全油门
                    fltStatisticalPojo.setFullThrottleCnt(badFsp.getFullThrottleCnt());
                    // 大油门
                    fltStatisticalPojo.setRoughThrottleCnt(badFsp.getRoughThrottleCnt());
                    // 空挡滑行
                    fltStatisticalPojo.setNeutralGearCoastCnt(badFsp.getNeutralGearCoastCnt());
                    // 熄火滑行在当天的统计数据中无法统计
                    fltStatisticalPojo.setStallCoastCnt(badFsp.getStallCoastCnt());
                }
            }
        }
        return cars;
    }

    @Override
    public String checkQueryTime(String startTime, String endTime) {
        // 大小校验
        if (startTime.compareTo(endTime) > 0) {
            return "统计开始日期不能大于结束日期";
        }
        // 校验结束日期是否大于当前日期
        if (DateUtil.diffNowDate(endTime) == 1) {
            return "统计结束日期不能大于当前日期";
        }
        // 最大统计范围60天
        if (DateUtil.diffByDay(startTime, endTime, "") > queryScope) {
            return "请选择跨度小于60天的自定义时间范围";
        }
        // 最大统计范围60天
        if (DateUtil.diffByDay(startTime, DateUtil.getNowDate_yyyyMMdd(), "") > maxQueryScope) {
            return "请统计一年内的数据";
        }
        return "";
    }

    @Override
    public List<MileageAndOilPojo> getCurrentDayEntityList(List<String> autoTerminals) {
        List<MileageAndOilPojo> list = new ArrayList<>();

        if (autoTerminals != null && autoTerminals.size() > 0) {
            Map<String, Object> map = new HashMap<>();
            map.put("terminalIds", autoTerminals);
            // 获取当前0点秒级时间戳
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            long time = calendar.getTimeInMillis() / 1000;
            map.put("startTime", time);
            map.put("endTime", time);
            list = HttpUtil.postJsonRequest(getBiDataUrl, map, new TypeReference<HttpCommandResultWithData<List<MileageAndOilPojo>>>() {
            });
        }
        return list;
    }

    @Override
    public List<FltStatisticalPojo> querySameDayBadDriving(List<String> autoTerminals) {
        List<FltStatisticalPojo> fltStatisticalPojos = new ArrayList<>();
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Calendar calendar = Calendar.getInstance();
            //开始结束时间，精确到秒
            long startTime = dateFormat.parse(dateFormat.format(calendar.getTime())).getTime();
            long endTime = startTime + 86399000;

            HashMap<String, Object> param = new HashMap<>(6);
            param.put("tids", autoTerminals);
            param.put("start", DateUtil.timeStampToDate(startTime));
            param.put("end", DateUtil.timeStampToDate(endTime));
            param.put("isFilter", 1);
            param.put("page_size", 99999);
            param.put("page_number", 1);

            // 调用位置云行程列表接口
            CarTripPojo carTripPojo = HttpUtil.postJsonRequest(getTripAnalysisList, param, new TypeReference<HttpCommandResultWithData<CarTripPojo>>() {
            });

            if (carTripPojo == null || carTripPojo.getData() == null || carTripPojo.getData().size() == 0) {
                return fltStatisticalPojos;
            }

            List<CarTripListPojo> reList = carTripPojo.getData();
            // 转MAP
            Map<String, List<CarTripListPojo>> groupBy = reList.stream().collect(Collectors.groupingBy(CarTripListPojo::getTerminalId));

            String statisDate = DateUtil.getNowDate_yyyyMMdd();
            // 按车统计不良驾驶行为数
            FltStatisticalPojo fltStatisticalPojo;
            for (Map.Entry<String, List<CarTripListPojo>> entry : groupBy.entrySet()) {
                String mapKey = entry.getKey();
                List<CarTripListPojo> list = entry.getValue();

                fltStatisticalPojo = new FltStatisticalPojo();
                fltStatisticalPojo.setStatisDate(statisDate);
                fltStatisticalPojo.setAutoTerminal(mapKey);

                int dfOverSpeed = 0;
                int dfRapidAcceleration = 0;
                int dfRapidDeceleration = 0;
                int sharpTurn = 0;
                int vehicleLongParkingIdle = 0;
                int carColdStart = 0;
                int inNight = 0;
                int engineHighSpeed = 0;
                int fullAccelerator = 0;
                int largeAccelerator = 0;
                int idlingSlide = 0;
                int dfStallingSlide = 0;
                for (CarTripListPojo carTripListPojo : list) {
                    dfOverSpeed += carTripListPojo.getDfOverSpeedFrequency();
                    dfRapidAcceleration += carTripListPojo.getDfSharpUpSpeedFrequency();
                    dfRapidDeceleration += carTripListPojo.getDfSharpDownSpeedFrequency();
                    sharpTurn += carTripListPojo.getSharpTurnFrequency();
                    vehicleLongParkingIdle += carTripListPojo.getLongParkingIdleNumber();
                    carColdStart += carTripListPojo.getDfVehicleColdStartFrequency();
                    inNight += carTripListPojo.getInNightFrequency();
                    engineHighSpeed += carTripListPojo.getEngineOverSpeedNumber();
                    fullAccelerator += carTripListPojo.getFullAcceleratorFrequency();
                    largeAccelerator += carTripListPojo.getLargeAcceleratorFrequency();
                    idlingSlide += carTripListPojo.getIdlingFrequency();
                    dfStallingSlide += carTripListPojo.getStallingSlideFrequency();
                }

                // 超速
                fltStatisticalPojo.setOverSpeedCnt(dfOverSpeed);
                // 急加速
                fltStatisticalPojo.setRaCnt(dfRapidAcceleration);
                // 急减速
                fltStatisticalPojo.setRdCnt(dfRapidDeceleration);
                // 急转弯
                fltStatisticalPojo.setSharpTurnCnt(sharpTurn);
                // 超长怠速
                fltStatisticalPojo.setIdleTimeoutCnt(vehicleLongParkingIdle);
                // 冷车启动
                fltStatisticalPojo.setColdRunCnt(carColdStart);
                // 夜晚开车
                fltStatisticalPojo.setNightRunCnt(inNight);
                // 引擎高转速
                fltStatisticalPojo.setLowGrHighSpdCnt(engineHighSpeed);
                // 全油门
                fltStatisticalPojo.setFullThrottleCnt(fullAccelerator);
                // 大油门
                fltStatisticalPojo.setRoughThrottleCnt(largeAccelerator);
                // 空挡滑行
                fltStatisticalPojo.setNeutralGearCoastCnt(idlingSlide);
                // 熄火滑行
                fltStatisticalPojo.setStallCoastCnt(dfStallingSlide);
                fltStatisticalPojos.add(fltStatisticalPojo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fltStatisticalPojos;
    }

    @Override
    public HttpCommandResultWithData carReportMileageCarList(StatisticsMileageCarListForm command) throws Exception {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        // 查询时间校验
        String checkResult = checkQueryTime(command.getBeginDate(), command.getEndDate());
        if (StringUtil.isNotEmpty(checkResult)) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage(checkResult);
            return result;
        }
        List<FltStatisticalPojo> totalList = new ArrayList<>();

        List<FltStatisticalPojo> sameDayList = null;
        List<FltStatisticalPojo> nonSameDayList = null;

        // 查询作为车主的车辆
        List<String> carIds = fltCarOwnerMappingMapper.queryOwnerCars(command.getAutoIncreaseId());

        // 包含当天日期
        if (DateUtil.diffNowDate(command.getEndDate()) == 3) {
            sameDayList = queryStatisticalOfMilAndFuelFromLocationCloud(command.getAutoIncreaseId(), carIds);
        }
        // 非当天日期
        if (DateUtil.diffNowDate(command.getBeginDate()) == 2) {
            nonSameDayList = queryStatisticalByFeetId(DateUtil.dateFormatConversion(command.getBeginDate(), DateUtil.date_s_pattern), DateUtil.dateFormatConversion(command.getEndDate(), DateUtil.date_s_pattern), command.getAutoIncreaseId(), carIds);
        }
        if (sameDayList != null && sameDayList.size() > 0) {
            totalList.addAll(sameDayList);
        }
        if (nonSameDayList != null && nonSameDayList.size() > 0) {
            totalList.addAll(nonSameDayList);
        }
        return result.setData(statisticsMileageCarList(totalList, command));
    }

    @Override
    public HttpCommandResultWithData carReportMileageChart(StatisticsCarReportChartForm command) throws Exception {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        // 查询时间校验
        String checkResult = checkQueryTime(command.getBeginDate(), command.getEndDate());
        if (StringUtil.isNotEmpty(checkResult)) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage(checkResult);
            return result;
        }
        List<FltStatisticalPojo> totalList = new ArrayList<>();

        List<FltStatisticalPojo> sameDayList = null;
        List<FltStatisticalPojo> nonSameDayList = null;

        // 查询作为车主的车辆
        List<String> carIds = fltCarOwnerMappingMapper.queryOwnerCars(command.getAutoIncreaseId());

        // 包含当天日期
        if (DateUtil.diffNowDate(command.getEndDate()) == 3) {
            sameDayList = queryStatisticalOfMilAndFuelFromLocationCloud(command.getAutoIncreaseId(), carIds);
        }
        // 非当天日期
        if (DateUtil.diffNowDate(command.getBeginDate()) == 2) {
            nonSameDayList = queryStatisticalByFeetId(DateUtil.dateFormatConversion(command.getBeginDate(), DateUtil.date_s_pattern), DateUtil.dateFormatConversion(command.getEndDate(), DateUtil.date_s_pattern), command.getAutoIncreaseId(), carIds);
        }
        if (sameDayList != null && sameDayList.size() > 0) {
            totalList.addAll(sameDayList);
        }
        if (nonSameDayList != null && nonSameDayList.size() > 0) {
            totalList.addAll(nonSameDayList);
        }
        return result.setData(statisticsMileageChart(totalList, command));
    }

    @Override
    public HttpCommandResultWithData carReportDetailedMileageList(StatisticsMileageCarDetailForm command) throws Exception {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        // 查询时间校验
        String checkResult = checkQueryTime(command.getBeginDate(), command.getEndDate());
        if (StringUtil.isNotEmpty(checkResult)) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage(checkResult);
            return result;
        }
        List<FltStatisticalPojo> totalList = new ArrayList<>();

        List<FltStatisticalPojo> sameDayList = null;
        List<FltStatisticalPojo> nonSameDayList = null;

        // 查询作为车主的车辆
        List<String> carIds = fltCarOwnerMappingMapper.queryOwnerCars(command.getAutoIncreaseId());

        // 包含当天日期
        if (DateUtil.diffNowDate(command.getEndDate()) == 3) {
            sameDayList = queryStatisticalOfMilAndFuelFromLocationCloud(command.getAutoIncreaseId(), carIds);
        }
        // 非当天日期
        if (DateUtil.diffNowDate(command.getBeginDate()) == 2) {
            nonSameDayList = queryStatisticalByFeetId(DateUtil.dateFormatConversion(command.getBeginDate(), DateUtil.date_s_pattern), DateUtil.dateFormatConversion(command.getEndDate(), DateUtil.date_s_pattern), command.getAutoIncreaseId(), carIds);
        }
        if (sameDayList != null && sameDayList.size() > 0) {
            totalList.addAll(sameDayList);
        }
        if (nonSameDayList != null && nonSameDayList.size() > 0) {
            totalList.addAll(nonSameDayList);
        }
        // 车辆里程明细列表
        result.setData(statisticsDetailedMileageList(totalList, command));
        return result;
    }

    @Override
    public HttpCommandResultWithData carReportOilChart(StatisticsCarReportChartForm command) throws Exception {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        // 查询时间校验
        String checkResult = checkQueryTime(command.getBeginDate(), command.getEndDate());
        if (StringUtil.isNotEmpty(checkResult)) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage(checkResult);
            return result;
        }
        List<FltStatisticalPojo> totalList = new ArrayList<>();

        List<FltStatisticalPojo> sameDayList = null;
        List<FltStatisticalPojo> nonSameDayList = null;

        // 查询作为车主的车辆
        List<String> carIds = fltCarOwnerMappingMapper.queryOwnerCars(command.getAutoIncreaseId());

        // 包含当天日期
        if (DateUtil.diffNowDate(command.getEndDate()) == 3) {
            sameDayList = queryStatisticalOfMilAndFuelFromLocationCloud(command.getAutoIncreaseId(), carIds);
        }
        // 非当天日期
        if (DateUtil.diffNowDate(command.getBeginDate()) == 2) {
            nonSameDayList = queryStatisticalByFeetId(DateUtil.dateFormatConversion(command.getBeginDate(), DateUtil.date_s_pattern), DateUtil.dateFormatConversion(command.getEndDate(), DateUtil.date_s_pattern), command.getAutoIncreaseId(), carIds);
        }
        if (sameDayList != null && sameDayList.size() > 0) {
            totalList.addAll(sameDayList);
        }
        if (nonSameDayList != null && nonSameDayList.size() > 0) {
            totalList.addAll(nonSameDayList);
        }

        return result.setData(statisticsOilChart(totalList, command));
    }

    @Override
    public HttpCommandResultWithData carReportIdlOil(StatisticsCarReportIdlOilForm command) throws Exception {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        // 查询时间校验
        String checkResult = checkQueryTime(command.getBeginDate(), command.getEndDate());
        if (StringUtil.isNotEmpty(checkResult)) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage(checkResult);
            return result;
        }
        List<FltStatisticalPojo> totalList = new ArrayList<>();

        List<FltStatisticalPojo> sameDayList = null;
        List<FltStatisticalPojo> nonSameDayList = null;

        // 查询作为车主的车辆
        List<String> carIds = fltCarOwnerMappingMapper.queryOwnerCars(command.getAutoIncreaseId());

        // 包含当天日期
        if (DateUtil.diffNowDate(command.getEndDate()) == 3) {
            sameDayList = queryStatisticalOfMilAndFuelFromLocationCloud(command.getAutoIncreaseId(), carIds);
        }
        // 非当天日期
        if (DateUtil.diffNowDate(command.getBeginDate()) == 2) {
            nonSameDayList = queryStatisticalByFeetId(DateUtil.dateFormatConversion(command.getBeginDate(), DateUtil.date_s_pattern), DateUtil.dateFormatConversion(command.getEndDate(), DateUtil.date_s_pattern), command.getAutoIncreaseId(), carIds);
        }
        if (sameDayList != null && sameDayList.size() > 0) {
            totalList.addAll(sameDayList);
        }
        if (nonSameDayList != null && nonSameDayList.size() > 0) {
            totalList.addAll(nonSameDayList);
        }
        return result.setData(statisticsIdlOil(totalList,command.getAppType()));
    }

    @Override
    public HttpCommandResultWithData carReportOilCarList(StatisticsOilCarListForm command) throws Exception {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        // 查询时间校验
        String checkResult = checkQueryTime(command.getBeginDate(), command.getEndDate());
        if (StringUtil.isNotEmpty(checkResult)) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage(checkResult);
            return result;
        }
        List<FltStatisticalPojo> totalList = new ArrayList<>();

        List<FltStatisticalPojo> sameDayList = null;
        List<FltStatisticalPojo> nonSameDayList = null;

        // 查询作为车主的车辆
        List<String> carIds = fltCarOwnerMappingMapper.queryOwnerCars(command.getAutoIncreaseId());

        // 包含当天日期
        if (DateUtil.diffNowDate(command.getEndDate()) == 3) {
            sameDayList = queryStatisticalOfMilAndFuelFromLocationCloud(command.getAutoIncreaseId(), carIds);
        }
        // 非当天日期
        if (DateUtil.diffNowDate(command.getBeginDate()) == 2) {
            nonSameDayList = queryStatisticalByFeetId(DateUtil.dateFormatConversion(command.getBeginDate(), DateUtil.date_s_pattern), DateUtil.dateFormatConversion(command.getEndDate(), DateUtil.date_s_pattern), command.getAutoIncreaseId(), carIds);
        }
        if (sameDayList != null && sameDayList.size() > 0) {
            totalList.addAll(sameDayList);
        }
        if (nonSameDayList != null && nonSameDayList.size() > 0) {
            totalList.addAll(nonSameDayList);
        }

        return result.setData(statisticsMileageCarList(totalList, command));
    }

    @Override
    public HttpCommandResultWithData carReportDetailedOilList(StatisticsMileageCarDetailForm command) throws Exception {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        // 查询时间校验
        String checkResult = checkQueryTime(command.getBeginDate(), command.getEndDate());
        if (StringUtil.isNotEmpty(checkResult)) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage(checkResult);
            return result;
        }
        List<FltStatisticalPojo> totalList = new ArrayList<>();

        List<FltStatisticalPojo> sameDayList = null;
        List<FltStatisticalPojo> nonSameDayList = null;

        // 查询作为车主的车辆
        List<String> carIds = fltCarOwnerMappingMapper.queryOwnerCars(command.getAutoIncreaseId());

        // 包含当天日期
        if (DateUtil.diffNowDate(command.getEndDate()) == 3) {
            sameDayList = queryStatisticalOfMilAndFuelFromLocationCloud(command.getAutoIncreaseId(), carIds);
        }
        // 非当天日期
        if (DateUtil.diffNowDate(command.getBeginDate()) == 2) {
            nonSameDayList = queryStatisticalByFeetId(DateUtil.dateFormatConversion(command.getBeginDate(), DateUtil.date_s_pattern), DateUtil.dateFormatConversion(command.getEndDate(), DateUtil.date_s_pattern), command.getAutoIncreaseId(), carIds);
        }
        if (sameDayList != null && sameDayList.size() > 0) {
            totalList.addAll(sameDayList);
        }
        if (nonSameDayList != null && nonSameDayList.size() > 0) {
            totalList.addAll(nonSameDayList);
        }
        return result.setData(statisticsDetailedOilList(totalList, command));
    }

    @Override
    public HttpCommandResultWithData carReportDrivingChart(StatisticsCarReportChartForm command) throws Exception {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        // 查询时间校验
        String checkResult = checkQueryTime(command.getBeginDate(), command.getEndDate());
        if (StringUtil.isNotEmpty(checkResult)) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage(checkResult);
            return result;
        }
        List<FltStatisticalPojo> totalList = new ArrayList<>();

        List<FltStatisticalPojo> sameDayList = null;
        List<FltStatisticalPojo> nonSameDayList = null;

        // 查询作为车主的车辆
        List<String> carIds = fltCarOwnerMappingMapper.queryOwnerCars(command.getAutoIncreaseId());

        // 包含当天日期
        if (DateUtil.diffNowDate(command.getEndDate()) == 3) {
            sameDayList = queryStatisticalOfMilAndFuelFromLocationCloud(command.getAutoIncreaseId(), carIds);
        }
        // 非当天日期
        if (DateUtil.diffNowDate(command.getBeginDate()) == 2) {
            nonSameDayList = queryStatisticalByFeetId(DateUtil.dateFormatConversion(command.getBeginDate(), DateUtil.date_s_pattern), DateUtil.dateFormatConversion(command.getEndDate(), DateUtil.date_s_pattern), command.getAutoIncreaseId(), carIds);
        }
        if (sameDayList != null && sameDayList.size() > 0) {
            totalList.addAll(sameDayList);
        }
        if (nonSameDayList != null && nonSameDayList.size() > 0) {
            totalList.addAll(nonSameDayList);
        }

        return result.setData(statisticsDrivingChart(totalList, command));
    }

    @Override
    public HttpCommandResultWithData carReportIdlTime(StatisticsCarReportIdlOilForm command) throws Exception {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        // 查询时间校验
        String checkResult = checkQueryTime(command.getBeginDate(), command.getEndDate());
        if (StringUtil.isNotEmpty(checkResult)) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage(checkResult);
            return result;
        }
        List<FltStatisticalPojo> totalList = new ArrayList<>();

        List<FltStatisticalPojo> sameDayList = null;
        List<FltStatisticalPojo> nonSameDayList = null;

        // 查询作为车主的车辆
        List<String> carIds = fltCarOwnerMappingMapper.queryOwnerCars(command.getAutoIncreaseId());

        // 包含当天日期
        if (DateUtil.diffNowDate(command.getEndDate()) == 3) {
            sameDayList = queryStatisticalOfMilAndFuelFromLocationCloud(command.getAutoIncreaseId(), carIds);
        }
        // 非当天日期
        if (DateUtil.diffNowDate(command.getBeginDate()) == 2) {
            nonSameDayList = queryStatisticalByFeetId(DateUtil.dateFormatConversion(command.getBeginDate(), DateUtil.date_s_pattern), DateUtil.dateFormatConversion(command.getEndDate(), DateUtil.date_s_pattern), command.getAutoIncreaseId(), carIds);
        }
        if (sameDayList != null && sameDayList.size() > 0) {
            totalList.addAll(sameDayList);
        }
        if (nonSameDayList != null && nonSameDayList.size() > 0) {
            totalList.addAll(nonSameDayList);
        }
        return result.setData(statisticsIdlDriving(totalList));
    }

    @Override
    public HttpCommandResultWithData carReportCarList(StatisticsOilCarListForm command) throws Exception {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        // 查询时间校验
        String checkResult = checkQueryTime(command.getBeginDate(), command.getEndDate());
        if (StringUtil.isNotEmpty(checkResult)) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage(checkResult);
            return result;
        }
        List<FltStatisticalPojo> totalList = new ArrayList<>();

        List<FltStatisticalPojo> sameDayList = null;
        List<FltStatisticalPojo> nonSameDayList = null;

        // 查询作为车主的车辆
        List<String> carIds = fltCarOwnerMappingMapper.queryOwnerCars(command.getAutoIncreaseId());

        // 包含当天日期
        if (DateUtil.diffNowDate(command.getEndDate()) == 3) {
            sameDayList = queryStatisticalOfMilAndFuelFromLocationCloud(command.getAutoIncreaseId(), carIds);
        }
        // 非当天日期
        if (DateUtil.diffNowDate(command.getBeginDate()) == 2) {
            nonSameDayList = queryStatisticalByFeetId(DateUtil.dateFormatConversion(command.getBeginDate(), DateUtil.date_s_pattern), DateUtil.dateFormatConversion(command.getEndDate(), DateUtil.date_s_pattern), command.getAutoIncreaseId(), carIds);
        }
        if (sameDayList != null && sameDayList.size() > 0) {
            totalList.addAll(sameDayList);
        }
        if (nonSameDayList != null && nonSameDayList.size() > 0) {
            totalList.addAll(nonSameDayList);
        }
        return result.setData(statisticsDrivingCarList(totalList, command));
    }

    @Override
    public HttpCommandResultWithData carReportDetailedDrivingList(StatisticsMileageCarDetailForm command) throws Exception {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        // 查询时间校验
        String checkResult = checkQueryTime(command.getBeginDate(), command.getEndDate());
        if (StringUtil.isNotEmpty(checkResult)) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage(checkResult);
            return result;
        }
        List<FltStatisticalPojo> totalList = new ArrayList<>();

        List<FltStatisticalPojo> sameDayList = null;
        List<FltStatisticalPojo> nonSameDayList = null;

        // 查询作为车主的车辆
        List<String> carIds = fltCarOwnerMappingMapper.queryOwnerCars(command.getAutoIncreaseId());

        // 包含当天日期
        if (DateUtil.diffNowDate(command.getEndDate()) == 3) {
            sameDayList = queryStatisticalOfMilAndFuelFromLocationCloud(command.getAutoIncreaseId(), carIds);
        }
        // 非当天日期
        if (DateUtil.diffNowDate(command.getBeginDate()) == 2) {
            nonSameDayList = queryStatisticalByFeetId(DateUtil.dateFormatConversion(command.getBeginDate(), DateUtil.date_s_pattern), DateUtil.dateFormatConversion(command.getEndDate(), DateUtil.date_s_pattern), command.getAutoIncreaseId(), carIds);
        }
        if (sameDayList != null && sameDayList.size() > 0) {
            totalList.addAll(sameDayList);
        }
        if (nonSameDayList != null && nonSameDayList.size() > 0) {
            totalList.addAll(nonSameDayList);
        }

        return result.setData(statisticsDetailedDrivingList(totalList, command));
    }

    @Override
    public HttpCommandResultWithData badDrivingChartForPie(StatisticsCarReportChartForm command) throws Exception {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        // 查询时间校验
        String checkResult = checkQueryTime(command.getBeginDate(), command.getEndDate());
        if (StringUtil.isNotEmpty(checkResult)) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage(checkResult);
            return result;
        }
        List<FltStatisticalPojo> totalList = new ArrayList<>();

        List<FltStatisticalPojo> sameDayList = null;
        List<FltStatisticalPojo> nonSameDayList = null;

        // 查询作为车主的车辆
        List<String> carIds = fltCarOwnerMappingMapper.queryOwnerCars(command.getAutoIncreaseId());

        // 包含当天日期
        if (DateUtil.diffNowDate(command.getEndDate()) == 3) {
            sameDayList = queryStatisticalOfMilAndFuelFromLocationCloud(command.getAutoIncreaseId(), carIds);
        }
        // 非当天日期
        if (DateUtil.diffNowDate(command.getBeginDate()) == 2) {
            nonSameDayList = queryStatisticalByFeetId(DateUtil.dateFormatConversion(command.getBeginDate(), DateUtil.date_s_pattern), DateUtil.dateFormatConversion(command.getEndDate(), DateUtil.date_s_pattern), command.getAutoIncreaseId(), carIds);
        }
        if (sameDayList != null && sameDayList.size() > 0) {
            totalList.addAll(sameDayList);
        }
        if (nonSameDayList != null && nonSameDayList.size() > 0) {
            totalList.addAll(nonSameDayList);
        }

        return result.setData(statisticsBadDrivingChart(totalList, command));
    }

    @Override
    public HttpCommandResultWithData badDrivingCountByBehavior(StatisticsCarReportForm command) throws Exception {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        // 查询时间校验
        String checkResult = checkQueryTime(command.getBeginDate(), command.getEndDate());
        if (StringUtil.isNotEmpty(checkResult)) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage(checkResult);
            return result;
        }
        List<FltStatisticalPojo> totalList = new ArrayList<>();

        List<FltStatisticalPojo> sameDayList = null;
        List<FltStatisticalPojo> nonSameDayList = null;

        // 查询作为车主的车辆
        List<String> carIds = fltCarOwnerMappingMapper.queryOwnerCars(command.getAutoIncreaseId());

        // 包含当天日期
        if (DateUtil.diffNowDate(command.getEndDate()) == 3) {
            sameDayList = queryStatisticalOfMilAndFuelFromLocationCloud(command.getAutoIncreaseId(), carIds);
        }
        // 非当天日期
        if (DateUtil.diffNowDate(command.getBeginDate()) == 2) {
            nonSameDayList = queryStatisticalByFeetId(DateUtil.dateFormatConversion(command.getBeginDate(), DateUtil.date_s_pattern), DateUtil.dateFormatConversion(command.getEndDate(), DateUtil.date_s_pattern), command.getAutoIncreaseId(), carIds);
        }
        if (sameDayList != null && sameDayList.size() > 0) {
            totalList.addAll(sameDayList);
        }
        if (nonSameDayList != null && nonSameDayList.size() > 0) {
            totalList.addAll(nonSameDayList);
        }
        return result.setData(statisticsBadDrivingByBehavior(totalList));
    }

    @Override
    public HttpCommandResultWithData badDrivingCountByCar(BadDrivingCountByCarForm command) throws Exception {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        // 查询时间校验
        String checkResult = checkQueryTime(command.getBeginDate(), command.getEndDate());
        if (StringUtil.isNotEmpty(checkResult)) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage(checkResult);
            return result;
        }
        List<FltStatisticalPojo> totalList = new ArrayList<>();

        List<FltStatisticalPojo> sameDayList = null;
        List<FltStatisticalPojo> nonSameDayList = null;

        // 查询作为车主的车辆
        List<String> carIds = fltCarOwnerMappingMapper.queryOwnerCars(command.getAutoIncreaseId());

        // 包含当天日期
        if (DateUtil.diffNowDate(command.getEndDate()) == 3) {
            sameDayList = queryStatisticalOfMilAndFuelFromLocationCloud(command.getAutoIncreaseId(), carIds);
        }
        // 非当天日期
        if (DateUtil.diffNowDate(command.getBeginDate()) == 2) {
            nonSameDayList = queryStatisticalByFeetId(DateUtil.dateFormatConversion(command.getBeginDate(), DateUtil.date_s_pattern), DateUtil.dateFormatConversion(command.getEndDate(), DateUtil.date_s_pattern), command.getAutoIncreaseId(), carIds);
        }
        if (sameDayList != null && sameDayList.size() > 0) {
            totalList.addAll(sameDayList);
        }
        if (nonSameDayList != null && nonSameDayList.size() > 0) {
            totalList.addAll(nonSameDayList);
        }
        return result.setData(statisticsBadDrivingByCar(totalList, command));
    }

    @Override
    public HttpCommandResultWithData badDrivingCountByCarDetailChart(StatisticsMileageCarDetailForm command) throws Exception {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        // 查询时间校验
        String checkResult = checkQueryTime(command.getBeginDate(), command.getEndDate());
        if (StringUtil.isNotEmpty(checkResult)) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage(checkResult);
            return result;
        }
        List<FltStatisticalPojo> totalList = new ArrayList<>();

        List<FltStatisticalPojo> sameDayList = null;
        List<FltStatisticalPojo> nonSameDayList = null;

        // 查询作为车主的车辆
        List<String> carIds = fltCarOwnerMappingMapper.queryOwnerCars(command.getAutoIncreaseId());

        // 包含当天日期
        if (DateUtil.diffNowDate(command.getEndDate()) == 3) {
            sameDayList = queryStatisticalOfMilAndFuelFromLocationCloud(command.getAutoIncreaseId(), carIds);
        }
        // 非当天日期
        if (DateUtil.diffNowDate(command.getBeginDate()) == 2) {
            nonSameDayList = queryStatisticalByFeetId(DateUtil.dateFormatConversion(command.getBeginDate(), DateUtil.date_s_pattern), DateUtil.dateFormatConversion(command.getEndDate(), DateUtil.date_s_pattern), command.getAutoIncreaseId(), carIds);
        }
        if (sameDayList != null && sameDayList.size() > 0) {
            totalList.addAll(sameDayList);
        }
        if (nonSameDayList != null && nonSameDayList.size() > 0) {
            totalList.addAll(nonSameDayList);
        }
        return result.setData(statisticsBadDrivingDetailByCar(totalList, command));
    }

    /**
     * 按线路统计
     * @param form
     * @return
     */
    @Override
    public CarReportDTO carLineReport(StatisticsCarLinReportForm form) {
        List<FltStatisticalPojo> totalList = new ArrayList<>();
        List<FltStatisticalPojo> sameDayList = null;
        List<FltStatisticalPojo> nonSameDayList = null;
        List<String> carIds = new ArrayList<>();
        //根据车辆id线路的所有车辆
        if (StringUtils.isNotBlank(form.getCarids())){
            Collections.addAll(carIds,form.getCarids().split(","));
        }
        //根据车队id当前线路下所有车辆
        if (StringUtils.isNotBlank(form.getFleetId())){
           //查询数据库中和当前车队有关的车辆
            String[] fleetArr = form.getFleetId().split(",");
            for (String fleetId : fleetArr) {
                List<FltFleetCarMappingEntity> fltFleetCarMappingEntities = fltFleetCarMappingDao.selectByTeamId(Long.parseLong(fleetId));
                List<String> carIdList = fltFleetCarMappingEntities.stream().map(FltFleetCarMappingEntity::getCarId).collect(Collectors.toList());
                carIds.addAll(carIdList);
            }
        }

        // 包含当天日期
        if (DateUtil.diffNowDate(form.getEndDate()) == 3) {
            sameDayList = queryStatisticalOfMilAndFuelFromLocationCloud(form.getAutoIncreaseId(), carIds);
        }
        // 非当天日期
        if (DateUtil.diffNowDate(form.getBeginDate()) == 2) {
            try {
                nonSameDayList = queryStatisticalByFeetId(DateUtil.dateFormatConversion(form.getBeginDate(), DateUtil.date_s_pattern), DateUtil.dateFormatConversion(form.getEndDate(), DateUtil.date_s_pattern), form.getAutoIncreaseId(), carIds);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (sameDayList != null && sameDayList.size() > 0) {
            totalList.addAll(sameDayList);
        }
        if (nonSameDayList != null && nonSameDayList.size() > 0) {
            totalList.addAll(nonSameDayList);
        }

        // 数据统计时间范围
        long dateInterval = DateUtil.diffByDay(form.getBeginDate(), form.getEndDate(), "") + 1;
        try {
            log.info("统计集合:{}", JsonUtil.toJson(totalList));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        // 统计
        CarReportDTO carReportDTO = statisticsCarReport(totalList, dateInterval);
        log.info("carReport end return:{}", carReportDTO);
        return carReportDTO;
    }

    @SneakyThrows
    @Override
    public HttpCommandResultWithData carReportLinMileageCarList(StatisticsLineMileageCarListForm form) {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        // 查询时间校验
        String checkResult = checkQueryTime(form.getBeginDate(), form.getEndDate());
        if (StringUtil.isNotEmpty(checkResult)) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage(checkResult);
            return result;
        }
        List<FltStatisticalPojo> totalList = new ArrayList<>();

        List<FltStatisticalPojo> sameDayList = null;
        List<FltStatisticalPojo> nonSameDayList = null;
        List<String> carIds = new ArrayList<>();
        if (StringUtils.isNotBlank(form.getCarIds())){
            carIds = Arrays.stream(form.getCarIds().split(",")).collect(Collectors.toList());
        }
        //根据车队id查询当前车队下所有车辆
        if (StringUtils.isNotBlank(form.getFleetId())){
            //查询数据库中和当前车队有关的车辆
            String[] fleetArr = form.getFleetId().split(",");
            for (String fleetId : fleetArr) {
                List<FltFleetCarMappingEntity> fltFleetCarMappingEntities = fltFleetCarMappingDao.selectByTeamId(Long.parseLong(fleetId));
                List<String> carIdList = fltFleetCarMappingEntities.stream().map(FltFleetCarMappingEntity::getCarId).collect(Collectors.toList());
                carIds.addAll(carIdList);
            }
        }

        // 包含当天日期
        if (DateUtil.diffNowDate(form.getEndDate()) == 3) {
            sameDayList = queryStatisticalOfMilAndFuelFromLocationCloud(carIds);
        }
        // 非当天日期
        if (DateUtil.diffNowDate(form.getBeginDate()) == 2) {
            nonSameDayList = queryStatisticalByFeetId(DateUtil.dateFormatConversion(form.getBeginDate(), DateUtil.date_s_pattern), DateUtil.dateFormatConversion(form.getEndDate(), DateUtil.date_s_pattern), carIds);
        }
        if (sameDayList != null && sameDayList.size() > 0) {
            totalList.addAll(sameDayList);
        }
        if (nonSameDayList != null && nonSameDayList.size() > 0) {
            totalList.addAll(nonSameDayList);
        }
        StatisticsMileageCarListForm listForm = new StatisticsMileageCarListForm();
        listForm.setKeyWord(form.getKeyWord());
        listForm.setReturnAll(form.getReturnAll());
        listForm.setPage_number(form.getPage_number());
        listForm.setPage_size(form.getPage_size());
        return result.setData(statisticsMileageCarList(totalList, listForm));
    }

    /**
     * 车辆报表-不良驾驶行为-单车不良驾驶行为统计
     *
     * @param list
     * @param command
     * @return
     */
    private List<CarReportBadDrivingCarDeTailDTO> statisticsBadDrivingDetailByCar(List<FltStatisticalPojo> list, StatisticsMileageCarDetailForm command) {
        List<CarReportBadDrivingCarDeTailDTO> carReportBadDrivingCarDeTailDtos = new ArrayList<>();

        if (StringUtil.isNotEmpty(command.getCarId())) {
            list = list.stream().filter(fsp -> StringUtil.isEq(command.getCarId(), fsp.getCarId())).collect(Collectors.toList());
        }
        if (list.size() == 0) {
            CarReportBadDrivingCarDeTailDTO carReportBadDrivingCarDeTailDto;
            for (String s : BAD_BEHAVIOR_NAME) {
                carReportBadDrivingCarDeTailDto = new CarReportBadDrivingCarDeTailDTO();
                carReportBadDrivingCarDeTailDto.setBadName(s);
                carReportBadDrivingCarDeTailDto.setBadTotalNumber(0);
                carReportBadDrivingCarDeTailDto.setBadProportion(0);
                carReportBadDrivingCarDeTailDtos.add(carReportBadDrivingCarDeTailDto);
            }
            return carReportBadDrivingCarDeTailDtos;
        }

        // 各不良驾驶行为次数，顺序与BAD_BEHAVIOR_NAME值一一对应，不可错乱
        Integer[] num = badDrivingNumber(list);

        // 不良驾驶行为总数
        int totalBadNumber = 0;
        int size = num.length;
        for (int i = 0; i < size; i++) {
            totalBadNumber += num[i];
        }

        CarReportBadDrivingCarDeTailDTO carReportBadDrivingCarDeTailDto;
        for (int i = 0; i < size; i++) {
            carReportBadDrivingCarDeTailDto = new CarReportBadDrivingCarDeTailDTO();
            carReportBadDrivingCarDeTailDto.setBadName(BAD_BEHAVIOR_NAME[i]);
            carReportBadDrivingCarDeTailDto.setBadTotalNumber(num[i]);
            if (totalBadNumber == 0) {
                carReportBadDrivingCarDeTailDto.setBadProportion(0);
            } else {
                carReportBadDrivingCarDeTailDto.setBadProportion(num[i] / totalBadNumber * 100);
            }
            carReportBadDrivingCarDeTailDtos.add(carReportBadDrivingCarDeTailDto);
        }
        return carReportBadDrivingCarDeTailDtos;

    }

    /**
     * 车辆报表-不良驾驶行为-按车辆统计
     *
     * @param list
     * @return
     */
    private PagingInfo<CarReportBadDrivingByCarDTO> statisticsBadDrivingByCar(List<FltStatisticalPojo> list, BadDrivingCountByCarForm command) {

        List<CarReportBadDrivingByCarDTO> carReportBadDrivingByCarDtos = new ArrayList<>();

        if (StringUtil.isNotEmpty(command.getKeyWord())) {
            list = list.stream().filter(fsp -> fsp.getCarNum().contains(command.getKeyWord())).collect(Collectors.toList());
        }
        if (list.size() == 0) {
            return null;
        }
        // 各不良驾驶行为次数，顺序与BAD_BEHAVIOR_NAME值一一对应，不可错乱
        Integer[] num = badDrivingNumber(list);
        // 不良驾驶行为总数
        int totalBadNumber = 0;
        int size = num.length;
        for (int i = 0; i < size; i++) {
            totalBadNumber += num[i];
        }
        // 提取车牌号
        Map<String, String> carMap = list.stream().collect(Collectors.toMap(FltStatisticalPojo::getCarId, FltStatisticalPojo::getCarNum, (a, b) -> (b)));

        // 按车分组数据
        Map<String, List<FltStatisticalPojo>> map = list.stream().collect(Collectors.groupingBy(FltStatisticalPojo::getCarId));
        CarReportBadDrivingByCarDTO carReportBadDrivingByCarDto;
        for (Map.Entry<String, List<FltStatisticalPojo>> entry : map.entrySet()) {
            carReportBadDrivingByCarDto = new CarReportBadDrivingByCarDTO();
            carReportBadDrivingByCarDto.setCarId(entry.getKey());
            carReportBadDrivingByCarDto.setCarNumber(carMap.get(entry.getKey()));

            // 计算此车的不良驾驶次数和
            int sum = 0;
            for (FltStatisticalPojo fsp : entry.getValue()) {
                sum += fsp.getOverSpeedCnt() + fsp.getRaCnt() + fsp.getRdCnt() + fsp.getSharpTurnCnt() + fsp.getIdleTimeoutCnt()
                        + fsp.getColdRunCnt() + fsp.getNightRunCnt() + fsp.getLowGrHighSpdCnt() + fsp.getFullThrottleCnt()
                        + fsp.getRoughThrottleCnt() + fsp.getNeutralGearCoastCnt() + fsp.getStallCoastCnt();


            }
            carReportBadDrivingByCarDto.setBadNumber(sum);
            if (totalBadNumber == 0) {
                carReportBadDrivingByCarDto.setBadProportion(new BigDecimal(0.0));
            } else {
                carReportBadDrivingByCarDto.setBadProportion(new BigDecimal(sum * 100).divide(new BigDecimal(totalBadNumber), 1, BigDecimal.ROUND_HALF_UP));
            }
            carReportBadDrivingByCarDtos.add(carReportBadDrivingByCarDto);
        }
        // 排序
        String sortType = command.getSort();
        if (StringUtil.isEmpty(sortType)) {
            sortType = "3";
        }
        switch (sortType) {
            // 从低到高
            case "0":
                carReportBadDrivingByCarDtos = carReportBadDrivingByCarDtos.stream().sorted(Comparator.comparing(CarReportBadDrivingByCarDTO::getBadNumber))
                        .collect(Collectors.toList());
                break;
            // 从高到低
            case "1":
                carReportBadDrivingByCarDtos = carReportBadDrivingByCarDtos.stream().sorted(Comparator.comparing(CarReportBadDrivingByCarDTO::getBadNumber).reversed())
                        .collect(Collectors.toList());
                break;
            // 默认
            default:
                break;
        }
        PagingInfo<CarReportBadDrivingByCarDTO> pagingInfo = new PagingInfo<>();
        // 分页
        if (StringUtil.isNotEq("1", command.getReturnAll())) {
            // 分页
            carReportBadDrivingByCarDtos = pageUtil.paging(carReportBadDrivingByCarDtos, command.getPage_number(), command.getPage_size(), pagingInfo);
            pagingInfo.setList(carReportBadDrivingByCarDtos);
        } else {
            pagingInfo.setList(carReportBadDrivingByCarDtos);
            pagingInfo.setTotal(carReportBadDrivingByCarDtos.size());
            pagingInfo.setPage_total(1);
        }
        return pagingInfo;
    }

    /**
     * 车辆报表-不良驾驶行为-按行为统计
     *
     * @param list
     * @return
     */
    private BadDrivingCountByBehaviorDTO statisticsBadDrivingByBehavior(List<FltStatisticalPojo> list) {
        BadDrivingCountByBehaviorDTO badDrivingCountByBehaviorDto = new BadDrivingCountByBehaviorDTO();
        if (list.size() == 0) {
            badDrivingCountByBehaviorDto.setTotalNumber(0);
            List<BadDrivingCountByBehaviorListDTO> badList = new ArrayList<>();
            BadDrivingCountByBehaviorListDTO b;
            for (String s : BAD_BEHAVIOR_NAME) {
                b = new BadDrivingCountByBehaviorListDTO();
                b.setBadName(s);
                b.setBadNumber(0);
                b.setBadProportion(0);
                badList.add(b);
            }
            return badDrivingCountByBehaviorDto;
        }
        // 各不良驾驶行为次数，顺序与BAD_BEHAVIOR_NAME值一一对应，不可错乱
        Integer[] num = badDrivingNumber(list);
        // 不良驾驶行为总数
        int totalBadNumber = 0;
        int size = num.length;
        for (int i = 0; i < size; i++) {
            totalBadNumber += num[i];
        }

        List<BadDrivingCountByBehaviorListDTO> totalBadByBehaviorList = new ArrayList<>();
        BadDrivingCountByBehaviorListDTO badDrivingCountByBehaviorListDto;
        // 提取车牌号
        Map<String, String> carMap = list.stream().collect(Collectors.toMap(FltStatisticalPojo::getCarId, FltStatisticalPojo::getCarNum, (a, b) -> (b)));

        if (totalBadNumber == 0) {
            badDrivingCountByBehaviorDto.setTotalNumber(0);
            for (int i = 0; i < size; i++) {
                badDrivingCountByBehaviorListDto = new BadDrivingCountByBehaviorListDTO();
                badDrivingCountByBehaviorListDto.setBadName(BAD_BEHAVIOR_NAME[i]);
                badDrivingCountByBehaviorListDto.setBadNumber(0);
                badDrivingCountByBehaviorListDto.setBadProportion(0);
                totalBadByBehaviorList.add(badDrivingCountByBehaviorListDto);
            }
            return badDrivingCountByBehaviorDto;
        } else {
            badDrivingCountByBehaviorDto.setTotalNumber(totalBadNumber);
            for (int i = 0; i < size; i++) {
                badDrivingCountByBehaviorListDto = new BadDrivingCountByBehaviorListDTO();
                badDrivingCountByBehaviorListDto.setBadName(BAD_BEHAVIOR_NAME[i]);
                badDrivingCountByBehaviorListDto.setBadNumber(num[i]);
                badDrivingCountByBehaviorListDto.setBadProportion(num[i] * 100 / totalBadNumber);
                // 按车分组数据。并统计每台车的各个不良驾驶行为总数
                Map<String, Integer> bad = new HashMap<>();
                if (i == 0) {
                    bad = list.stream().collect(Collectors.toMap(FltStatisticalPojo::getCarId, FltStatisticalPojo::getOverSpeedCnt, (a, b) -> (a + b))).entrySet()
                            .stream()
                            .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                            .collect(
                                    toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                            LinkedHashMap::new));
                }
                if (i == 1) {
                    bad = list.stream().collect(Collectors.toMap(FltStatisticalPojo::getCarId, FltStatisticalPojo::getRaCnt, (a, b) -> (a + b))).entrySet()
                            .stream()
                            .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                            .collect(
                                    toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                            LinkedHashMap::new));
                }

                if (i == 2) {
                    bad = list.stream().collect(Collectors.toMap(FltStatisticalPojo::getCarId, FltStatisticalPojo::getRdCnt, (a, b) -> (a + b))).entrySet()
                            .stream()
                            .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                            .collect(
                                    toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                            LinkedHashMap::new));
                }
                if (i == 3) {
                    bad = list.stream().collect(Collectors.toMap(FltStatisticalPojo::getCarId, FltStatisticalPojo::getSharpTurnCnt, (a, b) -> (a + b))).entrySet()
                            .stream()
                            .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                            .collect(
                                    toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                            LinkedHashMap::new));
                }
                if (i == 4) {
                    bad = list.stream().collect(Collectors.toMap(FltStatisticalPojo::getCarId, FltStatisticalPojo::getIdleTimeoutCnt, (a, b) -> (a + b))).entrySet()
                            .stream()
                            .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                            .collect(
                                    toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                            LinkedHashMap::new));
                }
                if (i == 5) {
                    bad = list.stream().collect(Collectors.toMap(FltStatisticalPojo::getCarId, FltStatisticalPojo::getColdRunCnt, (a, b) -> (a + b))).entrySet()
                            .stream()
                            .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                            .collect(
                                    toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                            LinkedHashMap::new));
                }
                if (i == 6) {
                    bad = list.stream().collect(Collectors.toMap(FltStatisticalPojo::getCarId, FltStatisticalPojo::getNightRunCnt, (a, b) -> (a + b))).entrySet()
                            .stream()
                            .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                            .collect(
                                    toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                            LinkedHashMap::new));
                }
                if (i == 7) {
                    bad = list.stream().collect(Collectors.toMap(FltStatisticalPojo::getCarId, FltStatisticalPojo::getLowGrHighSpdCnt, (a, b) -> (a + b))).entrySet()
                            .stream()
                            .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                            .collect(
                                    toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                            LinkedHashMap::new));
                }
                if (i == 8) {
                    bad = list.stream().collect(Collectors.toMap(FltStatisticalPojo::getCarId, FltStatisticalPojo::getFullThrottleCnt, (a, b) -> (a + b))).entrySet()
                            .stream()
                            .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                            .collect(
                                    toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                            LinkedHashMap::new));
                }
                if (i == 9) {
                    bad = list.stream().collect(Collectors.toMap(FltStatisticalPojo::getCarId, FltStatisticalPojo::getRoughThrottleCnt, (a, b) -> (a + b))).entrySet()
                            .stream()
                            .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                            .collect(
                                    toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                            LinkedHashMap::new));
                }
                if (i == 10) {
                    bad = list.stream().collect(Collectors.toMap(FltStatisticalPojo::getCarId, FltStatisticalPojo::getNeutralGearCoastCnt, (a, b) -> (a + b))).entrySet()
                            .stream()
                            .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                            .collect(
                                    toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                            LinkedHashMap::new));
                }
                if (i == 11) {
                    bad = list.stream().collect(Collectors.toMap(FltStatisticalPojo::getCarId, FltStatisticalPojo::getStallCoastCnt, (a, b) -> (a + b))).entrySet()
                            .stream()
                            .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                            .collect(
                                    toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                            LinkedHashMap::new));
                }

                int k = 0;
                List<BadDrivingCountInfoByBehaviorDTO> topTen = new ArrayList<>();
                BadDrivingCountInfoByBehaviorDTO badDrivingCountInfoByBehaviorDto;
                for (Map.Entry<String, Integer> entry : bad.entrySet()) {
                    if (k > 9) {
                        break;
                    }
                    badDrivingCountInfoByBehaviorDto = new BadDrivingCountInfoByBehaviorDTO();
                    badDrivingCountInfoByBehaviorDto.setCarNumber(carMap.get(entry.getKey()));
                    badDrivingCountInfoByBehaviorDto.setBehaviorNumber(entry.getValue());
                    if (num[i] == 0) {
                        badDrivingCountInfoByBehaviorDto.setBehaviorProportion(0);
                    } else {
                        badDrivingCountInfoByBehaviorDto.setBehaviorProportion(entry.getValue() * 100 / num[i]);
                    }
                    topTen.add(badDrivingCountInfoByBehaviorDto);
                    k += 1;
                }
                badDrivingCountByBehaviorListDto.setTopTen(topTen);

                totalBadByBehaviorList.add(badDrivingCountByBehaviorListDto);
            }
        }
        badDrivingCountByBehaviorDto.setList(totalBadByBehaviorList);
        return badDrivingCountByBehaviorDto;
    }

    /**
     * 车辆报表-不良驾驶行为图表统计
     *
     * @param list
     * @param command
     * @return
     */
    private CarReportBadDrivingChartDTO statisticsBadDrivingChart(List<FltStatisticalPojo> list, StatisticsCarReportChartForm command) {
        CarReportBadDrivingChartDTO carReportBadDrivingChartDto = new CarReportBadDrivingChartDTO();

        if (StringUtil.isNotEmpty(command.getCarId())) {
            list = list.stream().filter(fsp -> StringUtil.isEq(command.getCarId(), fsp.getCarId())).collect(Collectors.toList());
        }
        // 各不良驾驶行为次数，顺序与BAD_BEHAVIOR_NAME值一一对应，不可错乱
        Integer[] num = badDrivingNumber(list);

        List<BadDrivingChartPieDTO> badList = new ArrayList<>();
        BadDrivingChartPieDTO badDrivingChartPieDto;
        for (int i = 0; i < num.length; i++) {
            badDrivingChartPieDto = new BadDrivingChartPieDTO();
            badDrivingChartPieDto.setName(BAD_BEHAVIOR_NAME[i]);
            badDrivingChartPieDto.setValue(num[i]);
            badList.add(badDrivingChartPieDto);
        }
        carReportBadDrivingChartDto.setNameList(BAD_BEHAVIOR_NAME);
        carReportBadDrivingChartDto.setList(badList);

        return carReportBadDrivingChartDto;
    }

    /**
     * 不良驾驶行为数量统计
     *
     * @param list 计算前数据集合
     * @return 统计后结果
     */
    private Integer[] badDrivingNumber(List<FltStatisticalPojo> list) {
        Integer[] num = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        for (FltStatisticalPojo fltStatisticalPojo : list) {
            num[0] += fltStatisticalPojo.getOverSpeedCnt();
            num[1] += fltStatisticalPojo.getRaCnt();
            num[2] += fltStatisticalPojo.getRdCnt();
            num[3] += fltStatisticalPojo.getSharpTurnCnt();
            num[4] += fltStatisticalPojo.getIdleTimeoutCnt();
            num[5] += fltStatisticalPojo.getColdRunCnt();
            num[6] += fltStatisticalPojo.getNightRunCnt();
            num[7] += fltStatisticalPojo.getLowGrHighSpdCnt();
            num[8] += fltStatisticalPojo.getFullThrottleCnt();
            num[9] += fltStatisticalPojo.getRoughThrottleCnt();
            num[10] += fltStatisticalPojo.getNeutralGearCoastCnt();
            num[11] += fltStatisticalPojo.getStallCoastCnt();
        }
        return num;
    }

    /**
     * 车辆报表-车辆行驶时长明细列表
     *
     * @param list
     * @param command
     * @return
     */
    private List<CarReportDrivingCarDeTailDTO> statisticsDetailedDrivingList(List<FltStatisticalPojo> list, StatisticsMileageCarDetailForm command) {

        if (StringUtil.isNotEmpty(command.getCarId())) {
            list = list.stream().filter(fsp -> StringUtil.isEq(command.getCarId(), fsp.getCarId())).collect(Collectors.toList());
        }
        if (list.size() == 0) {
            return null;
        }

        List<CarReportDrivingCarDeTailDTO> returnList = new ArrayList<>();
        String[] dates = DateUtil.getDateArrayBetween(command.getBeginDate(), command.getEndDate());

        Map<String, LongSummaryStatistics> mapTimeTotal = list.stream().collect(Collectors.groupingBy(FltStatisticalPojo::getStatisDate, Collectors.summarizingLong(FltStatisticalPojo::getTimeTotal)));
        Map<String, LongSummaryStatistics> mapIdlTime = list.stream().collect(Collectors.groupingBy(FltStatisticalPojo::getStatisDate, Collectors.summarizingLong(FltStatisticalPojo::getIdleTime)));

        CarReportDrivingCarDeTailDTO carReportDrivingCarDeTailDto;
        for (String s : dates) {
            carReportDrivingCarDeTailDto = new CarReportDrivingCarDeTailDTO();
            LongSummaryStatistics longSummaryStatistics1 = mapTimeTotal.get(s);
            LongSummaryStatistics longSummaryStatistics2 = mapIdlTime.get(s);
            carReportDrivingCarDeTailDto.setData(timeCon(s));

            if (longSummaryStatistics1 == null) {
                carReportDrivingCarDeTailDto.setTotalTime("0.0");
                carReportDrivingCarDeTailDto.setIdlTime("0.0");
                carReportDrivingCarDeTailDto.setDrivingTime("0.0");
            } else {
                BigDecimal bt = new BigDecimal(longSummaryStatistics1.getSum()).divide(new BigDecimal(3600000.0), 1, BigDecimal.ROUND_HALF_UP);
                BigDecimal bi = new BigDecimal(longSummaryStatistics2.getSum()).divide(new BigDecimal(3600000.0), 1, BigDecimal.ROUND_HALF_UP);
                BigDecimal bd = bt.subtract(bi);
                carReportDrivingCarDeTailDto.setTotalTime(bt.toString());
                carReportDrivingCarDeTailDto.setIdlTime(bi.toString());
                carReportDrivingCarDeTailDto.setDrivingTime(bd.toString());
            }
            returnList.add(carReportDrivingCarDeTailDto);
        }
        return returnList;
    }

    /**
     * 车辆报表-车辆行驶时长列表
     *
     * @param list
     * @param command
     * @return
     */
    private PagingInfo<CarReportDrivingCarListDTO> statisticsDrivingCarList(List<FltStatisticalPojo> list, StatisticsOilCarListForm command) {
        List<CarReportDrivingCarListDTO> carReportDrivingCarListDtos = new ArrayList<>();

        if (StringUtil.isNotEmpty(command.getKeyWord())) {
            list = list.stream().filter(fsp -> fsp.getCarNum().contains(command.getKeyWord())).collect(Collectors.toList());
        }
        if (list.size() == 0) {
            return null;
        }
        // 按车分组数据
        Map<String, List<FltStatisticalPojo>> map = list.stream().collect(Collectors.groupingBy(FltStatisticalPojo::getCarId));
        CarReportDrivingCarListDTO carReportDrivingCarListDto;
        for (Map.Entry<String, List<FltStatisticalPojo>> entry : map.entrySet()) {
            carReportDrivingCarListDto = new CarReportDrivingCarListDTO();
            carReportDrivingCarListDto.setCarId(entry.getKey());
            List<FltStatisticalPojo> carDataList = entry.getValue();
            carReportDrivingCarListDto.setCarNumber(carDataList.get(0).getCarNum());
            // 统计时长
            BigDecimal bt = new BigDecimal(carDataList.stream().mapToLong(FltStatisticalPojo::getTimeTotal).sum()).divide(new BigDecimal(3600000.0), 1, BigDecimal.ROUND_HALF_UP);
            BigDecimal bi = new BigDecimal(carDataList.stream().mapToLong(FltStatisticalPojo::getIdleTime).sum()).divide(new BigDecimal(3600000.0), 1, BigDecimal.ROUND_HALF_UP);
            BigDecimal bd = bt.subtract(bi);
            carReportDrivingCarListDto.setTotalTime(bt.doubleValue());
            carReportDrivingCarListDto.setIdlTime(bi.doubleValue());
            carReportDrivingCarListDto.setDrivingTime(bd.doubleValue());

            carReportDrivingCarListDtos.add(carReportDrivingCarListDto);
        }

        // 排序
        int sortType = command.getSortType();
        switch (sortType) {
            // 行驶时长由高到低
            case 1:
                carReportDrivingCarListDtos = carReportDrivingCarListDtos.stream().sorted(Comparator.comparing(CarReportDrivingCarListDTO::getTotalTime).reversed())
                        .collect(Collectors.toList());
                break;
            // 行驶时长由低到高
            case 2:
                carReportDrivingCarListDtos = carReportDrivingCarListDtos.stream().sorted(Comparator.comparing(CarReportDrivingCarListDTO::getTotalTime))
                        .collect(Collectors.toList());
                break;
            // 驾驶时长由高到低
            case 3:
                carReportDrivingCarListDtos = carReportDrivingCarListDtos.stream().sorted(Comparator.comparing(CarReportDrivingCarListDTO::getDrivingTime).reversed())
                        .collect(Collectors.toList());
                break;
            // 驾驶时长由低到高
            case 4:
                carReportDrivingCarListDtos = carReportDrivingCarListDtos.stream().sorted(Comparator.comparing(CarReportDrivingCarListDTO::getDrivingTime))
                        .collect(Collectors.toList());
                break;
            // 怠速时长由高到低
            case 5:
                carReportDrivingCarListDtos = carReportDrivingCarListDtos.stream().sorted(Comparator.comparing(CarReportDrivingCarListDTO::getIdlTime).reversed())
                        .collect(Collectors.toList());
                break;
            // 怠速时长由低到高
            case 6:
                carReportDrivingCarListDtos = carReportDrivingCarListDtos.stream().sorted(Comparator.comparing(CarReportDrivingCarListDTO::getIdlTime))
                        .collect(Collectors.toList());
                break;
            // 默认
            default:
                break;
        }
        PagingInfo<CarReportDrivingCarListDTO> pagingInfo = new PagingInfo<>();
        // 分页
        if (StringUtil.isNotEq("1", command.getReturnAll())) {
            // 分页
            carReportDrivingCarListDtos = pageUtil.paging(carReportDrivingCarListDtos, command.getPage_number(), command.getPage_size(), pagingInfo);
            pagingInfo.setList(carReportDrivingCarListDtos);
        } else {
            pagingInfo.setList(carReportDrivingCarListDtos);
            pagingInfo.setTotal(carReportDrivingCarListDtos.size());
            pagingInfo.setPage_total(1);
        }

        return pagingInfo;
    }

    /**
     * 车辆报表-怠速时长
     *
     * @param list
     * @return
     */
    private CarReportIdlDrivingDTO statisticsIdlDriving(List<FltStatisticalPojo> list) {
        CarReportIdlDrivingDTO carReportIdlDrivingDto = new CarReportIdlDrivingDTO();
        if (list.size() == 0) {
            return null;
        }
        long timeTotal = 0L;
        long idleTime = 0L;
        for (FltStatisticalPojo fltStatisticalPojo : list) {
            timeTotal += fltStatisticalPojo.getTimeTotal();
            idleTime += fltStatisticalPojo.getIdleTime();
        }

        BigDecimal bt = new BigDecimal(timeTotal).divide(new BigDecimal(3600000.0), 1, BigDecimal.ROUND_HALF_UP);
        BigDecimal bi = new BigDecimal(idleTime).divide(new BigDecimal(3600000.0), 1, BigDecimal.ROUND_HALF_UP);

        carReportIdlDrivingDto.setTotalTime(bt.toString());
        carReportIdlDrivingDto.setIdlTime(bi.toString());

        if (BigDecimal.ZERO.compareTo(bt) == 0) {
            carReportIdlDrivingDto.setIdlTimePro("0.0");
        } else {
            carReportIdlDrivingDto.setIdlTimePro(bi.multiply(new BigDecimal(100)).divide(bt, 1, BigDecimal.ROUND_HALF_UP).toString());
        }

        // 按车分组数据
        Map<String, List<FltStatisticalPojo>> map = list.stream().collect(Collectors.groupingBy(FltStatisticalPojo::getCarId));

        // 按车分组统计怠速时长
        Map<String, LongSummaryStatistics> idlTimeMap = list.stream().collect(Collectors.groupingBy(FltStatisticalPojo::getCarId, Collectors.summarizingLong(FltStatisticalPojo::getIdleTime)));
        List<CarReportIdlDrivingRankingDTO> rankingList = new ArrayList<>();
        CarReportIdlDrivingRankingDTO carReportIdlDrivingRankingDto;
        for (Map.Entry<String, LongSummaryStatistics> entry : idlTimeMap.entrySet()) {
            carReportIdlDrivingRankingDto = new CarReportIdlDrivingRankingDTO();
            LongSummaryStatistics longSummaryStatistics = entry.getValue();
            // 获取车牌号
            carReportIdlDrivingRankingDto.setCarNumber(map.get(entry.getKey()).get(0).getCarNum());
            BigDecimal carIdlTime = new BigDecimal(longSummaryStatistics.getSum()).divide(new BigDecimal(3600000.0), 1, BigDecimal.ROUND_HALF_UP);
            carReportIdlDrivingRankingDto.setIdlTime(carIdlTime.doubleValue());
            if (bt.compareTo(BigDecimal.ZERO) == 0) {
                carReportIdlDrivingRankingDto.setProportion("0.0");
            } else {
                carReportIdlDrivingRankingDto.setProportion(carIdlTime.multiply(new BigDecimal(100)).divide(bt, 1, BigDecimal.ROUND_HALF_UP).toString());
            }

            rankingList.add(carReportIdlDrivingRankingDto);
        }

        if (rankingList.size() > 0) {
            // 排序
            rankingList = rankingList.stream().sorted(Comparator.comparing(CarReportIdlDrivingRankingDTO::getIdlTime).reversed())
                    .collect(Collectors.toList());
        }
        // 最多取前5名
        if (rankingList.size() > 5) {
            rankingList = rankingList.subList(0, 5);
        }

        carReportIdlDrivingDto.setList(rankingList);
        return carReportIdlDrivingDto;
    }

    /**
     * 车辆报表-行驶时长图表统计
     *
     * @param list
     * @param command
     * @return
     */
    private CarReportDrivingChartDTO statisticsDrivingChart(List<FltStatisticalPojo> list, StatisticsCarReportChartForm command) {
        CarReportDrivingChartDTO carReportDrivingChartDto = new CarReportDrivingChartDTO();

        if (StringUtil.isNotEmpty(command.getCarId())) {
            list = list.stream().filter(fsp -> StringUtil.isEq(command.getCarId(), fsp.getCarId())).collect(Collectors.toList());
        }
        if (list.size() == 0) {
            // 是否需要统计图表信息
            if (StringUtil.isEq("1", command.getChartType())) {
                List<DrivingDetailChartDTO> chartList = new ArrayList<>();
                String[] dates = DateUtil.getDateArrayBetween(command.getBeginDate(), command.getEndDate());
                DrivingDetailChartDTO drivingDetailChartDto;
                for (String s : dates) {
                    drivingDetailChartDto = new DrivingDetailChartDTO();
                    drivingDetailChartDto.setStatisDate(s);
                    drivingDetailChartDto.setTime("0.0");
                    chartList.add(drivingDetailChartDto);
                }
                carReportDrivingChartDto.setList(chartList);
            }
            return carReportDrivingChartDto;
        }

        long timeTotal = 0L;
        long idleTime = 0L;
        for (FltStatisticalPojo fltStatisticalPojo : list) {
            timeTotal += fltStatisticalPojo.getTimeTotal();
            idleTime += fltStatisticalPojo.getIdleTime();
        }

        BigDecimal bt = new BigDecimal(timeTotal).divide(new BigDecimal(3600000.0), 1, BigDecimal.ROUND_HALF_UP);
        BigDecimal bi = new BigDecimal(idleTime).divide(new BigDecimal(3600000.0), 1, BigDecimal.ROUND_HALF_UP);
        BigDecimal bd = bt.subtract(bi);
        carReportDrivingChartDto.setTotalTime(bt.toString());
        carReportDrivingChartDto.setIdlTime(bi.toString());
        carReportDrivingChartDto.setDrivingTime(bd.toString());

        // 是否需要统计图表信息
        if (StringUtil.isEq("1", command.getChartType())) {
            List<DrivingDetailChartDTO> chartList = new ArrayList<>();
            // 按日期分组求和
            Map<String, LongSummaryStatistics> map = list.stream().collect(Collectors.groupingBy(FltStatisticalPojo::getStatisDate, Collectors.summarizingLong(FltStatisticalPojo::getTimeTotal)));

            String[] dates = DateUtil.getDateArrayBetween(command.getBeginDate(), command.getEndDate());
            DrivingDetailChartDTO drivingDetailChartDto;
            for (String s : dates) {
                drivingDetailChartDto = new DrivingDetailChartDTO();
                drivingDetailChartDto.setStatisDate(s);
                LongSummaryStatistics longSummaryStatistics = map.get(s);
                if (longSummaryStatistics == null) {
                    drivingDetailChartDto.setTime("0.0");
                } else {
                    drivingDetailChartDto.setTime(new BigDecimal(longSummaryStatistics.getSum()).divide(new BigDecimal(3600000.0), 1, BigDecimal.ROUND_HALF_UP).toString());
                }
                chartList.add(drivingDetailChartDto);
            }
            carReportDrivingChartDto.setList(chartList);
        }
        return carReportDrivingChartDto;
    }


    /**
     * 车辆报表-车辆油耗明细列表
     *
     * @param list
     * @param command
     * @return
     */
    private List<CarReportOilCarDeTailDTO> statisticsDetailedOilList(List<FltStatisticalPojo> list, StatisticsMileageCarDetailForm command) {

        if (StringUtil.isNotEmpty(command.getCarId())) {
            list = list.stream().filter(fsp -> StringUtil.isEq(command.getCarId(), fsp.getCarId())).collect(Collectors.toList());
        }
        if (list.size() == 0) {
            return null;
        }

        List<CarReportOilCarDeTailDTO> returnList = new ArrayList<>();
        String[] dates = DateUtil.getDateArrayBetween(command.getBeginDate(), command.getEndDate());
        Map<String, DoubleSummaryStatistics> mapMil = list.stream().collect(Collectors.groupingBy(FltStatisticalPojo::getStatisDate, Collectors.summarizingDouble(FltStatisticalPojo::getMileage)));
        Map<String, DoubleSummaryStatistics> mapOil = list.stream().collect(Collectors.groupingBy(FltStatisticalPojo::getStatisDate, Collectors.summarizingDouble(FltStatisticalPojo::getOilwear)));
        Map<String, DoubleSummaryStatistics> mapIdlOil = list.stream().collect(Collectors.groupingBy(FltStatisticalPojo::getStatisDate, Collectors.summarizingDouble(FltStatisticalPojo::getIdlingOil)));

        CarReportOilCarDeTailDTO carReportOilCarDeTailDto;
        for (String s : dates) {
            carReportOilCarDeTailDto = new CarReportOilCarDeTailDTO();

            DoubleSummaryStatistics d1 = mapMil.get(s);
            DoubleSummaryStatistics d2 = mapOil.get(s);
            DoubleSummaryStatistics d3 = mapIdlOil.get(s);
            carReportOilCarDeTailDto.setData(timeCon(s));

            if (d1 == null) {
                carReportOilCarDeTailDto.setTotalOil("0.0");
                carReportOilCarDeTailDto.setIdlOil("0.0");
                carReportOilCarDeTailDto.setAvgOil("0.0");
            } else {
                carReportOilCarDeTailDto.setTotalOil(new BigDecimal(d2.getSum()).setScale(1, BigDecimal.ROUND_HALF_UP).toString());
                carReportOilCarDeTailDto.setIdlOil(new BigDecimal(d3.getSum()).setScale(1, BigDecimal.ROUND_HALF_UP).toString());
                if (d1.getSum() == 0) {
                    carReportOilCarDeTailDto.setAvgOil("0.0");
                } else {
                    carReportOilCarDeTailDto.setAvgOil(new BigDecimal(d2.getSum()).multiply(new BigDecimal(100)).divide(new BigDecimal(d1.getSum()), 1, BigDecimal.ROUND_HALF_UP).toString());
                }
            }
            returnList.add(carReportOilCarDeTailDto);
        }
        return returnList;
    }

    /**
     * 车辆报表-油耗车辆列表
     *
     * @param list
     * @param command
     * @return
     * @throws Exception
     */
    private PagingInfo<CarReportOilCarListDTO> statisticsMileageCarList(List<FltStatisticalPojo> list, StatisticsOilCarListForm command) throws Exception {
        List<CarReportOilCarListDTO> carReportOilCarListDtos = new ArrayList<>();

        if (StringUtil.isNotEmpty(command.getKeyWord())) {
            list = list.stream().filter(fsp -> fsp.getCarNum().contains(command.getKeyWord())).collect(Collectors.toList());
        }
        if (list.size() == 0) {
            return null;
        }
        // 按车分组数据
        Map<String, List<FltStatisticalPojo>> map = list.stream().collect(Collectors.groupingBy(FltStatisticalPojo::getCarId));
        CarReportOilCarListDTO carReportOilCarListDto;
        for (Map.Entry<String, List<FltStatisticalPojo>> entry : map.entrySet()) {
            carReportOilCarListDto = new CarReportOilCarListDTO();
            carReportOilCarListDto.setCarId(entry.getKey());
            List<FltStatisticalPojo> carDataList = entry.getValue();
            carReportOilCarListDto.setCarNumber(carDataList.get(0).getCarNum());
            // 统计里程
            Double mileage = carDataList.stream().mapToDouble(FltStatisticalPojo::getMileage).sum();
            Double oil = carDataList.stream().mapToDouble(FltStatisticalPojo::getOilwear).sum();

            carReportOilCarListDto.setTotalOil(new BigDecimal(oil).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue());
            carReportOilCarListDto.setIdlOil(new BigDecimal(carDataList.stream().mapToDouble(FltStatisticalPojo::getIdlingOil).sum()).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue());

            if (mileage == 0) {
                carReportOilCarListDto.setAvgOil(0.0);
            } else {
                carReportOilCarListDto.setAvgOil(new BigDecimal(oil).multiply(new BigDecimal(100)).divide(new BigDecimal(mileage), 1, BigDecimal.ROUND_HALF_UP).doubleValue());
            }
            carReportOilCarListDtos.add(carReportOilCarListDto);
        }

        //拿到第一名的平均油耗
        List<CarReportOilCarListDTO> oilList = carReportOilCarListDtos.stream().sorted(Comparator.comparing(CarReportOilCarListDTO::getAvgOil)).collect(Collectors.toList());
        CarReportOilCarListDTO carReportOilCarListDTO = oilList.get(0);
        carReportOilCarListDTO.setDifference("0.0");
        double avgOil = carReportOilCarListDTO.getAvgOil();
        oilList.remove(0);
        carReportOilCarListDtos= oilList.stream()
                .peek(carListDTO -> carListDTO.setDifference(String.format("%.1f",(carListDTO.getAvgOil() - avgOil))))
                .collect(Collectors.toList());
        carReportOilCarListDtos.add(carReportOilCarListDTO);

        // 排序
        int sortType = command.getSortType();
        switch (sortType) {
            // 总油耗由高到低
            case 1:
                carReportOilCarListDtos = carReportOilCarListDtos.stream().sorted(Comparator.comparing(CarReportOilCarListDTO::getTotalOil).reversed())
                        .collect(Collectors.toList());
                break;
            // 总油耗由低到高
            case 2:
                carReportOilCarListDtos = carReportOilCarListDtos.stream().sorted(Comparator.comparing(CarReportOilCarListDTO::getTotalOil))
                        .collect(Collectors.toList());
                break;
            // 与第一名的差由高到底
            case 3:
                carReportOilCarListDtos = carReportOilCarListDtos.stream().sorted(Comparator.comparing(carListDTO ->Double.valueOf(carListDTO.getDifference()),Comparator.reverseOrder()))
                        .collect(Collectors.toList());
                break;
            // 与第一名的差由低祷告
            case 4:
                carReportOilCarListDtos = carReportOilCarListDtos.stream().sorted(Comparator.comparing(carListDTO -> Double.valueOf(carListDTO.getDifference())))
                        .collect(Collectors.toList());
                break;
            // 平均油耗由高到低
            case 5:
                carReportOilCarListDtos = carReportOilCarListDtos.stream().sorted(Comparator.comparing(CarReportOilCarListDTO::getAvgOil).reversed())
                        .collect(Collectors.toList());
                break;
            // 平均油耗由低到高
            case 6:
                carReportOilCarListDtos = carReportOilCarListDtos.stream().sorted(Comparator.comparing(CarReportOilCarListDTO::getAvgOil))
                        .collect(Collectors.toList());
                break;
            // 默认
            default:
                break;
        }

        PagingInfo<CarReportOilCarListDTO> pagingInfo = new PagingInfo<>();
        // 分页
        if (StringUtil.isNotEq("1", command.getReturnAll())) {
            // 分页
            carReportOilCarListDtos = pageUtil.paging(carReportOilCarListDtos, command.getPage_number(), command.getPage_size(), pagingInfo);
            pagingInfo.setList(carReportOilCarListDtos);
        } else {
            pagingInfo.setList(carReportOilCarListDtos);
            pagingInfo.setTotal(carReportOilCarListDtos.size());
            pagingInfo.setPage_total(1);
        }
        return pagingInfo;
    }


    /**
     * 车辆报表-怠速油耗
     *
     * @param list
     * @return
     */
    private CarReportIdlOilDTO statisticsIdlOil(List<FltStatisticalPojo> list,String appType) {
        CarReportIdlOilDTO carReportIdlOilDto = new CarReportIdlOilDTO();
        if (list.size() == 0) {
            return null;
        }
        float oilwear = 0.0F;
        float idlingOil = 0.0F;
        for (FltStatisticalPojo fltStatisticalPojo : list) {
            oilwear += fltStatisticalPojo.getOilwear();
            idlingOil += fltStatisticalPojo.getIdlingOil();
        }

        carReportIdlOilDto.setTotalOil(String.format("%.1f", oilwear));
        carReportIdlOilDto.setTotalIdlOil(String.format("%.1f", idlingOil));
        if (oilwear == 0) {
            carReportIdlOilDto.setIdlOilPro("0.0");
        } else {
            carReportIdlOilDto.setIdlOilPro(String.format("%.1f", idlingOil / oilwear * 100));
        }

        List<CarReportIdlOilRankingDTO> rankingList = new ArrayList<>();
        CarReportIdlOilRankingDTO carReportIdlOilRankingDto;
        // 按车分组数据
        Map<String, List<FltStatisticalPojo>> map = list.stream().collect(Collectors.groupingBy(FltStatisticalPojo::getCarId));
        if (appType.equals("1")){
            //按车分组统计平均油耗
            for (Map.Entry<String, List<FltStatisticalPojo>> entry : map.entrySet()) {
                carReportIdlOilRankingDto = new CarReportIdlOilRankingDTO();
                // 获取车牌号
                List<FltStatisticalPojo> carDataList = entry.getValue();
                carReportIdlOilRankingDto.setCarNumber(carDataList.get(0).getCarNum());
                // 统计里程
                Double mileage = carDataList.stream().mapToDouble(FltStatisticalPojo::getMileage).sum();
                Double oil = carDataList.stream().mapToDouble(FltStatisticalPojo::getOilwear).sum();
                if (mileage == 0) {
                    carReportIdlOilRankingDto.setAvgOil(0.0);
                } else {
                    carReportIdlOilRankingDto.setAvgOil(new BigDecimal(oil).multiply(new BigDecimal(100)).divide(new BigDecimal(mileage), 1, BigDecimal.ROUND_HALF_UP).doubleValue());
                }
                rankingList.add(carReportIdlOilRankingDto);
            }
            if (rankingList.size() > 0) {
                // 排序
                rankingList = rankingList.stream().sorted(Comparator.comparingDouble(CarReportIdlOilRankingDTO::getAvgOil))
                        .collect(Collectors.toList());
            }
        }else{
            // 按车分组统计怠速油耗
            Map<String, DoubleSummaryStatistics> idlOilMap = list.stream().collect(Collectors.groupingBy(FltStatisticalPojo::getCarId, Collectors.summarizingDouble(FltStatisticalPojo::getIdlingOil)));

            for (Map.Entry<String, DoubleSummaryStatistics> entry : idlOilMap.entrySet()) {
                carReportIdlOilRankingDto = new CarReportIdlOilRankingDTO();
                DoubleSummaryStatistics doubleSummaryStatistics = entry.getValue();
                // 获取车牌号
                carReportIdlOilRankingDto.setCarNumber(map.get(entry.getKey()).get(0).getCarNum());
                carReportIdlOilRankingDto.setIdlOil(new BigDecimal(doubleSummaryStatistics.getSum()).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue());
                if (oilwear == 0) {
                    carReportIdlOilRankingDto.setProportion("0.0");
                } else {
                    carReportIdlOilRankingDto.setProportion(String.format("%.1f", doubleSummaryStatistics.getSum() / oilwear * 100));
                }
                rankingList.add(carReportIdlOilRankingDto);
            }
            if (rankingList.size() > 0) {
                // 排序
                rankingList = rankingList.stream().sorted(Comparator.comparingDouble(CarReportIdlOilRankingDTO::getIdlOil))
                        .collect(Collectors.toList());
            }
        }


        // 最多取前5名
        if (rankingList.size() > 5) {
            rankingList = rankingList.subList(0, 5);
        }

        carReportIdlOilDto.setList(rankingList);
        return carReportIdlOilDto;
    }

    /**
     * 车辆报表-总油耗图表
     *
     * @param list
     * @param command
     * @return
     */
    private CarReportOilChartDTO statisticsOilChart(List<FltStatisticalPojo> list, StatisticsCarReportChartForm command) {
        CarReportOilChartDTO carReportOilChartDto = new CarReportOilChartDTO();

        if (StringUtil.isNotEmpty(command.getCarId())) {
            list = list.stream().filter(fsp -> StringUtil.isEq(command.getCarId(), fsp.getCarId())).collect(Collectors.toList());
        }
        if (list.size() == 0) {
            // 是否需要统计图表信息
            if (StringUtil.isEq("1", command.getChartType())) {
                List<OilDetailChartDTO> chartList = new ArrayList<>();
                String[] dates = DateUtil.getDateArrayBetween(command.getBeginDate(), command.getEndDate());
                OilDetailChartDTO oilDetailChartDto;
                for (String s : dates) {
                    oilDetailChartDto = new OilDetailChartDTO();
                    oilDetailChartDto.setStatisDate(s);
                    oilDetailChartDto.setOil("0.0");
                    chartList.add(oilDetailChartDto);
                }
                carReportOilChartDto.setList(chartList);
            }
            return carReportOilChartDto;
        }
        float mileage = 0.0F;
        float oilwear = 0.0F;
        float idlingOil = 0.0F;
        for (FltStatisticalPojo fltStatisticalPojo : list) {
            mileage += fltStatisticalPojo.getMileage();
            oilwear += fltStatisticalPojo.getOilwear();
            idlingOil += fltStatisticalPojo.getIdlingOil();
        }
        carReportOilChartDto.setTotalOil(String.format("%.1f", oilwear));
        carReportOilChartDto.setIdlOil(String.format("%.1f", idlingOil));
        if (mileage == 0) {
            carReportOilChartDto.setAvgOil("0.0");
        } else {
            carReportOilChartDto.setAvgOil(String.format("%.1f", oilwear * 100 / mileage));
        }

        // 是否需要统计图表信息
        if (StringUtil.isEq("1", command.getChartType())) {
            List<OilDetailChartDTO> chartList = new ArrayList<>();
            // 按日期分组求和
            Map<String, DoubleSummaryStatistics> map = list.stream().collect(Collectors.groupingBy(FltStatisticalPojo::getStatisDate, Collectors.summarizingDouble(FltStatisticalPojo::getOilwear)));

            String[] dates = DateUtil.getDateArrayBetween(command.getBeginDate(), command.getEndDate());
            OilDetailChartDTO oilDetailChartDto;
            for (String s : dates) {
                oilDetailChartDto = new OilDetailChartDTO();
                oilDetailChartDto.setStatisDate(s);
                DoubleSummaryStatistics doubleSummaryStatistics = map.get(s);
                if (doubleSummaryStatistics == null) {
                    oilDetailChartDto.setOil("0.0");
                } else {
                    oilDetailChartDto.setOil(String.format("%.1f", doubleSummaryStatistics.getSum()));
                }
                chartList.add(oilDetailChartDto);
            }
            carReportOilChartDto.setList(chartList);
        }
        return carReportOilChartDto;
    }

    /**
     * 车辆报表-车辆里程明细
     *
     * @param list
     * @param command
     * @return
     */
    private List<CarReportMileageCarDeTailDTO> statisticsDetailedMileageList(List<FltStatisticalPojo> list, StatisticsMileageCarDetailForm command) {

        if (StringUtil.isNotEmpty(command.getCarId())) {
            list = list.stream().filter(fsp -> StringUtil.isEq(command.getCarId(), fsp.getCarId())).collect(Collectors.toList());
        }
        if (list.size() == 0) {
            return null;
        }
        Map<String, DoubleSummaryStatistics> map = list.stream().collect(Collectors.groupingBy(FltStatisticalPojo::getStatisDate, Collectors.summarizingDouble(FltStatisticalPojo::getMileage)));

        List<CarReportMileageCarDeTailDTO> returnList = new ArrayList<>();
        String[] dates = DateUtil.getDateArrayBetween(command.getBeginDate(), command.getEndDate());
        CarReportMileageCarDeTailDTO carReportMileageCarDeTailDto;
        for (String s : dates) {
            carReportMileageCarDeTailDto = new CarReportMileageCarDeTailDTO();
            DoubleSummaryStatistics doubleSummaryStatistics = map.get(s);
            carReportMileageCarDeTailDto.setData(timeCon(s));
            if (doubleSummaryStatistics == null) {
                carReportMileageCarDeTailDto.setMileage("0.0");
            } else {
                carReportMileageCarDeTailDto.setMileage(new BigDecimal(doubleSummaryStatistics.getSum()).setScale(1, BigDecimal.ROUND_HALF_UP).toString());
            }
            returnList.add(carReportMileageCarDeTailDto);
        }
        return returnList;
    }

    /**
     * 将时间转为M月dd日格式
     *
     * @param time 原始时间
     * @return 转换时间
     */
    private String timeCon(String time) {
        Date d = DateUtil.dateFormatConversion(time, DateUtil.date_s_pattern);
        SimpleDateFormat sdf = new SimpleDateFormat("M月dd日");
        return sdf.format(d);
    }

    /**
     * 车辆报表-行驶里程图表
     *
     * @param list
     * @param command
     * @return
     */
    private CarReportMileageChartDTO statisticsMileageChart(List<FltStatisticalPojo> list, StatisticsCarReportChartForm command) {
        CarReportMileageChartDTO carReportMileageChartDto = new CarReportMileageChartDTO();

        if (StringUtil.isNotEmpty(command.getCarId())) {
            list = list.stream().filter(fsp -> StringUtil.isEq(command.getCarId(), fsp.getCarId())).collect(Collectors.toList());
        }
        if (list.size() == 0) {
            // 是否需要统计图表信息
            if (StringUtil.isEq("1", command.getChartType())) {
                List<MileageDetailChartDTO> chartList = new ArrayList<>();
                String[] dates = DateUtil.getDateArrayBetween(command.getBeginDate(), command.getEndDate());
                MileageDetailChartDTO mileageDetailChartDto;
                for (String s : dates) {
                    mileageDetailChartDto = new MileageDetailChartDTO();
                    mileageDetailChartDto.setStatisDate(s);
                    mileageDetailChartDto.setMileage("0.0");
                    chartList.add(mileageDetailChartDto);
                }
                carReportMileageChartDto.setList(chartList);
            }
            return carReportMileageChartDto;
        }
        float mileage = 0.0F;
        long speedSum = 0L;
        int speedCount = 0;
        Set<String> set = new HashSet<>();
        for (FltStatisticalPojo fltStatisticalPojo : list) {
            set.add(fltStatisticalPojo.getCarId());
            mileage += fltStatisticalPojo.getMileage();
            speedSum += fltStatisticalPojo.getSpeedSum();
            speedCount += fltStatisticalPojo.getSpeedCount();
        }

        // 数据统计时间范围
        Long dateInterval = DateUtil.diffByDay(command.getBeginDate(), command.getEndDate(), "") + 1;

        carReportMileageChartDto.setTotalMileage(String.format("%.1f", mileage));
        carReportMileageChartDto.setAvgMileage(String.format("%.1f", mileage / set.size() / dateInterval.doubleValue()));
        if (speedCount != 0) {
            carReportMileageChartDto.setAvgSpeed(String.format("%.1f", speedSum / Double.valueOf(speedCount)));
        } else {
            carReportMileageChartDto.setAvgSpeed("0.0");
        }

        // 是否需要统计图表信息
        if (StringUtil.isEq("1", command.getChartType())) {
            List<MileageDetailChartDTO> chartList = new ArrayList<>();
            // 按日期分组求和
            Map<String, DoubleSummaryStatistics> map = list.stream().collect(Collectors.groupingBy(FltStatisticalPojo::getStatisDate, Collectors.summarizingDouble(FltStatisticalPojo::getMileage)));

            String[] dates = DateUtil.getDateArrayBetween(command.getBeginDate(), command.getEndDate());
            MileageDetailChartDTO mileageDetailChartDto;
            for (String s : dates) {
                mileageDetailChartDto = new MileageDetailChartDTO();
                mileageDetailChartDto.setStatisDate(s);
                DoubleSummaryStatistics doubleSummaryStatistics = map.get(s);
                if (doubleSummaryStatistics == null) {
                    mileageDetailChartDto.setMileage("0.0");
                } else {
                    mileageDetailChartDto.setMileage(String.format("%.1f", doubleSummaryStatistics.getSum()));
                }
                chartList.add(mileageDetailChartDto);
            }
            carReportMileageChartDto.setList(chartList);
        }
        return carReportMileageChartDto;
    }

    /**
     * 车辆报表-里程车辆列表
     *
     * @param list
     * @param command
     * @return
     */
    private PagingInfo<CarReportMileageCarListDTO> statisticsMileageCarList(List<FltStatisticalPojo> list, StatisticsMileageCarListForm command) {
        List<CarReportMileageCarListDTO> carReportMileageCarListDtos = new ArrayList<>();

        if (StringUtil.isNotEmpty(command.getKeyWord())) {
            list = list.stream().filter(fsp -> fsp.getCarNum().contains(command.getKeyWord())).collect(Collectors.toList());
        }

        if (list.size() == 0) {
            return null;
        }
        // 按车分组数据
        Map<String, List<FltStatisticalPojo>> map = list.stream().collect(Collectors.groupingBy(FltStatisticalPojo::getCarId));
        CarReportMileageCarListDTO carReportMileageCarListDto;
        for (Map.Entry<String, List<FltStatisticalPojo>> entry : map.entrySet()) {
            carReportMileageCarListDto = new CarReportMileageCarListDTO();
            carReportMileageCarListDto.setCarId(entry.getKey());
            List<FltStatisticalPojo> carDataList = entry.getValue();
            carReportMileageCarListDto.setCarNumber(carDataList.get(0).getCarNum());
            // 统计里程
            carReportMileageCarListDto.setTotalMileage(new BigDecimal(carDataList.stream().mapToDouble(FltStatisticalPojo::getMileage).sum()).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue());

            long sum = carDataList.stream().mapToLong(FltStatisticalPojo::getSpeedSum).sum();
            long count = carDataList.stream().mapToLong(FltStatisticalPojo::getSpeedCount).sum();
            if (count != 0) {
                carReportMileageCarListDto.setAvgSpeed(new BigDecimal(sum).divide(new BigDecimal(count), 1, BigDecimal.ROUND_HALF_UP).doubleValue());
            } else {
                carReportMileageCarListDto.setAvgSpeed(0.0);
            }
            carReportMileageCarListDtos.add(carReportMileageCarListDto);
        }

        // 排序
        int sortType = command.getSortType();
        switch (sortType) {
            // 总里程由高到低
            case 1:
                carReportMileageCarListDtos = carReportMileageCarListDtos.stream().sorted(Comparator.comparing(CarReportMileageCarListDTO::getTotalMileage).reversed())
                        .collect(Collectors.toList());
                break;
            // 总里程由低到高
            case 2:
                carReportMileageCarListDtos = carReportMileageCarListDtos.stream().sorted(Comparator.comparing(CarReportMileageCarListDTO::getTotalMileage))
                        .collect(Collectors.toList());
                break;
            // 平均速度由高到低
            case 3:
                carReportMileageCarListDtos = carReportMileageCarListDtos.stream().sorted(Comparator.comparing(CarReportMileageCarListDTO::getAvgSpeed).reversed())
                        .collect(Collectors.toList());
                break;
            // 平均速度由低到高
            case 4:
                carReportMileageCarListDtos = carReportMileageCarListDtos.stream().sorted(Comparator.comparing(CarReportMileageCarListDTO::getAvgSpeed))
                        .collect(Collectors.toList());
                break;
            // 默认
            default:
                break;
        }
        PagingInfo<CarReportMileageCarListDTO> pagingInfo = new PagingInfo<>();
        // 分页
        if (StringUtil.isNotEq("1", command.getReturnAll())) {
            // 分页
            carReportMileageCarListDtos = pageUtil.paging(carReportMileageCarListDtos, command.getPage_number(), command.getPage_size(), pagingInfo);
            pagingInfo.setList(carReportMileageCarListDtos);
        } else {
            pagingInfo.setList(carReportMileageCarListDtos);
            pagingInfo.setTotal(carReportMileageCarListDtos.size());
            pagingInfo.setPage_total(1);
        }
        return pagingInfo;
    }

    /**
     * 车辆报表数据统计
     *
     * @param list
     * @param dateInterval
     * @return
     */
    private CarReportDTO statisticsCarReport(List<FltStatisticalPojo> list, Long dateInterval) {
        CarReportDTO carReportDto = new CarReportDTO();
        if (list.size() == 0) {
            carReportDto.setTotalTime("0.0");
            carReportDto.setTotalMileage("0.0");
            carReportDto.setAvgMileage("0.0");
            carReportDto.setAvgSpeed("0.0");
            carReportDto.setTotalOil("0.0");
            carReportDto.setAvgOil("0.0");
            carReportDto.setIdleOil("0.0");
            carReportDto.setTotalTime("0.0");
            carReportDto.setIdleTime("0.0");
            carReportDto.setDrivingTime("0.0");
            return carReportDto;
        }
        float mileage = 0.0F;
        float oilwear = 0.0F;
        float idlingOil = 0.0F;
        long timeTotal = 0L;
        long idleTime = 0L;
        long speedSum = 0L;
        int speedCount = 0;
        int overSpeedCnt = 0;
        int raCnt = 0;
        int rdCnt = 0;
        int sharpTurnCnt = 0;
        int idleTimeoutCnt = 0;
        int coldRunCnt = 0;
        int nightRunCnt = 0;
        int lowGrHighSpdCnt = 0;
        int fullThrottleCnt = 0;
        int roughThrottleCnt = 0;
        int neutralGearCoastCnt = 0;
        int stallCoastCnt = 0;
        Set<String> set = new HashSet<>();
        for (FltStatisticalPojo fltStatisticalPojo : list) {
            set.add(fltStatisticalPojo.getCarId());
            mileage += fltStatisticalPojo.getMileage();
            oilwear += fltStatisticalPojo.getOilwear();
            idlingOil += fltStatisticalPojo.getIdlingOil();
            timeTotal += fltStatisticalPojo.getTimeTotal();
            idleTime += fltStatisticalPojo.getIdleTime();
            speedSum += fltStatisticalPojo.getSpeedSum();
            speedCount += fltStatisticalPojo.getSpeedCount();
            overSpeedCnt += fltStatisticalPojo.getOverSpeedCnt();
            raCnt += fltStatisticalPojo.getRaCnt();
            rdCnt += fltStatisticalPojo.getRdCnt();
            sharpTurnCnt += fltStatisticalPojo.getSharpTurnCnt();
            idleTimeoutCnt += fltStatisticalPojo.getIdleTimeoutCnt();
            coldRunCnt += fltStatisticalPojo.getColdRunCnt();
            nightRunCnt += fltStatisticalPojo.getNightRunCnt();
            lowGrHighSpdCnt += fltStatisticalPojo.getLowGrHighSpdCnt();
            fullThrottleCnt += fltStatisticalPojo.getFullThrottleCnt();
            roughThrottleCnt += fltStatisticalPojo.getRoughThrottleCnt();
            neutralGearCoastCnt += fltStatisticalPojo.getNeutralGearCoastCnt();
            stallCoastCnt += fltStatisticalPojo.getStallCoastCnt();
        }

        carReportDto.setCarTotal(set.size());
        carReportDto.setTotalMileage(String.format("%.1f", mileage) == null ? "0.0" : String.format("%.1f", mileage));
        carReportDto.setAvgMileage(String.format("%.1f", mileage / set.size() / dateInterval.doubleValue()) == null ? "0.0" : String.format("%.1f", mileage / set.size() / dateInterval.doubleValue()));
        if (speedCount != 0) {
            carReportDto.setAvgSpeed(String.format("%.1f", speedSum / Double.valueOf(speedCount)) == null ? "0.0" : String.format("%.1f", speedSum / Double.valueOf(speedCount)));
        } else {
            carReportDto.setAvgSpeed("0.0");
        }
        carReportDto.setTotalOil(String.format("%.1f", oilwear) == null ? "0.0" : String.format("%.1f", oilwear));

        if (mileage != 0) {
            carReportDto.setAvgOil(String.format("%.1f", oilwear * 100 / mileage) == null ? "0.0" : String.format("%.1f", oilwear * 100 / mileage));
        } else {
            carReportDto.setAvgOil("0.0");
        }
        carReportDto.setIdleOil(String.format("%.1f", idlingOil) == null ? "0.0" : String.format("%.1f", idlingOil));
        carReportDto.setTotalTime(String.format("%.1f", timeTotal / 3600000.0) == null ? "0.0" : String.format("%.1f", timeTotal / 3600000.0));
        carReportDto.setIdleTime(String.format("%.1f", idleTime / 3600000.0) == null ? "0.0" : String.format("%.1f", idleTime / 3600000.0));
        String t1 = carReportDto.getTotalTime();
        String t2 = carReportDto.getIdleTime();
        BigDecimal runTime = new BigDecimal(Double.valueOf(t1)).setScale(1, BigDecimal.ROUND_HALF_UP).subtract(new BigDecimal(Double.valueOf(t2)).setScale(1, BigDecimal.ROUND_HALF_UP));
        carReportDto.setDrivingTime(runTime.toString() == null ? "0.0" : runTime.toString());
        carReportDto.setOverSpeedCount(overSpeedCnt);
        carReportDto.setRapidAccelerationCount(raCnt);
        carReportDto.setRapidDecelerationCount(rdCnt);
        carReportDto.setSuddenTurnCount(sharpTurnCnt);
        carReportDto.setLongIdlingCount(idleTimeoutCnt);
        carReportDto.setColdCarCount(coldRunCnt);
        carReportDto.setNightDrivingCount(nightRunCnt);
        carReportDto.setEngineHighSpeedCount(lowGrHighSpdCnt);
        carReportDto.setFullThrottleCount(fullThrottleCnt);
        carReportDto.setBigThrottleCount(roughThrottleCnt);
        carReportDto.setCoastingCount(neutralGearCoastCnt);
        carReportDto.setCoastingEngineOffCount(stallCoastCnt);


        return carReportDto;
    }
}
