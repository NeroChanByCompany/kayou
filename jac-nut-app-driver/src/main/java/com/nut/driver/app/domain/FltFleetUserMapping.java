package com.nut.driver.app.domain;

import lombok.Data;

import java.util.Date;

@Data
public class FltFleetUserMapping {
    private Long id;

    private Long userId;

    private Long teamId;

    private Integer role;

    private Integer createType;

    private Long createUserId;

    private String tbossUserId;

    private Date createTime;

    private Date updateTime;

}