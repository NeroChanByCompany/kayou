package com.nut.tripanalysis.app.service.impl;

import com.nut.common.utils.DateUtil;
import com.nut.common.utils.StringUtil;
import com.nut.tripanalysis.app.dao.*;
import com.nut.tripanalysis.app.dto.AvgOilwearRankingDto;
import com.nut.tripanalysis.app.form.*;
import com.nut.tripanalysis.app.pojo.OilwearRankingPojo;
import com.nut.tripanalysis.app.pojo.RankCarPojo;
import com.nut.tripanalysis.app.service.CommonService;
import com.nut.tripanalysis.app.service.RankingService;
import com.nut.truckingteam.app.dto.CarOilWearDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/*
 *  @author wuhaotian 2021/7/10
 */
@Service("RankingService")
@Slf4j
public class RankingServiceImpl implements RankingService {

    @Autowired
    private RankMonthDao rankMonthEntityMapper;
    @Autowired
    private RankYesterdayDao rankYesterdayEntityMapper;
    /** 油耗排行类型*/
    private static final String AVG_OIL_WEEK_RANKING = "week";
    /** 油耗排行类型*/
    private static final String AVG_OIL_MONTH_RANKING = "month";
    /** 油耗排行类型*/
    private static final String AVG_OIL_YESTERDAY_RANKING = "yesterday";
    @Autowired
    private CommonService commonService;
    @Autowired
    private CarAnalyseDao carAnalyseEntityMapper;
    @Autowired
    private RankWeekDao rankWeekEntityMapper;

    @Autowired
    private CarDao carEntityMapper;
    /**
     * 阈值 （1.9.1需求：平均油耗大于60L/100km时，前端处理成显示18.8L/100km）
     */
    @Value("${magicNumber.avgOil.limit:60}")
    private double avgOilLimit;

    /**
     * 假值 （1.9.1需求：平均油耗大于60L/100km时，前端处理成显示18.8L/100km）
     */
    @Value("${magicNumber.avgOil:18.8}")
    private double avgOilMagicNumber;

    /**
     * 阈值 （2.1.2需求：平均油耗低于10L/100km时，前端处理成显示16.6L/100km）
     */
    @Value("${magicNumber.avgOilMini.limit:10}")
    private double avgOilMiniLimit;

    /**
     * 假值 （2.1.2需求：平均油耗低于10L/100km时，前端处理成显示16.6L/100km）
     */
    @Value("${magicNumber.avgOilMini:16.6}")
    private double avgOilMiniMagicNumber;

    @Override
    public List<AvgOilwearRankingDto> queryMonthOilWear(QueryMonthAvgOilWearForm command) {
        //判断是获取司机版当前车辆油耗还是车队版当前车队的油耗
        List<OilwearRankingPojo> list = getModelAndCars(command.getModelId(), command.getToken());
        if (list != null && list.size() > 0) {
            List<OilwearRankingPojo> pojos = new ArrayList<>();
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.MONTH, -1);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            //取月1号0点
            long moZero = DateUtil.getZeroTime(cal.getTime().getTime());
            pojos = rankMonthEntityMapper.getOilwearByCarId(list, moZero);
            //获取某月最大天数
            int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            //设置日历中月份的最大天数
            cal.set(Calendar.DAY_OF_MONTH, lastDay);
            return unionDtos(list, pojos, moZero, DateUtil.getZeroTime(cal.getTime().getTime()),AVG_OIL_MONTH_RANKING);
        }
        return new ArrayList<>();
    }
    /**
     * 获取车型和车辆列表私有方法
     */
    private List<OilwearRankingPojo> getModelAndCars(String modelId, String token) {
        List<OilwearRankingPojo> returnList = new ArrayList<>();
        // 获取用户下所有车辆
        List<CarOilWearDto> cars = commonService.callGetTeamCarsOilWear(modelId, token);
        if (cars != null && cars.size() > 0) {
            for (CarOilWearDto car : cars) {
                String modelName = car.getModelName();
                if (StringUtil.isNotEmpty(car.getSeriseName())) {
                    modelName = car.getSeriseName() + " " + modelName;
                }
                OilwearRankingPojo oilwearRankingPojo = new OilwearRankingPojo(car.getCarId(), car.getCarNumber(), "", modelName, car.getModelId());
                returnList.add(oilwearRankingPojo);
            }
        }
        return returnList;
    }
    /**
     * 合并结果组成dto
     */
    private List<AvgOilwearRankingDto> unionDtos(List<OilwearRankingPojo> allCars, List<OilwearRankingPojo> rankCars, Long start, Long end, String type) {
        List<AvgOilwearRankingDto> dtos = new ArrayList<>();
        String brand = "";
        String modelName = "";
        if (allCars != null && allCars.size() > 0) {
            brand = allCars.get(0).getCarBrand();
            modelName = allCars.get(0).getCarModel();
        } else {
            return dtos;
        }
        Map<String, AvgOilwearRankingDto> map = new HashMap<>();
        for (OilwearRankingPojo pojo : allCars) {
            map.put(pojo.getCarId(), new AvgOilwearRankingDto(pojo.getCarId(), pojo.getCarNumber(), pojo.getCarBrand(), pojo.getCarModel(), pojo.getModelId(), pojo.getAvgOilwear(), pojo.getRanking(), pojo.getPercentage()));
        }
        for (OilwearRankingPojo pojo : rankCars) {
            map.put(pojo.getCarId(), new AvgOilwearRankingDto(pojo.getCarId(), map.get(pojo.getCarId()).getCarNumber(), pojo.getCarBrand(), pojo.getCarModel(), pojo.getCarModel(), pojo.getAvgOilwear(), pojo.getRanking(), pojo.getPercentage()));
        }
        Iterator it = map.keySet().iterator();
        // 是否进排行榜
        boolean rankFlg = false;
        while (it.hasNext()) {
            String key = it.next().toString();
            AvgOilwearRankingDto dto = map.get(key);
            //补充品牌和车型
            dto.setCarModel(modelName);
            dto.setCarBrand(brand);
            if (dto.getPercentage() == -1) {
                //未进排行榜的车辆去固化表取它的百公里平均油耗
                Double avgoil = carAnalyseEntityMapper.getAvgOilForRanking(key, start, end);
                if (avgoil != null && avgoil > 0) {
                    dto.setAvgOilwear(avgoil);
                }
            } else {
                rankFlg = true;
            }
            dtos.add(dto);
        }
        // 进排行榜，重新排序
        if (rankFlg) {
            getAvgOilWearRanking(dtos.get(0).getModelId(), type, dtos);
        }
        return dtos;
    }
    /**
     * @Description: 进排行榜，重新排序
     * @method: getAvgOilWearRanking
     * @param modelId
     * @param type
     * @param dtoList
     * @return void
     * @Date: 2018/11/15 13:14
     * @authur: jiangcm
     */
    public void getAvgOilWearRanking(String modelId, String type, List<AvgOilwearRankingDto> dtoList) {
        List<AvgOilwearRankingDto> rankDtoList;
        boolean carNumberHideFlg = false;
        if (AVG_OIL_YESTERDAY_RANKING.equals(type)) {
            QueryYesterdayAvgOilWearRankingForm command = new QueryYesterdayAvgOilWearRankingForm();
            command.setModelId(modelId);
            rankDtoList = queryYesterdayOilWearRanking(command,carNumberHideFlg);
        } else if (AVG_OIL_WEEK_RANKING.equals(type)) {
            QueryWeekAvgOilWearRankingForm command = new QueryWeekAvgOilWearRankingForm();
            command.setModelId(modelId);
            rankDtoList = queryWeekAvgOilWearRanking(command,carNumberHideFlg);
        } else {
            QueryMonthAvgOilWearRankingForm command = new QueryMonthAvgOilWearRankingForm();
            command.setModelId(modelId);
            rankDtoList = queryMonthOilWearRanking(command,carNumberHideFlg);
        }
        if (rankDtoList != null && !rankDtoList.isEmpty()) {
            for (AvgOilwearRankingDto rankingDto : rankDtoList) {
                for (AvgOilwearRankingDto dto : dtoList) {
                    if (dto.getCarId().equals(rankingDto.getCarId())) {
                        dto.setPercentage(rankingDto.getPercentage());
                        dto.setRanking(rankingDto.getRanking());
                        break;
                    }
                }
            }
        }
    }
    /**
     * 昨日油耗排行榜(top10)
     *
     * @param command
     * @return
     * @throws ParseException
     */
    @Override
    public List<AvgOilwearRankingDto> queryYesterdayOilWearRanking(QueryYesterdayAvgOilWearRankingForm command, boolean carNumberHideFlg) {
        String modelid = command.getModelId();
        //取昨天0点
        long moZero = DateUtil.getZeroTime(System.currentTimeMillis() - 86400000L);
        List<AvgOilwearRankingDto> pojos = rankYesterdayEntityMapper.avgOilwear4YesterdayRanking(modelid, moZero);

        return getRanking(modelid, pojos, carNumberHideFlg, command.getToken());
    }
    /**
     * 上周油耗排行榜(top10)
     *
     * @param command
     * @return
     * @throws ParseException
     */
    @Override
    public List<AvgOilwearRankingDto> queryWeekAvgOilWearRanking(QueryWeekAvgOilWearRankingForm command, boolean carNumberHideFlg) {
        String modelid = command.getModelId();
        Date date = new Date(System.currentTimeMillis() - 86400000L * 7);
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        //取上周一0点
        long moZero = DateUtil.getZeroTime(cal.getTime().getTime());
        List<AvgOilwearRankingDto> pojos = rankWeekEntityMapper.avgOilwear4WeekRanking(modelid, moZero);

        return getRanking(modelid, pojos,carNumberHideFlg, command.getToken());
    }

    @Override
    public List<AvgOilwearRankingDto> queryYesterdayOilWear(QueryYesterdayAvgOilWearForm command) {
        //判断是获取司机版当前车辆油耗还是车队版当前车队的油耗
        List<OilwearRankingPojo> list = getModelAndCars(command.getModelId(), command.getToken());
        if (list != null && list.size() > 0) {
            List<OilwearRankingPojo> pojos = new ArrayList<>();
            //取昨天0点
            long moZero = DateUtil.getZeroTime(System.currentTimeMillis() - 86400000L);
            pojos = rankYesterdayEntityMapper.getOilwearByCarId(list, moZero);
            return unionDtos(list, pojos, moZero, moZero,AVG_OIL_YESTERDAY_RANKING);
        }
        return new ArrayList<>();
    }

    /**
     * 上月油耗排行榜(top10)
     *
     * @param command
     * @return
     * @throws ParseException
     */
    public List<AvgOilwearRankingDto> queryMonthOilWearRanking(QueryMonthAvgOilWearRankingForm command, boolean carNumberHideFlg) {
        String modelid = command.getModelId();
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MONTH, -1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        //取月1号0点
        long moZero = DateUtil.getZeroTime(cal.getTime().getTime());
        List<AvgOilwearRankingDto> pojos = rankMonthEntityMapper.avgOilwear4MonthRanking(modelid, moZero);

        return getRanking(modelid, pojos,carNumberHideFlg, command.getToken());
    }

    @Override
    public List<AvgOilwearRankingDto> queryWeekAvgOilWear(QueryWeekAvgOilWearForm command) {
        //判断是获取司机版当前车辆油耗还是车队版当前车队的油耗
        List<OilwearRankingPojo> list = getModelAndCars(command.getModelId(), command.getToken());
        if (list != null && list.size() > 0) {
            List<OilwearRankingPojo> pojos = new ArrayList<>();
            Date date = new Date(System.currentTimeMillis() - 86400000L * 7);
            Calendar cal = Calendar.getInstance();
            cal.setFirstDayOfWeek(Calendar.MONDAY);
            cal.setTime(date);
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            //取上周一0点
            long moZero = DateUtil.getZeroTime(cal.getTime().getTime());
            pojos = rankWeekEntityMapper.getOilwearByCarId(list, moZero);
            return unionDtos(list, pojos, moZero, moZero + 86400000L * 6, AVG_OIL_WEEK_RANKING);
        }
        return new ArrayList<>();
    }

    /**
     * 车牌号隐私处理及我的车辆标记
     * @param modelId
     * @param pojos
     * @param carNumberHideFlg
     * @param token
     * @return
     */
    private List<AvgOilwearRankingDto> getRanking(String modelId, List<AvgOilwearRankingDto> pojos,boolean carNumberHideFlg, String token) {
        //车牌号隐私处理
        if (pojos != null && !pojos.isEmpty() && carNumberHideFlg) {

            Long userId = commonService.getUserId(token);
            //获取用户下所有车辆
            List<RankCarPojo> cars = carEntityMapper.queryRankCarsByUserId(userId);
            Map<String, RankCarPojo> carsMap = cars.stream().collect(Collectors.toMap(RankCarPojo::getCarId, a -> a, (k1, k2) -> k1));

            String vin;
            for (AvgOilwearRankingDto avgOilwearRankingDto : pojos) {
                //处理底盘号
                vin = avgOilwearRankingDto.getVin() == null ? "" : avgOilwearRankingDto.getVin();
                if (StringUtil.isNotEmpty(vin) && vin.length() > 8) {
                    //此处先不做隐私处理
                    avgOilwearRankingDto.setVin(vin.substring(vin.length() - 8));
                }
                if (carsMap.containsKey(avgOilwearRankingDto.getCarId())) {
                    avgOilwearRankingDto.setMyCar("1");
                } else {
                    //不是本车队的车辆进行隐私处理
                    avgOilwearRankingDto.setCarNumber(dealCarNum(avgOilwearRankingDto.getCarNumber()));
                    avgOilwearRankingDto.setVin(avgOilwearRankingDto.getVin());
                    avgOilwearRankingDto.setMyCar("0");
                }
            }
        }
        // 平均油耗高到低
        pojos = pojos.stream().sorted(Comparator.comparing(AvgOilwearRankingDto::getAvgOilwear)).collect(Collectors.toList());
        int ranking = 0;
        for (AvgOilwearRankingDto avgOilwearRankingDto : pojos) {
            avgOilwearRankingDto.setRanking(++ranking);
        }
        // 百分比
        if (StringUtil.isNotEmpty(modelId)){
            int count = carEntityMapper.queryCountByCarModel(Arrays.asList( modelId.split(",")));
            if (count > 0 && count > pojos.size()){
                for (int i = 0; i < pojos.size(); i++) {
                    AvgOilwearRankingDto avgOilwearRankingDto = pojos.get(i);
                    int rank = avgOilwearRankingDto.getRanking();
                    avgOilwearRankingDto.setPercentage((int)Math.floor((count - rank + 0.0D) / count * 100));
                }
            }
        }
        return pojos;
    }
    public String dealCarNum(String content) {
        if (content == null || content.length() < 6) {
            return "*******";
        }
        String starStr = "";
        for (int i = 0; i < (content.length() - 3); i++) {
            starStr = starStr + "*";
        }
        return content.substring(0, 2) + starStr
                + content.substring(content.length() - 1, content.length());
    }
    public int compareTo(AvgOilwearRankingDto o1, AvgOilwearRankingDto o2) {
        Double avgOilwear1 = magicNum(o1.getAvgOilwear());
        Double avgOilwear2 = magicNum(o2.getAvgOilwear());
        // 相同平均油耗的，就按照行驶里程来排
        if (avgOilwear1.equals(avgOilwear2)) {
            // 里程降序排
            return o2.getMileage().compareTo(o1.getMileage());
        } else {
            // 油耗升序排
            return avgOilwear1.compareTo(avgOilwear2);
        }
    }
    private Double magicNum(Double a) {
        Double d = avoidNull(a);
        if (d > avgOilLimit) {
            d = avgOilMagicNumber;
        } else if (d < avgOilMiniLimit){
            d = avgOilMiniMagicNumber;
        }
        return d;
    }
    /**
     * Float null -> 0
     */
    private Double avoidNull(Double a) {
        if (a == null) {
            a = 0d;
        }
        return a;
    }
}
