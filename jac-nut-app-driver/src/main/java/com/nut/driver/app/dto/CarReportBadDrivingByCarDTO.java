package com.nut.driver.app.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CarReportBadDrivingByCarDTO {

    /**
     *车辆ID
     */
    private String carId;

    /**
     *车牌号
     */
    private String carNumber;

    /**
     *不良驾驶行为次数
     */
    private Integer badNumber;

    /**
     *不良驾驶行为占比
     */
    private BigDecimal badProportion;

}
