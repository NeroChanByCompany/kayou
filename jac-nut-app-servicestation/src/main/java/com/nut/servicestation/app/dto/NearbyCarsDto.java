package com.nut.servicestation.app.dto;

import lombok.Data;

/**
 * 周边车辆对应
 */
@Data
public class NearbyCarsDto {
    /**
     *车辆经度
     */
    private String carLon;
    /**
     *车辆纬度
     */
    private String carLat;
    /**
     *底盘号
     */
    private String chassisNumber;
    /**
     *车牌号
     */
    private String carNumber;
    /**
     *车型车系
     */
    private String carModel;

}
