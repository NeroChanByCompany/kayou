package com.nut.driver.app.form;


import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Description: 用户信息查询
 */
@Data
@ApiModel("APP端用户信息查询Entity")
@NutFormValidator
public class GetUserPhoneForm extends BaseForm {
    /**
     * 用户手机号
     */
    @NotBlank(message = "手机号不能为空")
    private String phone;
}
