package com.nut.servicestation.app.dto;

import lombok.Data;

@Data
public class StationStayWarnCarDto {

    /**
     *车辆ID
     */
    private String carId;
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
     *车系车型
     */
    private String carModel;
    /**
     *联系电话
     */
    private String phoneNumbers;
    /**
     *预警级别
     */
    private String warnLevel;

    private String autoTerminal;

    /**
     * 是否有工单（0：否；1：是）
     */
    private Integer withWork;

}
