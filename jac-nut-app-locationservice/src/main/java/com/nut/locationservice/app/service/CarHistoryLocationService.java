package com.nut.locationservice.app.service;

import com.nut.locationservice.app.dto.CarHistoryResultDTO;
import com.nut.locationservice.app.form.GetCarHistoryLocationForm;

/**
 * @author liuBing
 * @Classname CarHistoryLocationService
 * @Description TODO service
 * @Date 2021/6/16 10:21
 */
public interface CarHistoryLocationService {

    /**
     * @return com.nut.locationstation.app.locationService.dto.CarHistoryResultDTO
     * @Author liuBing
     * @Description //TODO 查询车辆历史信息
     * @Date 10:51 2021/6/16
     * @Param [form] terminalId 车辆id queryDate 查询时间 index 索引 accessTocken token
     **/
    CarHistoryResultDTO getCarLocation(GetCarHistoryLocationForm form);

}
