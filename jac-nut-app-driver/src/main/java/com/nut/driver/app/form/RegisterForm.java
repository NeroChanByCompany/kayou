package com.nut.driver.app.form;


import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


/**
 * @Description: 用户注册
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.driver.form
 * @Author: yzl
 * @CreateTime: 2021-06-15 16:30
 * @Version: 1.0
 */
@Data
@NutFormValidator
@ApiModel("APP用户注册Entity")
public class RegisterForm extends BaseForm {

    @NotNull(message = "手机号不能为空")
    @NotBlank(message = "手机号不能为空")
    @ApiModelProperty(name = "phone",notes = "手机号",dataType = "String")
    private String phone;

    @NotNull(message = "姓名不能为空")
    @NotBlank(message = "姓名不能为空")
    @ApiModelProperty(name = "name",notes = "姓名",dataType = "String")
    private String name;

    @NotNull(message = "密码不能为空")
    @NotBlank(message = "密码不能为空")
    @ApiModelProperty(name = "password",notes = "密码",dataType = "String")
    private String password;

    @NotNull(message = "确认密码不能为空")
    @NotBlank(message = "确认密码不能为空")
    @ApiModelProperty(name = "confirmPassword",notes = "确认密码",dataType = "String")
    private String confirmPassword;

    @NotNull(message = "验证码不能为空")
    @NotBlank(message = "验证码不能为空")
    @ApiModelProperty(name = "smsCode",notes = "短信验证码",dataType = "String")
    private String smsCode;

    @ApiModelProperty(name = "type",notes = "type",dataType = "String")
    private String type;

    @ApiModelProperty(name = "orgCode",notes = "组织代码",dataType = "String")
    private String orgCode;
    @NotNull(message = "设备唯一标识码不能为空")
    @NotBlank(message = "设备唯一标识码不能为空")
    @ApiModelProperty(name = "deviceId",notes = "设备编号",dataType = "String")
    private String deviceId;

    @ApiModelProperty(name = "deviceType",notes = "设备类型",dataType = "String")
    private String deviceType;

    @ApiModelProperty(name = "inviterUcid",notes = "邀请人ucId",dataType = "String")
    private String inviterUcid;
    /**
     * 加密数据
     */

    @NotNull(message = "签名不能为空")
    @NotBlank(message = "签名不能为空")
    @ApiModelProperty(name = "sign",notes = "加密签名",dataType = "String")
    private String sign;

    @ApiModelProperty(name = "openInstallType",notes = "用户注册来源是否是openInstall 1 是",dataType = "String")
    private String openInstallType;
    /**
     * 用户注册接口标识 0 新接口
     */
    private Integer registerFlag;
    /**
     * 版本号
     */
    private String paramVersion;

}
