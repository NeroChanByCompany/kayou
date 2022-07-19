package com.nut.driver.app.domain;

import lombok.Data;

import java.util.Date;

@Data
public class FltFleet {
    private Long id;

    private String name;

    private String avatar;

    private Integer createType;

    private String tbossUserId;

    private Date createTime;

    private Date updateTime;

}