package com.nut.locationservice.app.service.impl;

import com.nut.locationservice.app.dto.CarOnlineStatusDTO;
import com.nut.locationservice.app.form.GetOnlineStatusForm;
import com.nut.locationservice.app.dao.CarDao;
import com.nut.locationservice.app.module.LastLocationResponse;
import com.nut.locationservice.app.pojo.CarInfoPojo;
import com.nut.locationservice.app.service.CloudService;
import com.nut.locationservice.common.constants.CloudConstants;
import com.nut.common.enums.OnlineStatusEnum;
import com.nut.common.utils.JsonUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: 查询车辆位置云状态
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.app.locationService.service.impl
 * @Author: yzl
 * @CreateTime: 2021-06-17 11:29
 * @Version: 1.0
 */
@Service
@Slf4j
public class CarOnlineStatusServiceImpl {

    @Autowired
    private CloudService cloudService;

    @Autowired
    private CarDao carDao;

    @SneakyThrows
    public Map<String, CarOnlineStatusDTO> getOnlineStatus(GetOnlineStatusForm form) {
        log.info("[CarOnlineStatusService][getOnlineStatus] start ....... ");

        List<Long> list = new ArrayList<>();
        List<CarInfoPojo> carList = new ArrayList<>();
        // 判断是否需要查询通信号
        if (form.getSourceFlag()) {
            list = form.getTidList();
        } else {
            // 查询车辆对应的通信号
            carList = carDao.queryCarInfoList(form.getVinList());
            if (CollectionUtils.isNotEmpty(carList)) {
                // 封装查询位置云的参数通信号tidList
                list = carList.stream().filter(e -> e.getTerminalId() != null).map(CarInfoPojo::getTerminalId).collect(Collectors.toList());
            }
        }

        Map<String, CarOnlineStatusDTO> resultMap = null;
        if (list == null  || list.size() == 0  || list.get(0).equals(0L)){
            return  resultMap;
        }

        if (CollectionUtils.isNotEmpty(list)) {
            resultMap = new HashMap<>(list.size());
            try {
                // 封装查询条件
                Map<String, Object> paramMap = new HashMap<>(2);
                paramMap.put("tidList", list);
                paramMap.put("statueFlag", true);
                log.info("[CarOnlineStatusService][getOnlineStatus]请求位置云参数为：{}", JsonUtil.toJson(paramMap));
                List<LastLocationResponse> lastLocationList = cloudService.queryJsonLastLocation(list);
                if (CollectionUtils.isNotEmpty(lastLocationList)) {
                    for (LastLocationResponse lastLocation : lastLocationList) {
                        CarOnlineStatusDTO dto = new CarOnlineStatusDTO();
                        dto.setTid(lastLocation.getTerminalId());
                        dto.setStandardFuelCon(lastLocation.getSf());
                        dto.setStandardMileage(lastLocation.getSm());
                        dto.setGpsDate(lastLocation.getGpsDate());
                        dto.setSpeed((int) lastLocation.getSpeed());
                        dto.setDayOil(lastLocation.getDayOil());
                        //Double lastMil = NumberFormatUtil.getDoubleValueData((double) lastLocation.getDayMil() / 1000, 2);
                        dto.setDayMil(Float.valueOf(lastLocation.getDayMil()));
                        dto.setLon(String.valueOf(lastLocation.getLon() / 1000000D));
                        dto.setLat(String.valueOf(lastLocation.getLat() / 1000000D));
                        dto.setDirection(lastLocation.getDirection());
                        dto.setMileageEngine(lastLocation.getMileageEngine());
                        if (CloudConstants.VEHICLE_STATUS_OFFLINE_STOP.equals(lastLocation.getStatus())) {
                            // 车辆离线
                            dto.setOnLineStatus(OnlineStatusEnum.OL_STS_OFFLINE.code());
                        } else if (CloudConstants.VEHICLE_STATUS_ONLINE_STOP.equals(lastLocation.getStatus())) {
                            // 在线静止
                            dto.setOnLineStatus(OnlineStatusEnum.OL_STS_STILL.code());
                        } else if (CloudConstants.VEHICLE_STATUS_ONLINE_TRAVEL.equals(lastLocation.getStatus())) {
                            // 在线行驶
                            dto.setOnLineStatus(OnlineStatusEnum.OL_STS_MOVING.code());
                        } else if (CloudConstants.VEHICLE_STATUS_DISCONNECTION.equals(lastLocation.getStatus())) {
                            // 车辆断连
                            dto.setOnLineStatus(OnlineStatusEnum.OL_STS_ABSENT.code());
                        } else {
                            // 其他情况都视为断连
                            // 车辆断连
                            dto.setOnLineStatus(OnlineStatusEnum.OL_STS_ABSENT.code());
                        }
                        // 查询方式不同返回的Map的key也不同，true用通信号做key，false用vin做key。
                        if (form.getSourceFlag()) {
                            resultMap.put(String.valueOf(dto.getTid()), dto);
                        } else {
                            String vin = getVin(carList, lastLocation.getTerminalId());
                            resultMap.put(vin, dto);
                        }
                    }
                    log.info("[CarOnlineStatusService][getOnlineStatus]locationService返回数据为：{}", JsonUtil.toJson(resultMap));
                }
            } catch (Exception e) {
                log.error("[CarOnlineStatusService][getOnlineStatus]位置云接口调用异常：" + e.getMessage());
            }
        }
        log.info("[CarOnlineStatusService][getOnlineStatus] end ....... ");
        return resultMap;
    }

    /**
     * 获取vin
     */
    private String getVin(List<CarInfoPojo> list, Long tid) {
        List<CarInfoPojo> pojoList = list.stream().filter(item -> tid.equals(item.getTerminalId())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(pojoList)) {
            return pojoList.get(0).getVin();
        }
        return null;
    }

}
