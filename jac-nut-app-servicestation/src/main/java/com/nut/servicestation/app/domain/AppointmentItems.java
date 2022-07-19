package com.nut.servicestation.app.domain;

import lombok.Data;

import java.util.Date;

@Data
public class AppointmentItems {
    private Long id;

    private String itemName;

    private String itemType;

    private Date createTime;

    private String createUser;

    private Date updateTime;

    private String updateUser;

}