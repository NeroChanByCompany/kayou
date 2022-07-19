package com.nut.servicestation.app.domain;

import lombok.Data;

/**
 * @description: 核销记录
 **/
@Data
public class CouponApproval {

    private String userPhone;
    private String infoName;
    private String infoValue;
    private String infoType;
    private String infoContent;
    private String infoValid;
    private String infoValidDay;
    private String infoValidStartDate;
    private String infoValidEndDate;
    private String infoValidDate;
    private String cumGiveTime;
    private String cumExchangeTime;
    private String cumApprovalTime;
    private String cumApprovalStatus;

    /**
     * 新增两个字段：核销券类型--applicableType，车辆底盘号--cumVin
     * @return
     */
}
