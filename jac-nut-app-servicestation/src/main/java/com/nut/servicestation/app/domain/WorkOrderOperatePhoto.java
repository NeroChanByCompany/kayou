package com.nut.servicestation.app.domain;

import lombok.Data;

import java.util.Date;

@Data
public class WorkOrderOperatePhoto {
    private Long id;

    private String woCode;

    private String operateId;

    private String type;

    private String url;

    private Long timestamp;

    private String lon;

    private String lat;

    private String addr;

    private String deviceNo;

    private Integer isOffline;

    private Date createTime;

    private Date updateTime;
}