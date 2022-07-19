package com.nut.servicestation.app.pojo;

import lombok.Data;

@Data
public class CarWarnPojo {

    /**
     * 车辆ID
     */
    private String carId;
    /**
     * VIN
     */
    private String carVin;
    /**
     *车牌号
     */
    private String carNumber;
    /**
     *车辆型号名
     */
    private String carModelName;

    /**
     * 车系名称
     */
    private String carSeriesName;
    /**
     *发动机型号
     */
    private String engine;
    /**
     *通信号
     */
    private String autoTerminal;
    /**
     * 预警级别（1：橙色预警；2：红色预警）
     */
    private String warnLevel;
    /**
     * 联系电话
     */
    private String phoneNumbers;
    /**
     * 是否有工单（0：否；1：是）
     */
    private Integer withWork;

}
