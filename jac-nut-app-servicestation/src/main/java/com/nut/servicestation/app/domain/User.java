package com.nut.servicestation.app.domain;

import lombok.Data;

import java.util.Date;

@Data
public class User {
    private Long id;

    private String ucId;

    private String accountName;

    private String ucPassword;

    private String password;

    private Integer createType;

    private String name;

    private String phone;

    private String identityCard;

    private Integer drivingLicense;

    private String createUserId;

    private String ownrCurTeamId;

    private String drvrCurTeamId;

    private String drvrCurCarId;

    private Date createTime;

    private Date updateTime;

    private Date lastLogonTime;

}