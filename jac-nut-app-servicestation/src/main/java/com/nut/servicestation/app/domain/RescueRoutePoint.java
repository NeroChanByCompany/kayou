package com.nut.servicestation.app.domain;

import lombok.Data;

import java.util.Date;

@Data
public class RescueRoutePoint {
    private Long id;

    private String woCode;

    private String userId;

    private Integer mileage;

    private Integer duration;

    private Integer estimateMileage;

    private Integer estimateDuration;

    private Integer confirmMileage;

    private Integer isLogout;

    private String points;

    private String estimatePoints;

    private String originalPoints;

    private Integer maxIndex;

    private Date createTime;

    private Date updateTime;

}