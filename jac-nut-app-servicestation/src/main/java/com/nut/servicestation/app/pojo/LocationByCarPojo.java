package com.nut.servicestation.app.pojo;

import lombok.Data;

@Data
public class LocationByCarPojo {

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

}
