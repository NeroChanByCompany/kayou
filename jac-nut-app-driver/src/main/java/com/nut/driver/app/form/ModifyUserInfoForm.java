package com.nut.driver.app.form;

import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 修改用户信息Form
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.driver.form
 * @Author: yzl
 * @CreateTime: 2021-06-16 10:08
 * @Version: 1.0
 */
@Data
@Accessors(chain = true)
@ApiModel("APP端修改用户信息Entity")
public class ModifyUserInfoForm extends BaseForm implements Serializable {

    private static final long serialVersionUID = 8680757060276568919L;
    /**
     * 用户名
     */
    @ApiModelProperty(name = "name",notes = "用户名称",dataType = "String")
    private String name;
    @ApiModelProperty(name = "userPicUrl",notes = "头像地址",dataType = "String")
    private String userPicUrl;
    @ApiModelProperty(name = "signature",notes = "签名",dataType = "String")
    private String signature;
    @ApiModelProperty(name = "sex",notes = "性别",dataType = "String")
    private String sex;
    @ApiModelProperty(name = "interest",notes = "兴趣",dataType = "String")
    private String interest;
    @ApiModelProperty(name = "birthday",notes = "生日",dataType = "Date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthday;
    /**
     * 省份/直辖市
     */
    @ApiModelProperty(name = "provinceDesc",notes = "省份/直辖市",dataType = "String")
    private String provinceDesc;

    /**
     * 地级市
     */
    @ApiModelProperty(name = "cityDesc",notes = "地级市",dataType = "String")
    private String cityDesc;

    /**
     * 驾龄
     */
    @ApiModelProperty(name = "drivingAge",notes = "驾龄",dataType = "Integer")
    private Integer drivingAge;

    /**
     * 邮件
     */
    @ApiModelProperty(name = "email",notes = "邮箱",dataType = "String")
    private String email;

    /**
     * 年收入单位（万元）
     */
    @ApiModelProperty(name = "annualIncome",notes = "年收入单位（万元）",dataType = "Integer")
    private Integer annualIncome;

    @ApiModelProperty(name = "orgCode",notes = "组织代码",dataType = "String")
    private String orgCode;

    /**
     * 用户手机号
     */
    @ApiModelProperty(name = "phone",notes = "用户手机号",dataType = "String")
    private String phone;

    /**
     * 短信验证码
     */
    @ApiModelProperty(name = "smsCode",notes = "短信验证码",dataType = "String")
    private String smsCode;

    /**
     * 用户类型
     */
    @ApiModelProperty(name = "type",notes = "用户类型",dataType = "String")
    private String type;

    @ApiModelProperty(name = "realName",notes = "真实名称",dataType = "String")
    private String realName;

}
