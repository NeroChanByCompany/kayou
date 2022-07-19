package com.nut.driver.app.domain;

import lombok.Data;

import java.util.Date;

@Data
public class FltFleetLineRoute {
    private Long id;

    private Long lineId;

    private String routeCityCode;

    private String routeCityName;

    private String createUserId;

    private Date createTime;

    private Date updateTime;

}
