package com.nut.tripanalysis.app.entity;

import lombok.Data;

import java.util.Date;

@Data
public class CarEntity {
    private String id;

    private String carVin;

    private String carBrand;

    private String carSeries;

    private String carSeriesName;

    private String carModel;

    private String carModelCode;

    private String carModelName;

    private String engine;

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

}