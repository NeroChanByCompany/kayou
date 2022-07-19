package com.nut.tripanalysis.app.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author generator
 */
@Data
public class RankYesterdayEntity {
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

    private Integer ranking;

    private Integer percentage;

}