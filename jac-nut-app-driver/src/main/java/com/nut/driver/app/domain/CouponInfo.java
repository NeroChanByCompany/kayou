package com.nut.driver.app.domain;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CouponInfo {
    private Long id;

    private String infoType;

    private String infoName;

    private String infoContent;

    private String infoValue;

    private String infoFullDecre;

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
    private Long isNationalThird;

    /**
     * 适用类型，1:车系设置 2:指定底盘号 3:指定车型号 4:指定手机号 8:首保券
     */
    private String couponApplicableType;

    private String applicableNumber;
    /**
     * 每个人可以发放的优惠券数量
     */
    private Integer totalAmount;

    private List<CouponApplicable> couponApplicables;
}
