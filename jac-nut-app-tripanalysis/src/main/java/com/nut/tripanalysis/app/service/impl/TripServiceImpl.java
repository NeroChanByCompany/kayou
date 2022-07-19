package com.nut.tripanalysis.app.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.utils.DateUtil;
import com.nut.common.utils.HttpUtil;
import com.nut.common.utils.StringUtil;
import com.nut.tripanalysis.app.converter.TripInfoConvertor;
import com.nut.tripanalysis.app.dao.CarDao;
import com.nut.tripanalysis.app.dao.ModelInfoDao;
import com.nut.tripanalysis.app.dto.TripInfoDto;
import com.nut.tripanalysis.app.dto.TripMonthDto;
import com.nut.tripanalysis.app.dto.TripMonthListDto;
import com.nut.tripanalysis.app.entity.CarEntity;
import com.nut.tripanalysis.app.entity.ModelInfoEntity;
import com.nut.tripanalysis.app.form.QueryTripByDayForm;
import com.nut.tripanalysis.app.form.QueryTripByMonthForm;
import com.nut.tripanalysis.app.form.QueryTripInfoForm;
import com.nut.tripanalysis.app.pojo.*;
import com.nut.tripanalysis.app.service.TripService;
import com.nut.tripanalysis.common.component.BaiduMapComponent;
import com.nut.tripanalysis.common.utils.DoubleFormatUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.nut.common.utils.DateUtil.parseDate;

/*
 *  @author wuhaotian 2021/7/9
 */
@Slf4j
@Service("TripService")
public class TripServiceImpl implements TripService {

    @Autowired
    private CarDao carEntityMapper;
    @Value("${getTripAnalysisListUrl}")
    private String getTripAnalysisListUrl;
    @Value("${getBiDataUrl}")
    private String  getBiDataUrl;
    @Value("${getTripCountUrl}")
    private String getTripCountUrl;
    @Value("${getTripAnalysisDetailUrl}")
    private String getTripAnalysisDetailUrl;
    @Value("${lastLocationSearchUrl}")
    private String lastLocationSearch;

    static private DecimalFormat doubleFormat = new DecimalFormat("0.0");

    private static String textDescription = "过短的行程已被过滤，不在列表中展示，您今天短行程总计里程{隐藏里程}km，总计油耗有{隐藏油耗}L；";

    @Autowired
    private BaiduMapComponent baiduMapComponent;
    @Autowired
    private ModelInfoDao modelInfoEntityMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;


    @Override
    public HttpCommandResultWithData queryTripByDay(QueryTripByDayForm command) throws Exception {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        CarEntity car = carEntityMapper.selectByPrimaryKey(command.getCarId());
        if (car == null) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("查询车辆不存在");
            return result;
        }
        if (StringUtils.isBlank( car.getAutoTerminal() )){
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("该车无车联网设备，此功能无法使用");
            return result;
        }

        // 查询行程信息
        CarTripPojo carTripPojo = getTripList(Collections.singletonList(car.getAutoTerminal()), command.getDay(), 999, 1);

        //查询总里程总油耗
        List<MileageAndOilPojo> mileageAndOilPojoList = getMilAndOilByCarId(command.getDay(), Collections.singletonList(car.getAutoTerminal()));

        TripInfoByDatePojo tripInfoByDatePojo = new TripInfoByDatePojo();
        List<TripInfoPojo> tripInfoPojos = new ArrayList<>();
        //正常行程的当日总里程
        BigDecimal normalMileage = BigDecimal.ZERO;
        //正常行程的当日总油耗
        BigDecimal normalOil = BigDecimal.ZERO;
        if (carTripPojo != null && carTripPojo.getData() != null && carTripPojo.getData().size() > 0) {
            List<CarTripListPojo> carTripListPojos = carTripPojo.getData();
            TripInfoPojo tripInfoPojo;
            for (CarTripListPojo carTripListPojo : carTripListPojos) {
                tripInfoPojo = new TripInfoPojo();
                tripInfoPojo.setTripId(carTripListPojo.getTripId());
                tripInfoPojo.setStartTime(carTripListPojo.getStartTime() * 1000);
                tripInfoPojo.setEndTIme(carTripListPojo.getEndTime() * 1000);
                BigDecimal bl = BigDecimal.valueOf(carTripListPojo.getMileage() / 1000D).setScale(1, BigDecimal.ROUND_HALF_UP);
                tripInfoPojo.setTripLen(bl.toString());
                tripInfoPojo.setAvgOil(BigDecimal.valueOf(carTripListPojo.getAvgOil()).setScale(1, BigDecimal.ROUND_HALF_UP).toString());
                BigDecimal bo = BigDecimal.valueOf(carTripListPojo.getOil()).setScale(1, BigDecimal.ROUND_HALF_UP);
                tripInfoPojo.setTripOil(bo.toString());
                tripInfoPojo.setTripScore(carTripListPojo.getScore());
                tripInfoPojo.setCarId(command.getCarId());
                tripInfoPojo.setTerminalId(carTripListPojo.getTerminalId());
                tripInfoPojo.setBeyondFlag(carTripListPojo.getAcrossFlag() == 1);
                tripInfoPojo.setTripLenToday(doubleFormat.format(carTripListPojo.getAcrossMileage() / 1000D));
                tripInfoPojo.setTripOilToday(doubleFormat.format(carTripListPojo.getAcrossOil()));
                normalMileage = normalMileage.add(bl);
                normalOil = normalOil.add(bo);
                tripInfoPojos.add(tripInfoPojo);
            }
        } else {
            tripInfoByDatePojo.setPage_total(0L);
            tripInfoByDatePojo.setTotal(0L);
            tripInfoByDatePojo.setDayLen("0.0");
            tripInfoByDatePojo.setDayOil("0.0");
            if (mileageAndOilPojoList == null || mileageAndOilPojoList.size() == 0) {
                tripInfoByDatePojo.setTextDescription(textDescription
                        .replace("{隐藏里程}", "0.0")
                        .replace("{隐藏油耗}", "0.0"));
            } else {
                MileageAndOilPojo mileageAndOilPojo = mileageAndOilPojoList.get(0);
                tripInfoByDatePojo.setTextDescription(textDescription
                        .replace("{隐藏里程}", doubleFormat.format(mileageAndOilPojo.getmMilage()))
                        .replace("{隐藏油耗}", doubleFormat.format(mileageAndOilPojo.getFuel())));
            }
            tripInfoByDatePojo.setList(new ArrayList<>());
            result.setData(tripInfoByDatePojo);
            return result;
        }

        //按行程开始时间由新到旧排序
        tripInfoPojos = tripInfoPojos.stream().sorted(Comparator.comparing(TripInfoPojo::getStartTime).reversed()).collect(Collectors.toList());

        tripInfoByDatePojo.setList(tripInfoPojos);
        tripInfoByDatePojo.setDayOil(normalOil.toString());
        tripInfoByDatePojo.setDayLen(normalMileage.toString());

        if (mileageAndOilPojoList == null || mileageAndOilPojoList.size() == 0) {
            tripInfoByDatePojo.setTextDescription(textDescription
                    .replace("{隐藏里程}", "0.0")
                    .replace("{隐藏油耗}", "0.0"));

        } else {
            MileageAndOilPojo mileageAndOilPojo = mileageAndOilPojoList.get(0);
            tripInfoByDatePojo.setTextDescription(textDescription
                    .replace("{隐藏里程}", mileageAndOilPojo.getmMilage() > normalMileage.doubleValue() ? doubleFormat.format(mileageAndOilPojo.getmMilage() - normalMileage.doubleValue()) : "0.0")
                    .replace("{隐藏油耗}", mileageAndOilPojo.getFuel() > normalOil.doubleValue() ? doubleFormat.format(mileageAndOilPojo.getFuel() - normalOil.doubleValue()) : "0.0"));
        }
        tripInfoByDatePojo.setTotal(1L);
        tripInfoByDatePojo.setPage_total((long) tripInfoPojos.size());
        result.setData(tripInfoByDatePojo);
        return result;
    }

    @Override
    public HttpCommandResultWithData queryTripByMonth(QueryTripByMonthForm command) throws Exception {
        log.info("================queryTripByMonth service begin========================");
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());
        CarEntity car = carEntityMapper.selectByPrimaryKey(command.getCarId());
        if (car == null) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("查询车辆不存在");
            return result;
        }

        if (StringUtils.isBlank(car.getAutoTerminal())){
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("该车无车联网设备，此功能无法使用");
            return result;
        }

        // 查询位置云接口
        Map<String, Object> map = new HashMap<>(2);
        map.put("tid", car.getAutoTerminal());
        map.put("month", command.getMonth());
        map.put("isFilter", 1);

        List<TripMonthDto> entityList = HttpUtil.postJsonRequest(getTripCountUrl, map, new TypeReference<HttpCommandResultWithData<List<TripMonthDto>>>() {
        });

        TripMonthListDto tripMonthListDto = new TripMonthListDto();
        tripMonthListDto.setList(entityList);
        return result.setData(tripMonthListDto);
    }

    @Override
    public TripInfoDto queryTripInfo(QueryTripInfoForm command) throws Exception {
        // 行程详情查询
        Map<String, Object> map = new HashMap<>(2);
        map.put("tripId", command.getTripId());
        map.put("tripDate", DateFormatUtils.format(command.getStartTime(), "yyyyMMdd"));

        TripDetailPojo tripDetailPojo = HttpUtil.postJsonRequest(getTripAnalysisDetailUrl, map, new TypeReference<HttpCommandResultWithData<TripDetailPojo>>() {
        });
        if (tripDetailPojo == null) {
            return null;
        }
        log.info("行程分析返回: {}", tripDetailPojo.toString());
        TripInfoDto dto = TripInfoConvertor.entityToDto(tripDetailPojo);

        //调用逆地理接口，并设置开始点位置
        dto.setStartLocal(baiduMapComponent.getAddressByLonLat(String.valueOf(tripDetailPojo.getStartLat() / 1000000D), String.valueOf(tripDetailPojo.getStartLon() / 1000000D)));
        //调用逆地理接口，并设置结束点位置
        dto.setEndLocal(baiduMapComponent.getAddressByLonLat(String.valueOf(tripDetailPojo.getEndLat() / 1000000D), String.valueOf(tripDetailPojo.getEndLon() / 1000000D)));

        CarEntity car = carEntityMapper.selectByTerminalId(command.getTerminalId());
        if (car == null || StringUtils.isBlank(car.getCarModel())) {
            dto.setAvgOilCarModel(0.0D);
        } else {
            ModelInfoEntity modelInfo = modelInfoEntityMapper.selectByPrimaryKey(Long.parseLong(car.getCarModel()));
            dto.setAvgOilCarModel(DoubleFormatUtil.formatDoubleValueTo1Bit(modelInfo.getAvgOilWear() == null ? 0.0 : modelInfo.getAvgOilWear()));
        }
        // 查询历史平均油耗
        // 总里程油耗查询
        Map<String, Object> mapLast = new HashMap<>(2);
        mapLast.put("terminalIds", Collections.singletonList(command.getTerminalId()));
        mapLast.put("fields", Arrays.asList("sm", "sf"));
        List<LastLocationPojo> lastLocationPojos = HttpUtil.postJsonRequest(lastLocationSearch, mapLast, new TypeReference<HttpCommandResultWithData<List<LastLocationPojo>>>() {
        });

        if (lastLocationPojos != null && lastLocationPojos.size() > 0) {
            LastLocationPojo lastLocationPojo = lastLocationPojos.get(0);
            if (lastLocationPojo.getSm() != 0) {
                //历史平均油耗=总油耗/总行驶里程
                double dvalue = Double.parseDouble(String.valueOf(lastLocationPojo.getSf() * 100 / lastLocationPojo.getSm()));
                dto.setAvgOilHistory(DoubleFormatUtil.formatDoubleValueTo1Bit(dvalue));
            }
        }
        //填充建议语言查询
        BoundHashOperations tripHashOps = redisTemplate.boundHashOps("suggest_info");
        Map<String, String> suggestMap = tripHashOps.entries();
        if (suggestMap.size() == 0) {
            log.info("缓存中没有驾驶建议，添加驾驶建议");
            addSuggestInfo();
            tripHashOps = redisTemplate.boundHashOps("suggest_info");
            suggestMap = tripHashOps.entries();
        } else {
            log.info("缓存中有驾驶建议");
            log.info("suggestMap:{}", tripHashOps.entries());
        }

        // 驾驶建议注入
        if (StringUtil.isNotEmpty(dto.getDriveSuggest())) {
            String[] driverSug = dto.getDriveSuggest().split(",");
            String driverSug_str = "";
            for (String dSug : driverSug) {
                driverSug_str += suggestMap.get(dSug) + ",";
            }

            dto.setDriveSuggest(StringUtil.removeLastStr(driverSug_str, ",") + "。");
        }

        //油耗分析建议注入
        //1、当本次油耗大于同车型、历史平均油耗时，文案【油耗较高，建议尽快改善驾驶行为，若想了解详细情况，请联系厂家】
        if (dto.getAvgOil() > dto.getAvgOilCarModel() && dto.getAvgOil() > dto.getAvgOilHistory()) {
            dto.setOilSuggest(suggestMap.get("15"));
        }
        //2、当本次油耗大于同车型油耗，小于历史平均油耗时，文案【本次行程油耗略高，请注意改善驾驶行为】
        if (dto.getAvgOil() > dto.getAvgOilCarModel() && dto.getAvgOil() <= dto.getAvgOilHistory()) {
            dto.setOilSuggest(suggestMap.get("16"));
        }
        //3、当本次油耗小于同车型油耗，大于历史平均油耗是，文案【本次行程油耗有所增加，请注意驾驶行为】
        if (dto.getAvgOil() <= dto.getAvgOilCarModel() && dto.getAvgOil() > dto.getAvgOilHistory()) {
            dto.setOilSuggest(suggestMap.get("17"));
        }
        //4、本次油耗低于同车型油耗、历史平均油耗时，文案【本次行程行为优秀，油耗较低，建议保持。】
        if (dto.getAvgOil() <= dto.getAvgOilCarModel() && dto.getAvgOil() <= dto.getAvgOilHistory()) {
            dto.setOilSuggest(suggestMap.get("18"));
        }

        //当制动里程大于行程里程时制动里程取总里程数据
        if (dto.getBreakLen() > dto.getTripLen()) {
            dto.setBreakLen(dto.getTripLen());
        }
        return dto;
    }

    /**
     * 行程信息查询  位置云
     *
     * @param autoTerminal
     * @param queryTime
     * @param page_size
     * @param page_number
     * @return
     * @throws Exception
     */
    private CarTripPojo getTripList(List<String> autoTerminal, String queryTime, int page_size, int page_number) throws Exception {
        Map<String, Object> map = new HashMap<>(5);
        // 时间格式转换, APP行程只按天查询
        String start = DateUtil.formatTime(parseDate(queryTime, "yyyyMMdd"));
        String end = start.replace("00:00:00", "23:59:59");

        map.put("tids", autoTerminal);
        map.put("start", start);
        map.put("end", end);
        map.put("isFilter", 1);
        map.put("page_size", page_size);
        map.put("page_number", page_number);

        // 调用位置云行程列表接口
        CarTripPojo carTripPojo = HttpUtil.postJsonRequest(getTripAnalysisListUrl, map, new TypeReference<HttpCommandResultWithData<CarTripPojo>>() {
        });
        log.info("行程列表返回: {}", carTripPojo);
        return carTripPojo;
    }

    /**
     * 日里程油耗信息查询
     *
     * @param time
     * @param terminalId
     * @return
     * @throws Exception
     */
    private List<MileageAndOilPojo> getMilAndOilByCarId(String time, List<String> terminalId) throws Exception {
        // 封装查询条件
        Map<String, Object> paramMap = new HashMap<>(3);
        Long date = parseDate(time, "yyyyMMdd").getTime() / 1000;
        paramMap.put("startTime", date);
        paramMap.put("endTime", date);
        paramMap.put("terminalIds", terminalId);
        List<MileageAndOilPojo> list = HttpUtil.postJsonRequest(getBiDataUrl, paramMap, new TypeReference<HttpCommandResultWithData<List<MileageAndOilPojo>>>() {
        });
        return list;
    }

    /**
     * 当缓存里没有行驶建议的时候，向缓存里面添加行驶建议
     */
    public void addSuggestInfo() {
        Map<String, String> map = new HashMap<>();
        map.put("1", "速度偏高");
        map.put("2", "速度略高");
        map.put("3", "速度稳定,路况通畅");
        map.put("4", "速度略低,部分路段不通畅");
        map.put("5", "速度偏低,部分路段不通畅");
        map.put("6", "建议平稳驾驶");
        map.put("7", "驾驶习惯急待改进");
        map.put("8", "注意不良驾驶习惯");
        map.put("9", "驾驶习惯良好");
        map.put("10", "油耗较高,请注意养成良好驾驶习惯");
        map.put("11", "油耗偏高,请注意养成良好驾驶习惯");
        map.put("12", "油耗略高,请注意养成良好驾驶习惯");
        map.put("13", "油耗正常,请保持良好驾驶习惯");
        map.put("14", "油耗低,请保持良好驾驶习惯");
        redisTemplate.opsForHash().putAll("suggest_info", map);
    }
}
