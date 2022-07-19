package com.nut.truckingteam.app.domain;

import lombok.Data;

import java.util.Date;

@Data
public class FltFleet {
    private Long id;

    private String name;

    private String avatar;

    private Integer createType;

    private String tbossUserId;

    private String isCompany;

    private String companyName;

    private String creditCode;

    private String isGroup;

    private String isVip;

    private Date createTime;

    private Date updateTime;

}