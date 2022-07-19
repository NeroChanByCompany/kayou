package com.nut.driver.app.domain;

import lombok.Data;

import java.util.Date;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.domain
 * @Author: yzl
 * @CreateTime: 2021-06-27 15:10
 * @Version: 1.0
 */
@Data
public class Car {
    private String id;

    private String carVin;

    private String carBrand;

    private String carBandName;

    private String carSeries;

    private String carSeriesName;

    private String carModel;

    private String carModelCode;

    private String carModelBase;

    private String carModelName;

    private String engine;

    private String engineNo;

    private String isVip;

    private String ownerId;

    private String invoicePhoto;

    private String invoiceNo;

    private String identityCard;

    private String organization;

    private String carNumber;

    private String terminalId;

    private String autoTerminal;

    private Date createTime;

    private Date updateTime;

    private Date salesDate;

    private Long tId;

    private Integer mbSalesStatus;

    private Date mbSalesDate;

    private Integer salesStatus;

    private Integer tboxType;

    private String isBind;

    private Integer extInfoOk;
}
