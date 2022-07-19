package com.nut.servicestation.app.form;


import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @description: 优惠券详情
 **/
@Data
@NutFormValidator
public class CouponDetailForm extends BaseForm {

    @NotNull(message = "券码不能为空")
    @NotBlank(message = "券码不能为空")
    private String cumNumber;

}
