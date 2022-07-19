package com.nut.driver.app.form;


import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import com.nut.common.utils.RegexpUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @Description: APP端登录接口
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.driver.form
 * @Author: yzl
 * @CreateTime: 2021-06-16 09:06
 * @Version: 1.0
 */
@Data
@NutFormValidator
@ApiModel("APP确认验证码Entity")
public class SmsVerificationCodeForm extends BaseForm {

    @NotNull(message = "手机号不能为空")
    @NotBlank(message = "手机号能为空")
    @Pattern(regexp = RegexpUtils.MOBILE_PHONE_REGEXP, message = "手机号格式不正确")
    @ApiModelProperty(name = "phone",notes = "手机号",dataType = "String")
    private String phone;

    @NotNull(message = "验证码请求类型不能为空")
    @NotBlank(message = "验证码请求类型不能为空")
    @Pattern(regexp = RegexpUtils.MATCHING_SPECIFIC_NUMBERS_0_1REGEXP, message = "验证码请求类型不正确")
    @ApiModelProperty(name = "sign",notes = "验证码请求类型",dataType = "String")
    private String sign;


    @NotNull(message = "验证码请求秘钥不能为空")
    @NotBlank(message = "验证码请求秘钥不能为空")
    @ApiModelProperty(name = "secret",notes = "秘钥",dataType = "String")
    private String secret;
}
