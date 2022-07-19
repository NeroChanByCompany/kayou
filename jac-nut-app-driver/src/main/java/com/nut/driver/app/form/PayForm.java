package com.nut.driver.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author MengJinyue
 * @create 2021/3/27
 * @Describtion 预支付参数
 */
@Data
@NutFormValidator
public class PayForm extends BaseForm {

    @ApiModelProperty(value = "支付订单编号",required = true)
    @NotNull(message = "参数[payOrderNumber]不能为空")
    private String payOrderNumber;

    @ApiModelProperty(value = "支付方式：2102-微信公众号 2103-支付宝 2104-一网通",required = true)
    @NotNull(message = "参数[payWay]不能为空")
    private String payWay;
}
