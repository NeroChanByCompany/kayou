package com.nut.driver.app.domain;

import lombok.Data;

import java.util.Date;

@Data
public class User {
    private Long id;

    /**
     * 业务id
     */
    private String ucId;

    private String accountName;

    private String ucPassword;

    /**
     * 用户名密码
     */
    private String password;

    private Integer createType;

    /**
     * 用户名
     */
    private String name;

    /**
     * 手机号
     */
    private String phone;

    private String identityCard;

    private Integer drivingLicense;

    private String createUserId;

    private String ownrCurTeamId;

    private String drvrCurTeamId;

    private String drvrCurCarId;

    /**
     * 创建时间
     */
    private Date createTime;

    private Date updateTime;

    private Date lastLogonTime;

    private Date firstLoginTime;

    private String inviterId;

    private int noviceGuideTag;

    private String orgCode;
    private String custTag;
    private String region;
    private String provinceDesc;
    private String cityDesc;
    private String countyDesc;
    private String sendMessageKey;

    /**
     * 客户端类型
     * 0：司机
     * 1：车队
     */
    private String appType;

    private String userPicUrl;
    private String signature;
    private String sex;
    private String interest;
    private Date birthday;
    private Integer drivingAge;
    private String email;
    private Integer annualIncome;

    private String realName;

    private String deviceId;

    private Integer infoOk;


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", ucId='" + ucId + '\'' +
                ", accountName='" + accountName + '\'' +
                ", ucPassword='" + ucPassword + '\'' +
                ", password='" + password + '\'' +
                ", createType=" + createType +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", identityCard='" + identityCard + '\'' +
                ", drivingLicense=" + drivingLicense +
                ", createUserId='" + createUserId + '\'' +
                ", ownrCurTeamId='" + ownrCurTeamId + '\'' +
                ", drvrCurTeamId='" + drvrCurTeamId + '\'' +
                ", drvrCurCarId='" + drvrCurCarId + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", lastLogonTime=" + lastLogonTime +
                ", firstLoginTime=" + firstLoginTime +
                ", inviterId='" + inviterId + '\'' +
                ", noviceGuideTag=" + noviceGuideTag +
                ", orgCode='" + orgCode + '\'' +
                ", custTag='" + custTag + '\'' +
                ", region='" + region + '\'' +
                ", provinceDesc='" + provinceDesc + '\'' +
                ", cityDesc='" + cityDesc + '\'' +
                ", countyDesc='" + countyDesc + '\'' +
                ", sendMessageKey='" + sendMessageKey + '\'' +
                ", appType='" + appType + '\'' +
                ", userPicUrl='" + userPicUrl + '\'' +
                ", signature='" + signature + '\'' +
                ", sex='" + sex + '\'' +
                ", interest='" + interest + '\'' +
                ", birthday=" + birthday +
                ", drivingAge=" + drivingAge +
                ", email='" + email + '\'' +
                ", annualIncome=" + annualIncome +
                ", realName='" + realName + '\'' +
                ", deviceId='" + deviceId + '\'' +
                '}';
    }
}