package com.nut.driver.app.form;


import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @Description: APP端登录
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("APP端登录Entity")
@NutFormValidator
public class AppLoginForm extends BaseForm {

    @NotNull(message = "手机号不能为空")
    @NotBlank(message = "手机号不能为空")
    @ApiModelProperty(name = "phone",notes = "手机号",dataType = "String")
    private String phone;
    @ApiModelProperty(name = "loginName",notes = "用户名",dataType = "String")
    private String loginName;
    @ApiModelProperty(name = "password",notes = "密码",dataType = "String")
    private String password;

    /**
     * 短信验证码
     */
    @ApiModelProperty(name = "smsCode",notes = "短信验证码",dataType = "String")
    private String smsCode;

    @NotNull(message = "sendMessageKey不能为空")
    @NotBlank(message = "sendMessageKey不能为空")
    @ApiModelProperty(name = "sendMessageKey",notes = "发送短信key",dataType = "String")
    private String sendMessageKey;
    @ApiModelProperty(name = "product",notes = "产品",dataType = "String")
    private String product;
    @ApiModelProperty(name = "useDetail",notes = "使用详情",dataType = "String")
    private String useDetail;
    @ApiModelProperty(name = "autoLogin",notes = "自动登录",dataType = "String")
    private String autoLogin;
    @ApiModelProperty(name = "captcha",notes = "验证码",dataType = "String")
    private String captcha;
    @ApiModelProperty(name = "deviceId",notes = "设备编号",dataType = "String")
    private String deviceId;

    @NotNull(message = "设备类型不能为空")
    @NotBlank(message = "设备类型不能为空")
    @Pattern(regexp ="^[012]$", message = "设备类型格式不正确")
    @ApiModelProperty(name = "deviceType",notes = "设备类型",dataType = "String")
    private String deviceType;

    @NotNull(message = "appType不能为空")
    @NotBlank(message = "appType不能为空")
    @Pattern(regexp ="^[01]$", message = "appType格式不正确")
    @ApiModelProperty(name = "appType",notes = "登录类型",dataType = "String")
    private String appType;
}
