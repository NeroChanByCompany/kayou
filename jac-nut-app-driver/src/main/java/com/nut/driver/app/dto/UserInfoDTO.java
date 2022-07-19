package com.nut.driver.app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.driver.dto
 * @Author: yzl
 * @CreateTime: 2021-06-16 16:15
 * @Version: 1.0
 */
@Data
public class UserInfoDTO {

    private String ucId;

    private String accountName;

    //用户自增id
    private Long id;

    //用户名
    private String name;

    //手机号
    private String phone;

    // 用户角色（0：创建者；1：管理员；2：司机）用户拥有多个角色时用","分割
    private String role;

    // 司机/车队在途工单数
    private Long otwOrderNum;

    private String orgCode;
    private String custTag;

    private String userPicUrl;

    /**
     * 签名
     */
    private String signature;

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

    private String provinceDesc;
    private String cityDesc;

    /** 是否已签到 0-未签到 1-已签到 */
    private Integer isSign;

    /** 网红表示 1-网红；0-非网红 */
    private Integer startSign;
}
