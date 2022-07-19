package com.nut.tripanalysis.app.entity;

import lombok.Data;

import java.util.Date;

@Data
public class RankMonthEntity {
    private Long id;

    private String carId;

    private String carNum;

    private Date statisDate;

    private Float mileage;

    private Float oilwear;

    private Float oilwearAvg;

    private Date createTime;

    private Long statisTimestamp;

    private String carModel;

    private Integer validDay;

    private Integer ranking;

    private Integer percentage;

}