package com.nut.servicestation.app.domain;

import lombok.Data;

import java.util.Date;

@Data
public class ServiceAarNotice {
    private Long id;

    private String noticeCode;

    private String noticeName;

    private String noticeType;

    private Date endDt;

    private String chkService;

    private String checkVeh;

    private String identNum;

    private String stationName;

    private String orgId;

    private String chassisNoFinal;

    private String chassisNo;

    private String vehicleModel;

    private String caNum;

    private String alNum;

    private String effectiveFlag;

    private Date createTime;

    private Date updateTime;

}