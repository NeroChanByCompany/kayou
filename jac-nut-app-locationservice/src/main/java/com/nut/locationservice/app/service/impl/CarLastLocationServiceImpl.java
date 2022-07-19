package com.nut.locationservice.app.service.impl;

import com.nut.locationservice.app.dto.CarLocationToStationDto;
import com.nut.locationservice.app.form.GetCarLastLocationForm;
import com.nut.locationservice.app.module.LastLocationResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 根据通信号获取车辆末次位置信息
 * 只返回经纬度信息，不包含其他额外信息
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.app.locationService.service.impl
 * @Author: yzl
 * @CreateTime: 2021-06-17 11:12
 * @Version: 1.0
 */
@Service
@Slf4j
public class CarLastLocationServiceImpl {

    @Autowired
    private CloudServiceImpl cloudService;

    @SneakyThrows
    public Map<String, CarLocationToStationDto> queryCarLastLocation(GetCarLastLocationForm form) {
        log.info("[CarLastLocationService][queryCarLastLocation] start .... ");
        Map<String, CarLocationToStationDto> map = new HashMap<>();
        try {
            // 获取瞬时数据
            List<LastLocationResponse> lastLocationList = cloudService.queryJsonLastLocation(form.getTerminalIdList());
            log.info("[CarLastLocationService][queryCarLastLocation]位置云返回数据是否为空：{}", lastLocationList == null);
            // 转换成bean
            if (CollectionUtils.isNotEmpty(lastLocationList)) {
                for (LastLocationResponse lastLocation : lastLocationList) {
                    //获取到位置数据
                    log.info("[CarLastLocationService][queryCarLastLocation]通信号：" + lastLocation.getTerminalId());
                    log.info("[CarLastLocationService][queryCarLastLocation]经度：" + lastLocation.getLon() + "纬度: " + lastLocation.getLat());
                    CarLocationToStationDto dto = new CarLocationToStationDto();
                    dto.setTerminalId(lastLocation.getTerminalId());
                    dto.setLat(lastLocation.getLat() / 1000000D);
                    dto.setLon(lastLocation.getLon() / 1000000D);
                    map.put(String.valueOf(lastLocation.getTerminalId()), dto);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        log.info("[CarLastLocationService][queryCarLastLocation] end .... ");
        return map;
    }
}
