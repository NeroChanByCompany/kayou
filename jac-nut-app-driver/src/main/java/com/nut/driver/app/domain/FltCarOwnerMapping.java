package com.nut.driver.app.domain;

import lombok.Data;

import java.util.Date;

@Data
public class FltCarOwnerMapping {
    private Long id;

    private String carId;

    private Long userId;

    private Integer createType;

    private String tbossUserId;

    private Date createTime;

    private Date updateTime;
}