package com.nut.driver.app.service.impl;

import com.nut.driver.app.dao.FltFleetCarMappingDao;
import com.nut.driver.app.dao.StatisticalDao;
import com.nut.driver.app.entity.FltFleetCarMappingEntity;
import com.nut.driver.app.form.StatisticsFleetLineReportForm;
import com.nut.driver.app.form.StatisticsFleetReportForm;
import com.nut.driver.app.service.StatisticsFleetService;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.PagingInfo;
import com.nut.common.utils.DateUtil;
import com.nut.common.utils.JsonUtil;
import com.nut.common.utils.StringUtil;
import com.nut.driver.app.dto.*;
import com.nut.driver.app.pojo.*;
import com.nut.driver.app.service.StatisticsService;
import com.nut.driver.common.utils.PageUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service("StatisticsFleetService")
@Slf4j
public class StatisticsFleetServiceImpl implements StatisticsFleetService {

    @Autowired
    private StatisticsService statisticsService;
    @Autowired
    private StatisticalDao statisticalMapper;
    @Autowired
    private PageUtil pageUtil;

    @Autowired
    FltFleetCarMappingDao fltFleetCarMappingDao;

    @Override
    public HttpCommandResultWithData fleetReport(StatisticsFleetReportForm command) throws Exception{
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        // 查询时间校验
        String checkResult = statisticsService.checkQueryTime(command.getBeginDate(), command.getEndDate());
        if (StringUtil.isNotEmpty(checkResult)){
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage(checkResult);
            return result;
        }
        // 查询用户车队及车对下的车辆
        List<FltStatisticalFleetPojo> list = queryCars(command.getAutoIncreaseId(), command.getFleetName());
        if (list.size() == 0){
            FleetReportBaseDto fleetReportBaseDto = new FleetReportBaseDto();
            fleetReportBaseDto.setFleetTotal(0);
            result.setData(fleetReportBaseDto);
            return result;
        }

        List<FltStatisticalFleetDataPojo> sameDayList = new ArrayList<>();
        List<FltStatisticalFleetDataPojo> nonSameDayList = new ArrayList<>();

        // 包含当天日期
        if (DateUtil.diffNowDate(command.getEndDate()) == 3){
            List<String> autoTerminals = getAautoTerminals(list);
            if (autoTerminals.size() > 0){
                sameDayList = queryStatisticalOfMilAndFuelFromLocationCloud(autoTerminals);
            }
        }

        // 非当天日期
        if (DateUtil.diffNowDate(command.getBeginDate()) == 2){
            // 过滤全部车队ID
            List<Long> fleetIds = list.stream().map(FltStatisticalFleetPojo::getTeamId).distinct().collect(Collectors.toList());
            nonSameDayList = statisticalMapper.queryStatisticalByTeamId(DateUtil.dateFormatConversion(command.getBeginDate(), DateUtil.date_s_pattern), DateUtil.dateFormatConversion(command.getEndDate(), DateUtil.date_s_pattern), fleetIds);
            this.setTeamName(nonSameDayList);
        }

        // 将当日数据按通信号转为MAP
        Map<String, FltStatisticalFleetDataPojo> sameDayMap = new HashMap<>();
        if (sameDayList.size() > 0){
            sameDayMap = sameDayList.stream().collect(Collectors.toMap(FltStatisticalFleetDataPojo::getAutoTerminal, Function.identity()));
        }

        // 将非当日数据按车队ID转为MAP
        Map<Long, FltStatisticalFleetDataPojo> nonSameDayMap = new HashMap<>();
        if (nonSameDayList.size() > 0){
            nonSameDayMap = nonSameDayList.stream().collect(Collectors.toMap(FltStatisticalFleetDataPojo::getTeamId, Function.identity()));
        }

        // 按车队合并数据
        List<FltStatisticalFleetDataPojo> allList = new ArrayList<>();
        FltStatisticalFleetDataPojo fsfdp;
        for (FltStatisticalFleetPojo f : list){
            fsfdp = new FltStatisticalFleetDataPojo();
            fsfdp.setTeamName(f.getTeamName());
            fsfdp.setTeamId(f.getTeamId());
            String ats = f.getAutoTerminals();
            if (StringUtil.isNotEmpty(ats)){
                String[] atss = ats.split(",");
                // 车辆是否有当日数据
                for (String s : atss){
                    FltStatisticalFleetDataPojo fs = sameDayMap.get(s);
                    if (fs != null){
                        fsfdp.setMileage(fsfdp.getMileage() + fs.getMileage());
                        fsfdp.setOilwear(fsfdp.getOilwear() + fs.getOilwear());
                        fsfdp.setRunOil(fsfdp.getRunOil() + fs.getRunOil());
                        fsfdp.setIdlingOil(fsfdp.getIdlingOil() + fs.getIdlingOil());
                        fsfdp.setTimeTotal(fsfdp.getTimeTotal() + fs.getTimeTotal());
                        fsfdp.setIdleTime(fsfdp.getIdleTime() + fs.getIdleTime());
                        fsfdp.setSpeedSum(fsfdp.getSpeedSum() + fs.getSpeedSum());
                        fsfdp.setSpeedCount(fsfdp.getSpeedCount() + fs.getSpeedCount());
                        // 不良驾驶总次数
                        fsfdp.setTotalCount(fsfdp.getTotalCount()
                                + fs.getOverSpeedCnt()
                                + fs.getRaCnt()
                                + fs.getRdCnt()
                                + fs.getSharpTurnCnt()
                                + fs.getIdleTimeoutCnt()
                                + fs.getColdRunCnt()
                                + fs.getNightRunCnt()
                                + fs.getLowGrHighSpdCnt()
                                + fs.getFullThrottleCnt()
                                + fs.getRoughThrottleCnt()
                                + fs.getNeutralGearCoastCnt()
                                + fs.getStallCoastCnt()
                        );
                    }
                }
            }
            // 此车队在非当日数据中是否有数据
            FltStatisticalFleetDataPojo noFs = nonSameDayMap.get(f.getTeamId());
            if (noFs != null){
                fsfdp.setMileage(fsfdp.getMileage() + noFs.getMileage());
                fsfdp.setOilwear(fsfdp.getOilwear() + noFs.getOilwear());
                fsfdp.setRunOil(fsfdp.getRunOil() + noFs.getRunOil());
                fsfdp.setIdlingOil(fsfdp.getIdlingOil() + noFs.getIdlingOil());
                fsfdp.setTimeTotal(fsfdp.getTimeTotal() + noFs.getTimeTotal());
                fsfdp.setIdleTime(fsfdp.getIdleTime() + noFs.getIdleTime());
                fsfdp.setSpeedSum(fsfdp.getSpeedSum() + noFs.getSpeedSum());
                fsfdp.setSpeedCount(fsfdp.getSpeedCount() + noFs.getSpeedCount());
                // 不良驾驶总次数
                fsfdp.setTotalCount(fsfdp.getTotalCount()
                        + noFs.getOverSpeedCnt()
                        + noFs.getRaCnt()
                        + noFs.getRdCnt()
                        + noFs.getSharpTurnCnt()
                        + noFs.getIdleTimeoutCnt()
                        + noFs.getColdRunCnt()
                        + noFs.getNightRunCnt()
                        + noFs.getLowGrHighSpdCnt()
                        + noFs.getFullThrottleCnt()
                        + noFs.getRoughThrottleCnt()
                        + noFs.getNeutralGearCoastCnt()
                        + noFs.getStallCoastCnt()
                );
            }
            allList.add(fsfdp);
        }
        // 分项统计
        FleetReportBaseDto frbd = new FleetReportBaseDto();
        frbd.setFleetTotal(list.size());
        if (allList.size() > 0){
            // 数据统计时间范围
            long dateInterval = DateUtil.diffByDay(command.getBeginDate(), command.getEndDate(), "") + 1;
            log.info("车队数据：{}", JsonUtil.toJson(list));
            Map<Long, FltStatisticalFleetPojo> teamMap = list.stream().collect(Collectors.toMap(FltStatisticalFleetPojo::getTeamId, Function.identity()));
            // 不良驾驶总次数
            int badSum = allList.stream().mapToInt(FltStatisticalFleetDataPojo::getTotalCount).sum();

            // 里程
            List<FleetReportMileageDto> mList = new ArrayList<>();
            FleetReportMileageDto frmd;
            // 油耗
            List<FleetReportOilDto> oList = new ArrayList<>();
            FleetReportOilDto frod;
            // 时长
            List<FleetReportDrivingDto> dList = new ArrayList<>();
            FleetReportDrivingDto frdd;
            //不良驾驶行为
            List<FleetReportBadDrivingDto> bList = new ArrayList<>();
            FleetReportBadDrivingDto frbdd;
            for (FltStatisticalFleetDataPojo f : allList){
                // 车队下车辆数
                String autoTerminals = teamMap.get(f.getTeamId()).getAutoTerminals();
                int carNum = 0;
                if (StringUtil.isNotEmpty(autoTerminals)){
                    carNum = autoTerminals.split(",").length;
                }

                frmd = new FleetReportMileageDto();
                frod = new FleetReportOilDto();
                frdd = new FleetReportDrivingDto();
                frbdd = new FleetReportBadDrivingDto();

                // 里程相关
                frmd.setFleetName(f.getTeamName());
                frmd.setTotalMileage(new BigDecimal(f.getMileage()).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue());
                if (carNum == 0){
                    frmd.setAvgMileage("0.0");
                }else {
                    frmd.setAvgMileage(new BigDecimal(f.getMileage() / dateInterval / carNum).setScale(1, BigDecimal.ROUND_HALF_UP).toString());
                }
                if (f.getSpeedCount() == 0){
                    frmd.setAvgSpeed("0.0");
                }else {
                    frmd.setAvgSpeed(new BigDecimal(f.getSpeedSum() / f.getSpeedCount()).setScale(1, BigDecimal.ROUND_HALF_UP).toString());
                }
                mList.add(frmd);

                // 油耗相关
                frod.setFleetName(f.getTeamName());
                frod.setTotalOil(new BigDecimal(f.getOilwear()).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue());
                if (f.getMileage() == 0){
                    frod.setAvgOil("0.0");
                }else {
                    frod.setAvgOil(new BigDecimal(f.getOilwear() * 100 / f.getMileage()).setScale(1, BigDecimal.ROUND_HALF_UP).toString());
                }
                frod.setIdlOil(new BigDecimal(f.getIdlingOil()).setScale(1, BigDecimal.ROUND_HALF_UP).toString());
                oList.add(frod);

                // 时长相关
                frdd.setFleetName(f.getTeamName());
                BigDecimal tb = new BigDecimal(f.getTimeTotal() / 3600000.0D).setScale(1, BigDecimal.ROUND_HALF_UP);
                BigDecimal ib = new BigDecimal(f.getIdleTime() / 3600000.0D).setScale(1, BigDecimal.ROUND_HALF_UP);
                frdd.setTotalTime(tb.doubleValue());
                frdd.setIdlTime(ib.toString());
                frdd.setDrivingTime(tb.subtract(ib).toString());
                dList.add(frdd);
                // 不良驾驶行为相关
                frbdd.setFleetName(f.getTeamName());
                frbdd.setTotalNumber(f.getTotalCount());
                if (badSum == 0){
                    frbdd.setProportion("0.0");
                }else {
                    frbdd.setProportion(new BigDecimal(f.getTotalCount() * 100).divide(new BigDecimal(badSum),1, BigDecimal.ROUND_HALF_UP).toString());
                }
                bList.add(frbdd);
            }
            // 排序
            if (mList.size() > 0){
                mList = mList.stream().sorted(Comparator.comparing(FleetReportMileageDto::getTotalMileage).reversed())
                        .collect(Collectors.toList());
            }
            if (oList.size() > 0){
                oList = oList.stream().sorted(Comparator.comparing(FleetReportOilDto::getTotalOil).reversed())
                        .collect(Collectors.toList());
            }
            if (dList.size() > 0){
                dList = dList.stream().sorted(Comparator.comparing(FleetReportDrivingDto::getTotalTime).reversed())
                        .collect(Collectors.toList());
            }
            if (bList.size() > 0){
                bList = bList.stream().sorted(Comparator.comparing(FleetReportBadDrivingDto::getTotalNumber).reversed())
                        .collect(Collectors.toList());
            }
            // 取前5名
            if (mList.size() > 5){
                frbd.setMileageList(mList.subList(0, 5));
            }else {
                frbd.setMileageList(mList);
            }
            if (oList.size() > 5){
                frbd.setOilList(oList.subList(0, 5));
            }else {
                frbd.setOilList(oList);
            }
            if (dList.size() > 5){
                frbd.setDrivingList(dList.subList(0, 5));
            }else {
                frbd.setDrivingList(dList);
            }
            if (bList.size() > 5){
                frbd.setBadDrivingList(bList.subList(0, 5));
            }else {
                frbd.setBadDrivingList(bList);
            }
        }
         result.setData(frbd);
        return result;
    }

    @Override
    public HttpCommandResultWithData fleetMileageReport(StatisticsFleetReportForm command) throws Exception{
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        // 查询时间校验
        String checkResult = statisticsService.checkQueryTime(command.getBeginDate(), command.getEndDate());
        if (StringUtil.isNotEmpty(checkResult)){
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage(checkResult);
            return result;
        }
        // 查询用户车队及车对下的车辆
        List<FltStatisticalFleetPojo> list = queryCars(command.getAutoIncreaseId(), command.getFleetName());
        if (list.size() == 0){
            return result;
        }

        List<FltStatisticalFleetDataPojo> sameDayList = new ArrayList<>();
        List<FltStatisticalFleetDataPojo> nonSameDayList = new ArrayList<>();

        // 包含当天日期
        if (DateUtil.diffNowDate(command.getEndDate()) == 3){
            List<String> autoTerminals = getAautoTerminals(list);
            if (autoTerminals.size() > 0){
                sameDayList = queryStatisticalOfMilAndFuelFromLocationCloud(autoTerminals);
            }
        }

        // 非当天日期
        if (DateUtil.diffNowDate(command.getBeginDate()) == 2){
            // 过滤全部车队ID
            List<Long> fleetIds = list.stream().map(FltStatisticalFleetPojo::getTeamId).distinct().collect(Collectors.toList());
            nonSameDayList = statisticalMapper.queryStatisticalByTeamId(DateUtil.dateFormatConversion(command.getBeginDate(), DateUtil.date_s_pattern), DateUtil.dateFormatConversion(command.getEndDate(), DateUtil.date_s_pattern), fleetIds);
            this.setTeamName(nonSameDayList);
        }

        // 将当日数据按通信号转为MAP
        Map<String, FltStatisticalFleetDataPojo> sameDayMap = new HashMap<>();
        if (sameDayList.size() > 0){
            sameDayMap = sameDayList.stream().collect(Collectors.toMap(FltStatisticalFleetDataPojo::getAutoTerminal, Function.identity()));
        }

        // 将非当日数据按车队ID转为MAP
        Map<Long, FltStatisticalFleetDataPojo> nonSameDayMap = new HashMap<>();
        if (nonSameDayList.size() > 0){
            nonSameDayMap = nonSameDayList.stream().collect(Collectors.toMap(FltStatisticalFleetDataPojo::getTeamId, Function.identity()));
        }

        // 按车队合并数据
        List<FltStatisticalFleetDataPojo> allList = new ArrayList<>();
        FltStatisticalFleetDataPojo fsfdp;
        for (FltStatisticalFleetPojo f : list){
            fsfdp = new FltStatisticalFleetDataPojo();
            fsfdp.setTeamName(f.getTeamName());
            fsfdp.setTeamId(f.getTeamId());
            String ats = f.getAutoTerminals();
            if (StringUtil.isNotEmpty(ats)){
                String[] atss = ats.split(",");
                // 车辆是否有当日数据
                for (String s : atss){
                    FltStatisticalFleetDataPojo fs = sameDayMap.get(s);
                    if (fs != null){
                        fsfdp.setMileage(fsfdp.getMileage() + fs.getMileage());
                        fsfdp.setSpeedSum(fsfdp.getSpeedSum() + fs.getSpeedSum());
                        fsfdp.setSpeedCount(fsfdp.getSpeedCount() + fs.getSpeedCount());
                    }
                }
            }
            // 此车队在非当日数据中是否有数据
            FltStatisticalFleetDataPojo noFs = nonSameDayMap.get(f.getTeamId());
            if (noFs != null){
                fsfdp.setMileage(fsfdp.getMileage() + noFs.getMileage());
                fsfdp.setSpeedSum(fsfdp.getSpeedSum() + noFs.getSpeedSum());
                fsfdp.setSpeedCount(fsfdp.getSpeedCount() + noFs.getSpeedCount());
            }
            allList.add(fsfdp);
        }

        // 里程统计
        List<FleetReportMileageDto> mList = new ArrayList<>();

        if (allList.size() > 0){
            // 数据统计时间范围
            long dateInterval = DateUtil.diffByDay(command.getBeginDate(), command.getEndDate(), "") + 1;
            Map<Long, FltStatisticalFleetPojo> teamMap = list.stream().collect(Collectors.toMap(FltStatisticalFleetPojo::getTeamId, Function.identity()));
            FleetReportMileageDto frmd;
            for (FltStatisticalFleetDataPojo f : allList){

                // 车队下车辆数
                FltStatisticalFleetPojo fsfp = teamMap.get(f.getTeamId());
                String autoTerminals = fsfp.getAutoTerminals();
                int carNum = 0;
                if (StringUtil.isNotEmpty(autoTerminals)){
                    carNum = autoTerminals.split(",").length;
                }
                frmd = new FleetReportMileageDto();

                // 里程相关
                frmd.setFleetName(fsfp.getTeamName());
                frmd.setCreator(fsfp.getCreator());
                frmd.setCarNumber(String.valueOf(carNum));
                frmd.setTotalMileage(new BigDecimal(f.getMileage()).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue());
                if (carNum == 0){
                    frmd.setAvgMileage("0.0");
                }else {
                    frmd.setAvgMileage(new BigDecimal(f.getMileage() / dateInterval / carNum).setScale(1, BigDecimal.ROUND_HALF_UP).toString());
                }
                if (f.getSpeedCount() == 0){
                    frmd.setAvgSpeed("0.0");
                }else {
                    frmd.setAvgSpeed(new BigDecimal(f.getSpeedSum() / f.getSpeedCount()).setScale(1, BigDecimal.ROUND_HALF_UP).toString());
                }
                mList.add(frmd);

            }
        }
        // 排序
        if (mList.size() > 0){
            mList = mList.stream().sorted(Comparator.comparing(FleetReportMileageDto::getTotalMileage).reversed())
                    .collect(Collectors.toList());
        }
        // 分页
        PagingInfo<FleetReportMileageDto> pagingInfo = new PagingInfo<>();
        if (StringUtil.isNotEq("1", command.getReturnAll())){
            // 分页
            mList = pageUtil.paging(mList, command.getPage_number(), command.getPage_size(), pagingInfo);
            pagingInfo.setList(mList);
        }else {
            pagingInfo.setList(mList);
            pagingInfo.setTotal(mList.size());
            pagingInfo.setPage_total(1);
        }
         result.setData(pagingInfo);
        return result;
    }

    @Override
    public HttpCommandResultWithData fleetOilReport(StatisticsFleetReportForm command) throws Exception {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        // 查询时间校验
        String checkResult = statisticsService.checkQueryTime(command.getBeginDate(), command.getEndDate());
        if (StringUtil.isNotEmpty(checkResult)){
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage(checkResult);
            return result;
        }
        // 查询用户车队及车对下的车辆
        List<FltStatisticalFleetPojo> list = queryCars(command.getAutoIncreaseId(), command.getFleetName());
        if (list.size() == 0){
            return result;
        }

        List<FltStatisticalFleetDataPojo> sameDayList = new ArrayList<>();
        List<FltStatisticalFleetDataPojo> nonSameDayList = new ArrayList<>();

        // 包含当天日期
        if (DateUtil.diffNowDate(command.getEndDate()) == 3){
            List<String> autoTerminals = getAautoTerminals(list);
            if (autoTerminals.size() > 0){
                sameDayList = queryStatisticalOfMilAndFuelFromLocationCloud(autoTerminals);
            }
        }

        // 非当天日期
        if (DateUtil.diffNowDate(command.getBeginDate()) == 2){
            // 过滤全部车队ID
            List<Long> fleetIds = list.stream().map(FltStatisticalFleetPojo::getTeamId).distinct().collect(Collectors.toList());
            nonSameDayList = statisticalMapper.queryStatisticalByTeamId(DateUtil.dateFormatConversion(command.getBeginDate(), DateUtil.date_s_pattern), DateUtil.dateFormatConversion(command.getEndDate(), DateUtil.date_s_pattern), fleetIds);
            this.setTeamName(nonSameDayList);
        }

        // 将当日数据按通信号转为MAP
        Map<String, FltStatisticalFleetDataPojo> sameDayMap = new HashMap<>();
        if (sameDayList.size() > 0){
            sameDayMap = sameDayList.stream().collect(Collectors.toMap(FltStatisticalFleetDataPojo::getAutoTerminal, Function.identity()));
        }

        // 将非当日数据按车队ID转为MAP
        Map<Long, FltStatisticalFleetDataPojo> nonSameDayMap = new HashMap<>();
        if (nonSameDayList.size() > 0){
            nonSameDayMap = nonSameDayList.stream().collect(Collectors.toMap(FltStatisticalFleetDataPojo::getTeamId, Function.identity()));
        }

        // 按车队合并数据
        List<FltStatisticalFleetDataPojo> allList = new ArrayList<>();
        FltStatisticalFleetDataPojo fsfdp;
        for (FltStatisticalFleetPojo f : list){
            fsfdp = new FltStatisticalFleetDataPojo();
            fsfdp.setTeamName(f.getTeamName());
            fsfdp.setTeamId(f.getTeamId());
            String ats = f.getAutoTerminals();
            if (StringUtil.isNotEmpty(ats)){
                String[] atss = ats.split(",");
                // 车辆是否有当日数据
                for (String s : atss){
                    FltStatisticalFleetDataPojo fs = sameDayMap.get(s);
                    if (fs != null){
                        fsfdp.setMileage(fsfdp.getMileage() + fs.getMileage());
                        fsfdp.setOilwear(fsfdp.getOilwear() + fs.getOilwear());
                        fsfdp.setIdlingOil(fsfdp.getIdlingOil() + fs.getIdlingOil());
                    }
                }
            }
            // 此车队在非当日数据中是否有数据
            FltStatisticalFleetDataPojo noFs = nonSameDayMap.get(f.getTeamId());
            if (noFs != null){
                fsfdp.setMileage(fsfdp.getMileage() + noFs.getMileage());
                fsfdp.setOilwear(fsfdp.getOilwear() + noFs.getOilwear());
                fsfdp.setIdlingOil(fsfdp.getIdlingOil() + noFs.getIdlingOil());
            }
            allList.add(fsfdp);
        }

        // 油耗统计
        List<FleetReportOilDto> oList = new ArrayList<>();
        if (allList.size() > 0){
            Map<Long, FltStatisticalFleetPojo> teamMap = list.stream().collect(Collectors.toMap(FltStatisticalFleetPojo::getTeamId, Function.identity()));

            FleetReportOilDto frod;
            for (FltStatisticalFleetDataPojo f : allList){
                // 车队下车辆数
                FltStatisticalFleetPojo fsfp = teamMap.get(f.getTeamId());
                String autoTerminals = fsfp.getAutoTerminals();
                int carNum = 0;
                if (StringUtil.isNotEmpty(autoTerminals)){
                    carNum = autoTerminals.split(",").length;
                }
                frod = new FleetReportOilDto();

                // 油耗相关
                frod.setFleetName(f.getTeamName());
                frod.setCreator(fsfp.getCreator());
                frod.setCarNumber(String.valueOf(carNum));
                frod.setTotalOil(new BigDecimal(f.getOilwear()).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue());
                if (f.getMileage() == 0){
                    frod.setAvgOil("0.0");
                }else {
                    frod.setAvgOil(new BigDecimal(f.getOilwear() * 100 / f.getMileage()).setScale(1, BigDecimal.ROUND_HALF_UP).toString());
                }
                frod.setIdlOil(new BigDecimal(f.getIdlingOil()).setScale(1, BigDecimal.ROUND_HALF_UP).toString());
                oList.add(frod);

            }
        }
        if (oList.size() > 0){
            oList = oList.stream().sorted(Comparator.comparing(FleetReportOilDto::getTotalOil).reversed())
                    .collect(Collectors.toList());
        }
        // 分页
        PagingInfo<FleetReportOilDto> pagingInfo = new PagingInfo<>();
        if (StringUtil.isNotEq("1", command.getReturnAll())){
            // 分页
            oList = pageUtil.paging(oList, command.getPage_number(), command.getPage_size(), pagingInfo);
            pagingInfo.setList(oList);
        }else {
            pagingInfo.setList(oList);
            pagingInfo.setTotal(oList.size());
            pagingInfo.setPage_total(1);
        }
        result.setData(pagingInfo);
        return  result;
    }

    @Override
    public HttpCommandResultWithData fleetTimeReport(StatisticsFleetReportForm command) throws Exception {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        // 查询时间校验
        String checkResult = statisticsService.checkQueryTime(command.getBeginDate(), command.getEndDate());
        if (StringUtil.isNotEmpty(checkResult)){
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage(checkResult);
            return result;
        }
        // 查询用户车队及车对下的车辆
        List<FltStatisticalFleetPojo> list =queryCars(command.getAutoIncreaseId(), command.getFleetName());
        if (list.size() == 0){
            return result;
        }

        List<FltStatisticalFleetDataPojo> sameDayList = new ArrayList<>();
        List<FltStatisticalFleetDataPojo> nonSameDayList = new ArrayList<>();

        // 包含当天日期
        if (DateUtil.diffNowDate(command.getEndDate()) == 3){
            List<String> autoTerminals = getAautoTerminals(list);
            if (autoTerminals.size() > 0){
                sameDayList = queryStatisticalOfMilAndFuelFromLocationCloud(autoTerminals);
            }
        }

        // 非当天日期
        if (DateUtil.diffNowDate(command.getBeginDate()) == 2){
            // 过滤全部车队ID
            List<Long> fleetIds = list.stream().map(FltStatisticalFleetPojo::getTeamId).distinct().collect(Collectors.toList());
            nonSameDayList = statisticalMapper.queryStatisticalByTeamId(DateUtil.dateFormatConversion(command.getBeginDate(), DateUtil.date_s_pattern), DateUtil.dateFormatConversion(command.getEndDate(), DateUtil.date_s_pattern), fleetIds);
            this.setTeamName(nonSameDayList);
        }

        // 将当日数据按通信号转为MAP
        Map<String, FltStatisticalFleetDataPojo> sameDayMap = new HashMap<>();
        if (sameDayList.size() > 0){
            sameDayMap = sameDayList.stream().collect(Collectors.toMap(FltStatisticalFleetDataPojo::getAutoTerminal, Function.identity()));
        }

        // 将非当日数据按车队ID转为MAP
        Map<Long, FltStatisticalFleetDataPojo> nonSameDayMap = new HashMap<>();
        if (nonSameDayList.size() > 0){
            nonSameDayMap = nonSameDayList.stream().collect(Collectors.toMap(FltStatisticalFleetDataPojo::getTeamId, Function.identity()));
        }
        // 按车队合并数据
        List<FltStatisticalFleetDataPojo> allList = new ArrayList<>();
        FltStatisticalFleetDataPojo fsfdp;
        for (FltStatisticalFleetPojo f : list){
            fsfdp = new FltStatisticalFleetDataPojo();
            fsfdp.setTeamName(f.getTeamName());
            fsfdp.setTeamId(f.getTeamId());
            String ats = f.getAutoTerminals();
            if (StringUtil.isNotEmpty(ats)){
                String[] atss = ats.split(",");
                // 车辆是否有当日数据
                for (String s : atss){
                    FltStatisticalFleetDataPojo fs = sameDayMap.get(s);
                    if (fs != null){
                        fsfdp.setTimeTotal(fsfdp.getTimeTotal() + fs.getTimeTotal());
                        fsfdp.setIdleTime(fsfdp.getIdleTime() + fs.getIdleTime());
                    }
                }
            }
            // 此车队在非当日数据中是否有数据
            FltStatisticalFleetDataPojo noFs = nonSameDayMap.get(f.getTeamId());
            if (noFs != null){
                fsfdp.setTimeTotal(fsfdp.getTimeTotal() + noFs.getTimeTotal());
                fsfdp.setIdleTime(fsfdp.getIdleTime() + noFs.getIdleTime());
            }
            allList.add(fsfdp);
        }

        // 时长统计
        List<FleetReportDrivingDto> dList = new ArrayList<>();
        if (allList.size() > 0){

            Map<Long, FltStatisticalFleetPojo> teamMap = list.stream().collect(Collectors.toMap(FltStatisticalFleetPojo::getTeamId, Function.identity()));

            FleetReportDrivingDto frdd;
            for (FltStatisticalFleetDataPojo f : allList){
                // 车队下车辆数
                FltStatisticalFleetPojo fsfp = teamMap.get(f.getTeamId());
                String autoTerminals = fsfp.getAutoTerminals();
                int carNum = 0;
                if (StringUtil.isNotEmpty(autoTerminals)){
                    carNum = autoTerminals.split(",").length;
                }

                frdd = new FleetReportDrivingDto();

                // 时长相关
                frdd.setFleetName(fsfp.getTeamName());
                frdd.setCreator(fsfp.getCreator());
                frdd.setCarNumber(String.valueOf(carNum));
                BigDecimal tb = new BigDecimal(f.getTimeTotal() / 3600000.0D).setScale(1, BigDecimal.ROUND_HALF_UP);
                BigDecimal ib = new BigDecimal(f.getIdleTime() / 3600000.0D).setScale(1, BigDecimal.ROUND_HALF_UP);
                frdd.setTotalTime(tb.doubleValue());
                frdd.setIdlTime(ib.toString());
                frdd.setDrivingTime(tb.subtract(ib).toString());
                dList.add(frdd);

            }
        }
        if (dList.size() > 0){
            dList = dList.stream().sorted(Comparator.comparing(FleetReportDrivingDto::getTotalTime).reversed())
                    .collect(Collectors.toList());
        }
        // 分页
        PagingInfo<FleetReportDrivingDto> pagingInfo = new PagingInfo<>();
        if (StringUtil.isNotEq("1", command.getReturnAll())){
            // 分页
            dList = pageUtil.paging(dList, command.getPage_number(), command.getPage_size(), pagingInfo);
            pagingInfo.setList(dList);
        }else {
            pagingInfo.setList(dList);
            pagingInfo.setTotal(dList.size());
            pagingInfo.setPage_total(1);
        }
        result.setData(pagingInfo);
        return result;
    }

    @Override
    public HttpCommandResultWithData fleetBadDrivingReport(StatisticsFleetReportForm command) throws Exception {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        // 查询时间校验
        String checkResult = statisticsService.checkQueryTime(command.getBeginDate(), command.getEndDate());
        if (StringUtil.isNotEmpty(checkResult)){
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage(checkResult);
            return result;
        }
        // 查询用户车队及车对下的车辆
        List<FltStatisticalFleetPojo> list = queryCars(command.getAutoIncreaseId(), command.getFleetName());
        if (list.size() == 0){
            return result;
        }

        List<FltStatisticalFleetDataPojo> sameDayList = new ArrayList<>();
        List<FltStatisticalFleetDataPojo> nonSameDayList = new ArrayList<>();

        // 包含当天日期
        if (DateUtil.diffNowDate(command.getEndDate()) == 3){
            List<String> autoTerminals = getAautoTerminals(list);
            if (autoTerminals.size() > 0){
                sameDayList = queryStatisticalOfMilAndFuelFromLocationCloud(autoTerminals);
            }
        }

        // 非当天日期
        if (DateUtil.diffNowDate(command.getBeginDate()) == 2){
            // 过滤全部车队ID
            List<Long> fleetIds = list.stream().map(FltStatisticalFleetPojo::getTeamId).distinct().collect(Collectors.toList());
            nonSameDayList = statisticalMapper.queryStatisticalByTeamId(DateUtil.dateFormatConversion(command.getBeginDate(), DateUtil.date_s_pattern), DateUtil.dateFormatConversion(command.getEndDate(), DateUtil.date_s_pattern), fleetIds);
            this.setTeamName(nonSameDayList);
        }

        // 将当日数据按通信号转为MAP
        Map<String, FltStatisticalFleetDataPojo> sameDayMap = new HashMap<>();
        if (sameDayList.size() > 0){
            sameDayMap = sameDayList.stream().collect(Collectors.toMap(FltStatisticalFleetDataPojo::getAutoTerminal, Function.identity()));
        }

        // 将非当日数据按车队ID转为MAP
        Map<Long, FltStatisticalFleetDataPojo> nonSameDayMap = new HashMap<>();
        if (nonSameDayList.size() > 0){
            nonSameDayMap = nonSameDayList.stream().collect(Collectors.toMap(FltStatisticalFleetDataPojo::getTeamId, Function.identity()));
        }

        // 按车队合并数据
        List<FltStatisticalFleetDataPojo> allList = new ArrayList<>();
        FltStatisticalFleetDataPojo fsfdp;
        for (FltStatisticalFleetPojo f : list){
            fsfdp = new FltStatisticalFleetDataPojo();
            fsfdp.setTeamName(f.getTeamName());
            fsfdp.setTeamId(f.getTeamId());
            String ats = f.getAutoTerminals();
            if (StringUtil.isNotEmpty(ats)){
                String[] atss = ats.split(",");
                // 车辆是否有当日数据
                for (String s : atss){
                    FltStatisticalFleetDataPojo fs = sameDayMap.get(s);
                    if (fs != null){
                        // 不良驾驶总次数
                        fsfdp.setTotalCount(fsfdp.getTotalCount()
                                + fs.getOverSpeedCnt()
                                + fs.getRaCnt()
                                + fs.getRdCnt()
                                + fs.getSharpTurnCnt()
                                + fs.getIdleTimeoutCnt()
                                + fs.getColdRunCnt()
                                + fs.getNightRunCnt()
                                + fs.getLowGrHighSpdCnt()
                                + fs.getFullThrottleCnt()
                                + fs.getRoughThrottleCnt()
                                + fs.getNeutralGearCoastCnt()
                                + fs.getStallCoastCnt()
                        );
                    }
                }
            }
            // 此车队在非当日数据中是否有数据
            FltStatisticalFleetDataPojo noFs = nonSameDayMap.get(f.getTeamId());
            if (noFs != null){
                // 不良驾驶总次数
                fsfdp.setTotalCount(fsfdp.getTotalCount()
                        + noFs.getOverSpeedCnt()
                        + noFs.getRaCnt()
                        + noFs.getRdCnt()
                        + noFs.getSharpTurnCnt()
                        + noFs.getIdleTimeoutCnt()
                        + noFs.getColdRunCnt()
                        + noFs.getNightRunCnt()
                        + noFs.getLowGrHighSpdCnt()
                        + noFs.getFullThrottleCnt()
                        + noFs.getRoughThrottleCnt()
                        + noFs.getNeutralGearCoastCnt()
                        + noFs.getStallCoastCnt()
                );
            }
            allList.add(fsfdp);
        }

        // 不良驾驶行为统计
        List<FleetReportBadDrivingDto> bList = new ArrayList<>();
        if (allList.size() > 0){

            Map<Long, FltStatisticalFleetPojo> teamMap = list.stream().collect(Collectors.toMap(FltStatisticalFleetPojo::getTeamId, Function.identity()));
            // 不良驾驶总次数
            int badSum = allList.stream().mapToInt(FltStatisticalFleetDataPojo::getTotalCount).sum();

            FleetReportBadDrivingDto frbdd;
            for (FltStatisticalFleetDataPojo f : allList){
                // 车队下车辆数
                FltStatisticalFleetPojo fsfp = teamMap.get(f.getTeamId());
                String autoTerminals = fsfp.getAutoTerminals();
                int carNum = 0;
                if (StringUtil.isNotEmpty(autoTerminals)){
                    carNum = autoTerminals.split(",").length;
                }

                frbdd = new FleetReportBadDrivingDto();

                // 不良驾驶行为相关
                frbdd.setFleetName(fsfp.getTeamName());
                frbdd.setCreator(fsfp.getCreator());
                frbdd.setCarNumber(String.valueOf(carNum));
                frbdd.setTotalNumber(f.getTotalCount());
                if (badSum == 0){
                    frbdd.setProportion("0.0");
                }else {
                    frbdd.setProportion(new BigDecimal(f.getTotalCount() * 100).divide(new BigDecimal(badSum), 1, BigDecimal.ROUND_HALF_UP).toString());
                }
                bList.add(frbdd);
            }
            // 排序
            if (bList.size() > 0){
                bList = bList.stream().sorted(Comparator.comparing(FleetReportBadDrivingDto::getTotalNumber).reversed())
                        .collect(Collectors.toList());
            }
        }
        // 分页
        PagingInfo<FleetReportBadDrivingDto> pagingInfo = new PagingInfo<>();
        if (StringUtil.isNotEq("1", command.getReturnAll())){
            // 分页
            bList = pageUtil.paging(bList, command.getPage_number(), command.getPage_size(), pagingInfo);
            pagingInfo.setList(bList);
        }else {
            pagingInfo.setList(bList);
            pagingInfo.setTotal(bList.size());
            pagingInfo.setPage_total(1);
        }
        result.setData(pagingInfo);
        return result;
    }

    @SneakyThrows
    @Override
    public HttpCommandResultWithData fleetLineMileageReport(StatisticsFleetLineReportForm form) {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        // 查询时间校验
        String checkResult = statisticsService.checkQueryTime(form.getBeginDate(), form.getEndDate());
        if (StringUtil.isNotEmpty(checkResult)){
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage(checkResult);
            return result;
        }
        // 查询用户车队及车对下的车辆
        List<FltStatisticalFleetPojo> list = new ArrayList<>();
        if (StringUtils.isNotBlank(form.getFleetId())){
             list = queryCars(form.getFleetId(), form.getFleetName());
        }

        if (list.size() == 0){
            return result;
        }

        List<FltStatisticalFleetDataPojo> sameDayList = new ArrayList<>();
        List<FltStatisticalFleetDataPojo> nonSameDayList = new ArrayList<>();

        // 包含当天日期
        if (DateUtil.diffNowDate(form.getEndDate()) == 3){
            List<String> autoTerminals = getAautoTerminals(list);
            if (autoTerminals.size() > 0){
                sameDayList = queryStatisticalOfMilAndFuelFromLocationCloud(autoTerminals);
            }
        }

        // 非当天日期
        if (DateUtil.diffNowDate(form.getBeginDate()) == 2){
            // 过滤全部车队ID
            List<Long> fleetIds = list.stream().map(FltStatisticalFleetPojo::getTeamId).distinct().collect(Collectors.toList());
            nonSameDayList = statisticalMapper.queryStatisticalByTeamId(DateUtil.dateFormatConversion(form.getBeginDate(), DateUtil.date_s_pattern), DateUtil.dateFormatConversion(form.getEndDate(), DateUtil.date_s_pattern), fleetIds);
            this.setTeamName(nonSameDayList);
        }

        // 将当日数据按通信号转为MAP
        Map<String, FltStatisticalFleetDataPojo> sameDayMap = new HashMap<>();
        if (sameDayList.size() > 0){
            sameDayMap = sameDayList.stream().collect(Collectors.toMap(FltStatisticalFleetDataPojo::getAutoTerminal, Function.identity()));
        }

        // 将非当日数据按车队ID转为MAP
        Map<Long, FltStatisticalFleetDataPojo> nonSameDayMap = new HashMap<>();
        if (nonSameDayList.size() > 0){
            nonSameDayMap = nonSameDayList.stream().collect(Collectors.toMap(FltStatisticalFleetDataPojo::getTeamId, Function.identity()));
        }

        // 按车队合并数据
        List<FltStatisticalFleetDataPojo> allList = new ArrayList<>();
        FltStatisticalFleetDataPojo fsfdp;
        for (FltStatisticalFleetPojo f : list){
            fsfdp = new FltStatisticalFleetDataPojo();
            fsfdp.setTeamName(f.getTeamName());
            fsfdp.setTeamId(f.getTeamId());
            String ats = f.getAutoTerminals();
            if (StringUtil.isNotEmpty(ats)){
                String[] atss = ats.split(",");
                // 车辆是否有当日数据
                for (String s : atss){
                    FltStatisticalFleetDataPojo fs = sameDayMap.get(s);
                    if (fs != null){
                        fsfdp.setMileage(fsfdp.getMileage() + fs.getMileage());
                        fsfdp.setSpeedSum(fsfdp.getSpeedSum() + fs.getSpeedSum());
                        fsfdp.setSpeedCount(fsfdp.getSpeedCount() + fs.getSpeedCount());
                    }
                }
            }
            // 此车队在非当日数据中是否有数据
            FltStatisticalFleetDataPojo noFs = nonSameDayMap.get(f.getTeamId());
            if (noFs != null){
                fsfdp.setMileage(fsfdp.getMileage() + noFs.getMileage());
                fsfdp.setSpeedSum(fsfdp.getSpeedSum() + noFs.getSpeedSum());
                fsfdp.setSpeedCount(fsfdp.getSpeedCount() + noFs.getSpeedCount());
            }
            allList.add(fsfdp);
        }

        // 里程统计
        List<FleetReportMileageDto> mList = new ArrayList<>();

        if (allList.size() > 0){
            // 数据统计时间范围
            long dateInterval = DateUtil.diffByDay(form.getBeginDate(), form.getEndDate(), "") + 1;
            Map<Long, FltStatisticalFleetPojo> teamMap = list.stream().collect(Collectors.toMap(FltStatisticalFleetPojo::getTeamId, Function.identity()));
            FleetReportMileageDto frmd;
            for (FltStatisticalFleetDataPojo f : allList){

                // 车队下车辆数
                FltStatisticalFleetPojo fsfp = teamMap.get(f.getTeamId());
                String autoTerminals = fsfp.getAutoTerminals();
                int carNum = 0;
                if (StringUtil.isNotEmpty(autoTerminals)){
                    carNum = autoTerminals.split(",").length;
                }
                frmd = new FleetReportMileageDto();

                // 里程相关
                frmd.setFleetName(fsfp.getTeamName());
                frmd.setCreator(fsfp.getCreator());
                frmd.setCarNumber(String.valueOf(carNum));
                frmd.setTotalMileage(new BigDecimal(f.getMileage()).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue());
                if (carNum == 0){
                    frmd.setAvgMileage("0.0");
                }else {
                    frmd.setAvgMileage(new BigDecimal(f.getMileage() / dateInterval / carNum).setScale(1, BigDecimal.ROUND_HALF_UP).toString());
                }
                if (f.getSpeedCount() == 0){
                    frmd.setAvgSpeed("0.0");
                }else {
                    frmd.setAvgSpeed(new BigDecimal(f.getSpeedSum() / f.getSpeedCount()).setScale(1, BigDecimal.ROUND_HALF_UP).toString());
                }
                mList.add(frmd);

            }
        }
        // 排序
        if (mList.size() > 0){
            mList = mList.stream().sorted(Comparator.comparing(FleetReportMileageDto::getTotalMileage).reversed())
                    .collect(Collectors.toList());
        }
        // 分页
        PagingInfo<FleetReportMileageDto> pagingInfo = new PagingInfo<>();
        if (StringUtil.isNotEq("1", form.getReturnAll())){
            // 分页
            mList = pageUtil.paging(mList, form.getPage_number(), form.getPage_size(), pagingInfo);
            pagingInfo.setList(mList);
        }else {
            pagingInfo.setList(mList);
            pagingInfo.setTotal(mList.size());
            pagingInfo.setPage_total(1);
        }
        result.setData(pagingInfo);
        return result;
    }

    private List<FltStatisticalFleetPojo> queryCars(Long userId, String teamName) throws Exception{
        List<FltStatisticalFleetPojo> list = statisticalMapper.queryFleetAndCars(userId, teamName);
        if (list.size() > 0){
            List<Long> teamIds = list.stream().map(FltStatisticalFleetPojo::getTeamId).collect(Collectors.toList());
            List<FltStatisticalFleetPojo> fsfp = statisticalMapper.queryFleetCreatorByTeamId(teamIds);
            Map<Long, String> map = fsfp.stream().collect(Collectors.toMap(FltStatisticalFleetPojo::getTeamId, FltStatisticalFleetPojo::getCreator));
            // 合并车队创建者名称
            for (FltStatisticalFleetPojo f : list){
                f.setCreator(map.get(f.getTeamId()));
            }
        }
        return list;
    }
    private List<FltStatisticalFleetPojo> queryCars(String fleetId, String teamName) throws Exception{
        List<FltStatisticalFleetPojo> list = statisticalMapper.queryFleetAndCarsByTeamId(fleetId, teamName);
        if (list.size() > 0){
            List<Long> teamIds = list.stream().map(FltStatisticalFleetPojo::getTeamId).collect(Collectors.toList());
            List<FltStatisticalFleetPojo> fsfp = statisticalMapper.queryFleetCreatorByTeamId(teamIds);
            Map<Long, String> map = fsfp.stream().collect(Collectors.toMap(FltStatisticalFleetPojo::getTeamId, FltStatisticalFleetPojo::getCreator));
            // 合并车队创建者名称
            for (FltStatisticalFleetPojo f : list){
                f.setCreator(map.get(f.getTeamId()));
            }
        }
        return list;
    }
    /**
     * 获取通信号集合并去重
     * @param list
     * @return
     * @throws Exception
     */
    private List<String> getAautoTerminals(List<FltStatisticalFleetPojo> list) throws Exception{
        List<String> autoTerminals = new ArrayList<>();
        // 获取车对下车辆的全部车辆通信号
        for (FltStatisticalFleetPojo f : list){
            String ats = f.getAutoTerminals();
            if (StringUtil.isNotEmpty(ats)){
                autoTerminals.addAll(Arrays.asList(ats.split(",")));
            }
        }
        if (autoTerminals.size() > 0){
            // 通信号去重
            autoTerminals = autoTerminals.stream()
                    .distinct()
                    .collect(Collectors.toList());
        }
        return autoTerminals;
    }
    /**
     * 查询当日里程油耗及不良驾驶行为数量
     * @param autoTerminals
     * @return
     */
    private List<FltStatisticalFleetDataPojo> queryStatisticalOfMilAndFuelFromLocationCloud(List<String> autoTerminals){
        // 当日里程油耗等信息
        List<MileageAndOilPojo> milAndOilList = statisticsService.getCurrentDayEntityList(autoTerminals);
        Map<String, MileageAndOilPojo> milAndOilMap = new HashMap<>();
        if (!milAndOilList.isEmpty()){
            milAndOilMap = milAndOilList.stream().collect(Collectors.toMap(MileageAndOilPojo::getTerminalId, Function.identity()));
        }
        // 当日不良驾驶行为信息
        List<FltStatisticalPojo> badDrivingList = statisticsService.querySameDayBadDriving(autoTerminals);
        Map<String, FltStatisticalPojo> badDrivingMap = new HashMap<>();
        if (!badDrivingList.isEmpty()){
            badDrivingMap = badDrivingList.stream().collect(Collectors.toMap(FltStatisticalPojo::getAutoTerminal, Function.identity()));
        }
        // 按通信号合并车辆当日的里程和不良驾驶行为信息
        List<FltStatisticalFleetDataPojo> list = new ArrayList<>();
        FltStatisticalFleetDataPojo fltStatisticalFleetDataPojo;
        for (String s : autoTerminals){
            fltStatisticalFleetDataPojo = new FltStatisticalFleetDataPojo();
            fltStatisticalFleetDataPojo.setAutoTerminal(s);
            MileageAndOilPojo m = milAndOilMap.get(s);
            FltStatisticalPojo f = badDrivingMap.get(s);
            if (m != null){
                fltStatisticalFleetDataPojo.setMileage((float)m.getmMilage());
                fltStatisticalFleetDataPojo.setOilwear((float)m.getFuel());
                fltStatisticalFleetDataPojo.setRunOil((float)m.getRunFuel());
                fltStatisticalFleetDataPojo.setIdlingOil((float)m.getIdlingFuel());
                fltStatisticalFleetDataPojo.setTimeTotal((long) (m.getWorkHours() * 1000));
                fltStatisticalFleetDataPojo.setIdleTime((long) (m.getIdleHours() * 1000));
                fltStatisticalFleetDataPojo.setSpeedSum(m.getSpeedSum());
                fltStatisticalFleetDataPojo.setSpeedCount(m.getSpeedNum());
            }
            if (f != null){
                // 超速
                fltStatisticalFleetDataPojo.setOverSpeedCnt(f.getOverSpeedCnt());
                // 急加速
                fltStatisticalFleetDataPojo.setRaCnt(f.getRaCnt());
                // 急减速
                fltStatisticalFleetDataPojo.setRdCnt(f.getRdCnt());
                // 急转弯
                fltStatisticalFleetDataPojo.setSharpTurnCnt(f.getSharpTurnCnt());
                // 超长怠速
                fltStatisticalFleetDataPojo.setIdleTimeoutCnt(f.getIdleTimeoutCnt());
                // 冷车启动
                fltStatisticalFleetDataPojo.setColdRunCnt(f.getColdRunCnt());
                // 夜晚开车
                fltStatisticalFleetDataPojo.setNightRunCnt(f.getNightRunCnt());
                // 引擎高转速
                fltStatisticalFleetDataPojo.setLowGrHighSpdCnt(f.getLowGrHighSpdCnt());
                // 全油门
                fltStatisticalFleetDataPojo.setFullThrottleCnt(f.getFullThrottleCnt());
                // 大油门
                fltStatisticalFleetDataPojo.setRoughThrottleCnt(f.getRoughThrottleCnt());
                // 空挡滑行
                fltStatisticalFleetDataPojo.setNeutralGearCoastCnt(f.getNeutralGearCoastCnt());
                // 熄火滑行在当天的统计数据中无法统计
                fltStatisticalFleetDataPojo.setStallCoastCnt(f.getStallCoastCnt());
            }
            list.add(fltStatisticalFleetDataPojo);
        }
        return list;
    }
    /**
     * 设置车队名
     * @param pojoList
     */
    private void setTeamName(List<FltStatisticalFleetDataPojo> pojoList) {
        if(CollectionUtils.isNotEmpty(pojoList)){
            List<Long> teamIdList = pojoList.stream().map(FltStatisticalFleetDataPojo::getTeamId).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(teamIdList)){
                List<FleetInfoPojo> teamList = statisticalMapper.queryTeamName(teamIdList);
                if(CollectionUtils.isNotEmpty(teamList)){
                    Map<Long, String> teamMap = teamList.stream().collect(Collectors.toMap(FleetInfoPojo::getTeamId, FleetInfoPojo::getName));
                    for(FltStatisticalFleetDataPojo pojo : pojoList){
                        pojo.setTeamName(teamMap.get(pojo.getTeamId()));
                    }
                }
            }
        }
    }
}
