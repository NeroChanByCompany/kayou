package com.nut.driver.app.form;
import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description: 订单列表入参
 * @author: MengJinyue
 * @create: 2021-04-15 14:09
 **/
@Data
public class OrderForm extends BaseForm {
    @ApiModelProperty(value = "支付订单编号",required = false)
    private String payOrderNumber;
}
