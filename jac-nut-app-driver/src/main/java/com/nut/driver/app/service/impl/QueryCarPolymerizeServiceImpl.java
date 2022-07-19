package com.nut.driver.app.service.impl;

import com.nut.common.exception.ExceptionUtil;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.Result;
import com.nut.common.utils.DateUtil;
import com.nut.common.utils.JsonUtil;
import com.nut.common.utils.StringUtil;
import com.nut.driver.app.TrackPointConvertor;
import com.nut.driver.app.client.LocationServiceClient;
import com.nut.driver.app.dao.CarDao;
import com.nut.driver.app.dto.CarOnlineStatusDto;
import com.nut.driver.app.dto.CarPolymerizeDto;
import com.nut.driver.app.dto.MonitorDto;
import com.nut.driver.app.dto.QueryTrackDto;
import com.nut.driver.app.entity.CarEntity;
import com.nut.driver.app.entity.TrackInfoEntity;
import com.nut.driver.app.form.QueryCarPolymerizeForm;
import com.nut.driver.app.form.QueryTrackForm;
import com.nut.driver.app.pojo.RealTimeCarListPojo;
import com.nut.driver.app.service.QueryCarPolymerizeService;
import com.nut.driver.app.service.ShareTripService;
import com.nut.locationservice.app.form.GetOnlineStatusForm;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.service.impl
 * @Author: yzl
 * @CreateTime: 2021-06-29 16:56
 * @Version: 1.0
 */
@Service
@Slf4j
public class QueryCarPolymerizeServiceImpl implements QueryCarPolymerizeService {

    @Autowired
    private CarDao carDao;

    @Autowired
    private LocationServiceClient locationServiceClient;

    @Autowired
    private ShareTripService shareTripService;

    @Value("${queryTime:86400000}")
    private Long queryTime;



    @Override
    public MonitorDto queryCarLocation(QueryCarPolymerizeForm form) {
        // 查询用户下车辆
        List<RealTimeCarListPojo> carList = carDao.queryMonitorCarsByUserId(form.getAutoIncreaseId(), form.getTeamId());

        MonitorDto monitorDto = new MonitorDto();
        List<CarPolymerizeDto> returnLoc = new ArrayList<>();
        if (carList.size() > 0) {
            List<String> vList = carList.stream().map(RealTimeCarListPojo::getVin).collect(Collectors.toList());
            // 调用位置云接口查询车辆状态
            Map<String, CarOnlineStatusDto> cloudMap = null;
            try {
                GetOnlineStatusForm cloudCommand = new GetOnlineStatusForm();
                cloudCommand.setVinList(vList);
                cloudCommand.setSourceFlag(false);
                HttpCommandResultWithData cloudResult = locationServiceClient.getOnlineStatus(cloudCommand);
                if (cloudResult != null && ECode.SUCCESS.code() == cloudResult.getResultCode() && cloudResult.getData() != null) {
                    cloudMap = (Map<String, CarOnlineStatusDto>) cloudResult.getData();
                }
            } catch (Exception e) {
                log.error("[getOnlineStatus]Exception:", e);
            }

            try {
                int onlineCars = 0;
                CarPolymerizeDto carLocationDto;
                for (RealTimeCarListPojo realTimeCarListPojo : carList) {
                    String vin = realTimeCarListPojo.getVin();
                    CarOnlineStatusDto carOnlineStatusDto = null;
                    if (StringUtil.isNotEmpty(vin) && cloudMap != null) {
                        carOnlineStatusDto = JsonUtil.fromJson(JsonUtil.toJson(cloudMap.get(vin)), CarOnlineStatusDto.class);
                    }
                    carLocationDto = new CarPolymerizeDto();
                    // 查不到经纬度的车辆不显示
                    if (carOnlineStatusDto != null && carOnlineStatusDto.getLon() != null && carOnlineStatusDto.getLat() != null) {
                        carLocationDto.setDirection(carOnlineStatusDto.getDirection() != null ? carOnlineStatusDto.getDirection() : 0);
                        carLocationDto.setLongitude(Double.valueOf(carOnlineStatusDto.getLon()));
                        carLocationDto.setLatitude(Double.valueOf(carOnlineStatusDto.getLat()));
                        carLocationDto.setCount(1);
                        carLocationDto.setTravelStatus(carOnlineStatusDto.getOnLineStatus());
                        if (carOnlineStatusDto.getOnLineStatus() == 1 || carOnlineStatusDto.getOnLineStatus() == 2){
                            onlineCars ++;
                        }

                        carLocationDto.setCarId(realTimeCarListPojo.getCarId());
                        carLocationDto.setCarNo(realTimeCarListPojo.getCarCode());
                        // vin取后8位
                        if (StringUtil.isNotEmpty(vin) && vin.length() > 8) {
                            carLocationDto.setVin(vin.substring(vin.length() - 8));
                        } else {
                            carLocationDto.setVin(vin);
                        }
                        returnLoc.add(carLocationDto);
                    }
                }
                monitorDto.setOnlineCars(onlineCars);
                monitorDto.setList(returnLoc);
            } catch (Exception e) {
                log.error("[convertGetOnlineCar]Exception:", e);
            }
        }
        return monitorDto;
    }

    /**
    * @Description：轨迹回放
    * @author YZL
    * @data 2021/6/30 9:18
    */
    @Override
    @SneakyThrows
    public QueryTrackDto queryTrack(QueryTrackForm form) {
        CarEntity carEntity = carDao.selectByPrimaryKey(form.getCarId());
        if (carEntity == null) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "车辆不存在");
        }
        // 时间格式转换
        // 优先取得卖车时间，如果卖车时间为空，则取邦车时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date saleDate = carEntity.getMbSalesDate();
        if (saleDate == null) {
            List<Date> mappingDate = carDao.findMappingDateByCarId(carEntity.getId());
            if (mappingDate == null || mappingDate.isEmpty() || mappingDate.get(0) == null) {
                log.warn("车辆销售时间和绑定时间都为空 vin: {}, 销售状态: {}", carEntity.getCarVin(), carEntity.getSalesStatus());
            } else {
                saleDate = mappingDate.get(0);
            }
        }
        Long startTime = DateUtil.strTimeChangeLong(form.getBeginDate());
        if(saleDate != null){
            if(startTime < saleDate.getTime()){
                startTime = saleDate.getTime();
            }
        }else {
            log.info("查询车辆信息，销售时间为空");
        }
        Long endTime = DateUtil.strTimeChangeLong(form.getEndDate());

        if ((endTime - startTime) > 7 * queryTime){
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "仅允许查看最长7天的历史轨迹");
        }
        if ((System.currentTimeMillis() - startTime) > 90 * queryTime){
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "仅允许查看近90天内的历史轨迹");
        }

        QueryTrackDto track = null;
        // track
        List<TrackInfoEntity> trackInfoEntities = shareTripService.getTrack(carEntity.getAutoTerminal(), startTime / 1000, endTime / 1000);
        if (trackInfoEntities.size() > 0){
            //Map<String, String> encodeMap = new PolylineEncoder().dpCompute(trackInfoEntities, Integer.valueOf(form.getZoom()), "all", new HashMap(), true);
            track = TrackPointConvertor.list2dto(trackInfoEntities);
        }
        return track;
    }
}
