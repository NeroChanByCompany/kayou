package com.nut.driver.app.form;


import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Description: 获取优惠券详情接口
 */
@Data
@NutFormValidator
@ApiModel("优惠券详情，优惠券Id")
public class CouponDetailForm extends BaseForm {
    /**
     * 优惠券ID
     */
    @NotNull(message = "优惠券ID不能为空")
    @NotBlank(message = "优惠券ID不能为空")
    @ApiModelProperty(value = "cumId",notes = "优惠券Id",dataType = "String")
    private String cumId;
}
