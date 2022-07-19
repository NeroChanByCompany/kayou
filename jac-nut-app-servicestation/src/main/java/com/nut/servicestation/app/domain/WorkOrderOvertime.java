package com.nut.servicestation.app.domain;

import lombok.Data;

import java.util.Date;

@Data
public class WorkOrderOvertime {
    private Long id;

    private String woCode;

    private Integer type;

    private Integer status;

    private Date putOffTill;

    private Date normalTime;

    private Date createTime;

    private Date updateTime;

}