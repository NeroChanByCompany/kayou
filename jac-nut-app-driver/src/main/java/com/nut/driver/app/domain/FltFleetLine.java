package com.nut.driver.app.domain;

import lombok.Data;

import java.util.Date;

@Data
public class FltFleetLine {
    private Long id;

    private String startCityCode;

    private String startCityName;

    private String endCityCode;

    private String endCityName;

    private String maxSpeed;

    private String createUserId;

    private String oilConsLimit;

    private String routes;

    private String carIds;

    private String fleetId;

    private String fleetName;

    private Date createTime;

    private Date updateTime;

}
