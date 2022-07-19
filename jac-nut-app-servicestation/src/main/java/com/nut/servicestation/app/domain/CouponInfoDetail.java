package com.nut.servicestation.app.domain;

import lombok.Data;

/**
 * @description: 优惠券详情
 **/
@Data
public class CouponInfoDetail {

    private Long infoId;
    private Long cumId;
    private String userPhone;
    private String infoType;
    private String infoName;
    private String infoContent;
    private String infoValue;
    private String infoValid;
    private String infoValidDay;
    private String infoValidStartDate;
    private String infoValidEndDate;
    private String infoValidDate;
    private String applicableType;
    private String cumVin;
    private String cumGiveTime;

}
