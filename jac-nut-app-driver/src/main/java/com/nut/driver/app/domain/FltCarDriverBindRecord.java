package com.nut.driver.app.domain;

import lombok.Data;

import java.util.Date;

@Data
public class FltCarDriverBindRecord {
    private Long id;

    private Long teamId;

    private String carId;

    private Long userId;

    private Long createUserId;

    private String isMasterDriver;

    private Date createTime;

    private Date updateTime;

    private String bind;

    private String isFirstBind;
}