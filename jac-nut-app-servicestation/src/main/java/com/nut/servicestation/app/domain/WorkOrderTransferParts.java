package com.nut.servicestation.app.domain;

import lombok.Data;

import java.util.Date;

@Data
public class WorkOrderTransferParts {
    private Long id;

    private String woCode;

    private Integer woType;

    private String operateId;

    private Integer status;

    private String partsNo;

    private String partsName;

    private Integer partsNum;

    private Integer partsFlag;

    private Integer serviceType;

    private Integer inquireStatus;

    private Date putOffTill;

    private Date createTime;

    private Date updateTime;

}