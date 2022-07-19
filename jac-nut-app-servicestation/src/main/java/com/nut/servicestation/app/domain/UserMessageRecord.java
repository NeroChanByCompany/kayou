package com.nut.servicestation.app.domain;

import lombok.Data;

import java.util.Date;

@Data
public class UserMessageRecord {
    private Long id;

    private String messageId;

    private String title;

    private String content;

    private Integer pushShowType;

    private Integer messageCode;

    private Integer userVisible;

    private String receiverId;

    private Integer receiveAppType;

    private String senderId;

    private Long sendTime;

    private Integer readFlag;

    private String messageExtra;

    private Integer type;

    private Integer stype;

    private Date createTime;

    private Date updateTime;

}