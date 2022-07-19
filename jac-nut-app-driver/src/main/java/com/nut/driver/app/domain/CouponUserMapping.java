package com.nut.driver.app.domain;

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

    private String cumVin;

    private String orderId;

    @Override
    public String toString() {
        return "CouponUserMapping{" +
                "cumId=" + cumId +
                ", infoId=" + infoId +
                ", userId=" + userId +
                ", cumNumber='" + cumNumber + '\'' +
                ", cumStatus='" + cumStatus + '\'' +
                ", giveBranchId=" + giveBranchId +
                ", giveBranchType='" + giveBranchType + '\'' +
                ", cumGiveTime='" + cumGiveTime + '\'' +
                ", cumExchangeTime='" + cumExchangeTime + '\'' +
                ", exchangeBranchId=" + exchangeBranchId +
                ", exchangeBranchType='" + exchangeBranchType + '\'' +
                ", cumApprovalTime='" + cumApprovalTime + '\'' +
                ", cumApprovalStatus='" + cumApprovalStatus + '\'' +
                ", createTime=" + createTime +
                ", cumVin='" + cumVin + '\'' +
                ", orderId='" + orderId + '\'' +
                '}';
    }
}