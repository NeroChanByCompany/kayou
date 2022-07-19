package com.nut.servicestation.app.domain;

import lombok.Data;

import java.util.Date;

@Data
public class CarStationStayOvertime {
    private Long id;

    private String staCode;

    private String carId;

    private Date inTime;

    private Integer warnLevel;

    private Integer withWork;

    private Integer status;

    private Date putOffTill;

    private Date releaseTime;

    private Date createTime;

    private Date updateTime;

}