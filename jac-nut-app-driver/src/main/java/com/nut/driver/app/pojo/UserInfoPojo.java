package com.nut.driver.app.pojo;

import lombok.Data;

import java.util.Date;

/**
 * @Description: 用户信息
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.driver.pojo
 * @Author: yzl
 * @CreateTime: 2021-06-16 16:10
 * @Version: 1.0
 */
@Data
public class UserInfoPojo {
    private String accountName;
    private Long id;
    private String name;
    private String phone;
    private String identityCard;
    private Integer drivingLicense;
    private String carId;
    private String carVin;
    private String carNo;
    private String orgCode;
    private String custTag;
    private String userPicUrl;
    private String isMasterDriver;
    private String signature;
    private String provinceDesc;
    private String cityDesc;

    /**
     * 性别
     */
    private String sex;

    /**
     * 兴趣
     */
    private String interest;

    /**
     * 生日
     */
    private Date birthday;

    /**
     * 驾龄
     */
    private Integer drivingAge;

    /**
     * 邮件
     */
    private String email;

    /**
     * 年收入单位（万元）
     */
    private Integer annualIncome;

    private String realName;
}
