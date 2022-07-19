package com.nut.driver.app.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
//import com.nut.peijian.api.entity.OrderRefundEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @description: 订单列表出参
 * @author: MengJinyue
 * @create: 2021-04-14 17:42
 **/
@Data
public class OrderVo {

    @ApiModelProperty(value = "子单商户号",required = true)
    private String dealerMerId;

    @ApiModelProperty(value = "子单门店号",required = true)
    private String dealerStoreId;

    @ApiModelProperty(value = "支付订单编号",required = false)
    private String payOrderNumber;

    @ApiModelProperty(value = "订单编号",required = true)
    private String orderNumber;

    @ApiModelProperty(value = "订单状态：对应字典表中type_code为30",required = true)
    private String orderStatus;

    @ApiModelProperty(value = "实际付款价格",required = true)
    private BigDecimal realCost;

//    @ApiModelProperty(value = "商品集合",required = true)
//    private List<OrderGoodsVo> orderGoodsList;
//
//    @ApiModelProperty(value = "退款信息",required = false)
//    private OrderRefundEntity orderRefundInfo;

    @ApiModelProperty(value = "创建时间",required = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "完成时间",required = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date completeTime;
}
