package com.nut.driver.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author liuBing
 * @Classname ModifyPhoneForm
 * @Description TODO
 * @Date 2021/9/1 15:47
 */
@Data
@Accessors(chain = true)
@ApiModel("APP端修改用户手机号Entity")
@NutFormValidator
public class ModifyPhoneForm extends BaseForm {
    @NotNull(message = "手机号不能为空")
    @NotBlank(message = "手机号不能为空")
    @ApiModelProperty(name = "phone",notes = "用户手机号",dataType = "String")
    private String phone;
    @ApiModelProperty(name = "type",notes = "type类型",dataType = "String")
    private String type;
    @NotNull(message = "加密签名不能为空")
    @NotBlank(message = "加密签名不能为空")
    @ApiModelProperty(name = "sign;",notes = "加密签名",dataType = "String")
    private String sign;
    @NotNull(message = "短信验证码不能为空")
    @NotBlank(message = "短信验证码不能为空")
    @ApiModelProperty(name = "smsCode",notes = "短信验证码",dataType = "String")
    private String smsCode;
}
