package com.nut.servicestation.app.domain;

import lombok.Data;

import java.util.Date;

@Data
public class CouponUserMapping {
    private Long cumId;

    private Long infoId;

    private Long userId;

    private String cumNumber;

    private String cumStatus;

    private Long giveBranchId;

    private String giveBranchType;

    private String cumGiveTime;

    private String cumExchangeTime;

    private Long exchangeBranchId;

    private String exchangeBranchType;

    private String cumApprovalTime;

    private String cumApprovalStatus;

    private Date createTime;

    private String infoValid;

    private String infoValidStartDate;

    private String infoValidEndDate;

}
