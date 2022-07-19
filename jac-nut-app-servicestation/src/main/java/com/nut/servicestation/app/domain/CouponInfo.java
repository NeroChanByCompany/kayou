package com.nut.servicestation.app.domain;

import lombok.Data;

import java.util.Date;

@Data
public class CouponInfo {
    private Long id;

    private String infoType;

    private String infoName;

    private String infoContent;

    private String infoValue;

    private String infoValid;

    private String infoValidDay;

    private String infoValidStartDate;

    private String infoValidEndDate;

    private String infoTotalCountType;

    private String infoTotalCount;

    private String infoRule;

    private String applicableType;

    private String giveType;

    private String giveTimeStart;

    private String giveTimeEnd;

    private String giveTrigger;

    private String exchangeType;

    private Date createTime;

    private String infoStatus;

    private String updateTime;

    private String updateUser;

    private String productType;

    private Long isNationalThird;

    private String fullDecre;

    private String serviceStationCode;

}
