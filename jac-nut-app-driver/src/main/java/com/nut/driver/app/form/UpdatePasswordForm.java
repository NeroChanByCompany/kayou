package com.nut.driver.app.form;


import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Description: 重置密码
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.driver.form
 * @Author: yzl
 * @CreateTime: 2021-06-16 17:00
 * @Version: 1.0
 */
@Data
@NutFormValidator
@ApiModel("APP重置密码Entity")
public class UpdatePasswordForm extends BaseForm {

    /**
     * //新密码
     */
    @NotNull(message = "新密码不能为空")
    @ApiModelProperty(name = "newPassword",notes = "新密码",dataType = "String")
    private String newPassword;
    /**
     * 旧密码
     */
    @NotNull(message = "旧密码不能为空")
    @ApiModelProperty(name = "oldPassword",notes = "旧密码",dataType = "String")
    private String oldPassword;

    @NotNull(message = "token不能为空")
    @ApiModelProperty(name = "token",notes = "token",dataType = "String")
    private String token;

    @ApiModelProperty(name = "product",notes = "产品",dataType = "String")
    private String product;
    /**
     * 加密签名
     */
    @NotNull(message = "加密签名不能为空")
    @NotBlank(message = "加密签名不能为空")
    @ApiModelProperty(name = "sign",notes = "加密签名",dataType = "String")
    private String sign;

    /**
     * 加密签名
     */
    @NotNull(message = "手机号不能为空")
    @NotBlank(message = "手机号不能为空")
    @ApiModelProperty(name = "phone",notes = "手机号",dataType = "String")
    private String phone;

}
