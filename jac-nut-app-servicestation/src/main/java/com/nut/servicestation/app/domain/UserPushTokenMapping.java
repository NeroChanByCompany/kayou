package com.nut.servicestation.app.domain;

import lombok.Data;

import java.util.Date;

@Data
public class UserPushTokenMapping {
    private Long id;

    private String userId;

    private String appType;

    private String pushToken;

    private Date createTime;

    private Date updateTime;

}