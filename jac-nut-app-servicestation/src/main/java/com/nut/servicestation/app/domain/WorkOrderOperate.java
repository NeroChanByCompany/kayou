package com.nut.servicestation.app.domain;

import lombok.Data;

import java.util.Date;

@Data
public class WorkOrderOperate {
    private Long id;

    private String woCode;

    private Integer operateCode;

    private String operateId;

    private Integer isHiddenToApp;

    private Integer serviceType;

    private Integer dealType;

    private Integer chargeType;

    private Integer payType;

    private String description;

    private Integer photoNum;

    private String title;

    private String textJson;

    private String textJsonTb;

    private Integer hiddenFlg;

    private String longitude;

    private String latitude;

    private String userId;

    private Integer timesRescueNumber;

    private Integer finishedStatus;

    private Integer subTwiceMark;

    private Date createTime;

    private Date updateTime;
}