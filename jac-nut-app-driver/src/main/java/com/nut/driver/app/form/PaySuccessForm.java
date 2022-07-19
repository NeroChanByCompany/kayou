package com.nut.driver.app.form;

import com.nut.common.annotation.NutFormValidator;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author MengJinyue
 * @create 2021/3/27
 * @Describtion 支付成功入参
 */
@Data
@NutFormValidator
public class PaySuccessForm {

    @ApiModelProperty(value = "支付订单编号（当buttonType为0时必传）",required = true)
    @NotBlank(message = "参数[payOrderNumber]不能为空")
    private String payOrderNumber;

    @ApiModelProperty(value = "支付方式：2102-微信公众号 2103-支付宝 2104-一网通",required = false)
    private String payWay;
}
