package com.nut.servicestation.app.domain;

import lombok.Data;

import java.util.Date;

@Data
public class MeterMileageWo {
    private Long id;

    private String woCode;

    private String chassisNum;

    private String carModelBase;

    private Date timeReceive;

    private Double receiveMileage;

    private Date timeClose;

    private Double endrepairMileage;

    private Integer replaceStatus;

    private Date timeUpdateStatus;

    private Date createTime;


}