package com.nut.driver.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Description: 服务APP登录接口
 */
@NutFormValidator
@Data
@ApiModel("APP端忘记密码Entity")
public class ForgetPasswordForm extends BaseForm {

    @NotNull(message = "手机号不能为空")
    @NotBlank(message = "手机号不能为空")
    @ApiModelProperty(name = "phone",notes = "手机号",dataType = "String")
    private String phone;

    @NotNull(message = "验证码不能为空")
    @NotBlank(message = "验证码不能为空")
    @ApiModelProperty(name = "smsCode",notes = "验证码",dataType = "String")
    private String smsCode;

    @NotNull(message = "密码不能为空")
    @NotBlank(message = "密码不能为空")
    @ApiModelProperty(name = "password",notes = "密码",dataType = "String")
    private String password;

    @NotNull(message = "确认密码不能为空")
    @NotBlank(message = "确认密码不能为空")
    @ApiModelProperty(name = "confirmPassword",notes = "确认密码",dataType = "String")
    private String confirmPassword;

    @NotNull(message = "appType不能为空")
    @NotBlank(message = "appType不能为空")
    @ApiModelProperty(name = "appType",notes = "登录类型",dataType = "String")
    private String appType;

    @NotNull(message = "加密签名不能为空")
    @NotBlank(message = "加密签名不能为空")
    @ApiModelProperty(name = "sign",notes = "加密签名",dataType = "String")
    private String sign;
}
