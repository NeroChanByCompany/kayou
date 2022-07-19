package com.nut.servicestation.app.form;


import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @program:
 * @description: 兑换
 **/
@Data
@NutFormValidator
public class CouponExchangeForm extends BaseForm {

    @NotNull(message = "优惠券ID不能为空")
    @NotBlank(message = "优惠券ID不能为空")
    private String cumId;


}
