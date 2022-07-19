package com.nut.servicestation.app.dto;

import lombok.Data;

/**
 * 附近车辆功能位置云新接口使用结果entity
 */
@Data
public class CarLocationToStationNewDto {

    /**
     * 终端通信号
     */
    private String terminalId;

    /**
     * 经度坐标
     */
    private String longtitude;

    /**
     * 纬度坐标
     */
    private String latitude;

}
