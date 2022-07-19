package com.nut.locationservice.app.service.impl;

import com.nut.locationservice.app.assembler.NearByCarAssmber;
import com.nut.locationservice.app.dto.CarLocationToStationDto;
import com.nut.locationservice.app.form.GetCarLocationToStationForm;
import com.nut.locationservice.app.module.LastLocationResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 根据通信号获取车辆末次位置信息
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.app.locationService.service.impl
 * @Author: yzl
 * @CreateTime: 2021-06-17 11:18
 * @Version: 1.0
 */
@Service
@Slf4j
public class CarLocationToStationServiceImpl {

    @Autowired
    CloudServiceImpl cloudService;

    @SneakyThrows
    public List<CarLocationToStationDto> queryCarLocation(GetCarLocationToStationForm form) {
        log.info("[CarLocationToStationService][queryCarLocation] start .... ");
        List<CarLocationToStationDto> resultList = new ArrayList<>();
        try {
            List<LastLocationResponse> lastLocationList = cloudService.queryJsonLastLocation(form.getTerminalIdList());

            log.info("[CarLocationToStationService][queryCarLocation]位置云返回数据是否为空：{}", lastLocationList == null);
            // 转换成bean
            if (CollectionUtils.isNotEmpty(lastLocationList)) {

                for (LastLocationResponse lastLocation : lastLocationList) {
                    //获取到位置数据
                    if (lastLocation != null) {
                        // 返回true时在以服务站为中心，查询直径为边长的正方形范围
                        if (NearByCarAssmber.isCarInArea(form.getLon(), form.getLat(), form.getRange()
                                , lastLocation.getLon(), lastLocation.getLat())) {
                            //在范围内的车辆
                            log.info("[CarLocationToStationService][queryCarLocation]通信号：" + lastLocation.getTerminalId());
                            log.info("[CarLocationToStationService][queryCarLocation]经度：" + lastLocation.getLon() + "纬度: " + lastLocation.getLat());
                            CarLocationToStationDto dto = new CarLocationToStationDto();
                            dto.setTerminalId(lastLocation.getTerminalId());
                            dto.setLat(lastLocation.getLat() / 1000000D);
                            dto.setLon(lastLocation.getLon() / 1000000D);
                            resultList.add(dto);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        log.info("[CarLocationToStationService][queryCarLocation] end .... ");
        return resultList;
    }

}
