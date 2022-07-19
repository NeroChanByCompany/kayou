package com.nut.driver.app.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description: 创建订单的出参
 * @author: MengJinyue
 * @create: 2021-04-15 14:22
 **/
@Data
public class InsertOrderVo {
    @ApiModelProperty(value = "支付订单编号",required = true)
    private String payOrderNumber;

    @ApiModelProperty(value = "实际付款价格（现价*数量-优惠价*数量）",required = true)
    private String realCost;
}
