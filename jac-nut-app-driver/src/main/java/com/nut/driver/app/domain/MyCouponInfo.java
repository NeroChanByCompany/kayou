package com.nut.driver.app.domain;

import lombok.Data;

import java.util.List;

/**
 * @Description: 优惠券信息
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.common.domain
 * @Author: yzl
 * @CreateTime: 2021-06-18 15:35
 * @Version: 1.0
 */
@Data
public class MyCouponInfo {
    private Long cumId;

    private String infoType;

    private String applicableType;

    private String applicable;

    private String infoId;

    private String infoName;

    private String infoContent;

    private String cumStatus;

    private String infoValue;

    private String infoValid;

    private String infoValidDay;

    private String infoValidStartDate;

    private String infoValidEndDate;

    private String infoRule;

    private String infoStatus;

    private String infoValidDate;

    private String cumGiveTime;

    private String cumNumber;

    private String cumNumberUrl;

    private String productAble;

    private String productType;

    private String infoDisableType;

    private String defaultChoose;

    private String giveBranchType;
    private String infoPar;
    private String fullDecre;

    private List<String> allModels;

    private Long isNationalThird;
    private String isBind;

    private String carVIn;
}
