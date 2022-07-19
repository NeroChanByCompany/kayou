package com.nut.driver.app.domain;

import lombok.Data;

import java.util.Date;

@Data
public class FltUserContactMapping {
    private Long id;

    private Long userId;

    private String nickname;

    private String phone;

    private Long createUserId;

    private Date createTime;

    private Date updateTime;
}