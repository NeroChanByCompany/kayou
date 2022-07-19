package com.nut.driver.app.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nut.common.exception.ExceptionUtil;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.utils.DateUtil;
import com.nut.common.utils.HttpUtil;
import com.nut.common.utils.StringUtil;
import com.nut.driver.app.dao.CarDao;
import com.nut.driver.app.dto.TripCarListDto;
import com.nut.driver.app.form.QueryTripCarListForm;
import com.nut.driver.app.pojo.CarTripListPojo;
import com.nut.driver.app.pojo.CarTripPojo;
import com.nut.driver.app.service.QueryTripCarListService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.service.impl
 * @Author: yzl
 * @CreateTime: 2021-06-30 09:48
 * @Version: 1.0
 */
@Service
@Slf4j
public class QueryTripCarListServiceImpl extends DriverBaseService implements QueryTripCarListService {

    @Autowired
    private CarDao carDao;

    @Value("${getTripAnalysisListUrl}")
    private String getTripAnalysisList;

    static private DecimalFormat doubleFormat = new DecimalFormat("0.0");


    @Override
    @SneakyThrows
    public List<TripCarListDto> queryTripCarList(QueryTripCarListForm form) {
        //日期不能大于当前日期
        String currentTime = DateUtil.getNowDate_yyyyMMdd();
        if (form.getQueryTime().compareTo(currentTime) > 0){
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(),"查询日期不能超过当前日期！");
        }

        // 查询用户车辆及车辆基础信息列表
        List<TripCarListDto> cars = carDao.queryTripCarsByCarIds(form.getAutoIncreaseId());
        // 有条件时进行过滤
        if (StringUtil.isNotEmpty(form.getCarNumber())){
            cars = cars.stream().filter(tcp -> tcp.getCarNumber().contains(form.getCarNumber())).collect(Collectors.toList());
        }
        if (cars.size() == 0){
            return cars;
        }
        //整合车辆通信号
        List<String> autoTerminal = cars.stream().map(TripCarListDto :: getAutoTerminal).collect(Collectors.toList());

        // 查询行程
        CarTripPojo carTripPojo = getTripList(autoTerminal, form.getQueryTime(), 999999, 1);
        List<CarTripListPojo> list = new ArrayList<>();
        if (carTripPojo != null && carTripPojo.getData() != null){
            list = carTripPojo.getData();
        }
        Map<String, List<CarTripListPojo>> map = new HashMap<>(autoTerminal.size());
        if (list.size() > 0){
            map = list.stream().collect(Collectors.groupingBy(CarTripListPojo::getTerminalId));
        }

        //整合车辆信息和行程信息
        String vin;
        for (TripCarListDto tripCarListDto : cars){
            List<CarTripListPojo> carTripListPojos = map.get(tripCarListDto.getAutoTerminal());
            // 按车聚合数据
            if (carTripListPojos != null && carTripListPojos.size() > 0){
                BigDecimal mileage = BigDecimal.ZERO;
                BigDecimal oil = BigDecimal.ZERO;
                int score = 0;
                for (CarTripListPojo carTripListPojo : carTripListPojos){
                    BigDecimal.valueOf(carTripListPojo.getOil()).setScale(1, BigDecimal.ROUND_HALF_UP);
                    mileage = mileage.add(BigDecimal.valueOf(carTripListPojo.getMileage() / 1000D).setScale(1, BigDecimal.ROUND_HALF_UP));
                    oil = oil.add(BigDecimal.valueOf(carTripListPojo.getOil()).setScale(1, BigDecimal.ROUND_HALF_UP));
                    score += carTripListPojo.getScore();
                }

                tripCarListDto.setTripCount(String.valueOf(carTripListPojos.size()));
                tripCarListDto.setTripTotalMileage(mileage.toString());
                tripCarListDto.setTripTotalOil(doubleFormat.format(oil));
                tripCarListDto.setTripTotalMileageBySort(mileage.doubleValue());
                tripCarListDto.setAvgScore(score / carTripListPojos.size());
            }else {
                tripCarListDto.setTripCount("0");
                tripCarListDto.setTripTotalMileage("0.0");
                tripCarListDto.setTripTotalOil("0.0");
                tripCarListDto.setTripTotalMileageBySort(0.0);
                tripCarListDto.setAvgScore(0);
            }

            //vin后8位
            vin = tripCarListDto.getVin() == null ? "" : tripCarListDto.getVin();
            //vin展示底盘号后8位
            if (StringUtil.isNotEmpty(vin) && vin.length() > 8){
                tripCarListDto.setVin(vin.substring(vin.length() - 8));
            }else {
                tripCarListDto.setVin(vin);
            }
        }
        //按总里程倒叙排序
        cars = cars.stream().sorted(Comparator.comparing(TripCarListDto::getTripTotalMileageBySort).reversed()).collect(Collectors.toList());
        return cars;
    }

    /**
     * 行程信息查询  位置云
     * @param autoTerminal
     * @param queryTime
     * @param page_size
     * @param page_number
     * @return
     * @throws Exception
     */
    private CarTripPojo getTripList(List<String> autoTerminal, String queryTime, int page_size, int page_number) throws Exception{
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
        CarTripPojo carTripPojo = HttpUtil.postJsonRequest(getTripAnalysisList, map, new TypeReference<HttpCommandResultWithData<CarTripPojo>>() {});
        return carTripPojo;
    }

    /**
     * 给定日期，封装成Date格式
     */
    private Date parseDate(String date, String pattern) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.parse(date);
    }
}
