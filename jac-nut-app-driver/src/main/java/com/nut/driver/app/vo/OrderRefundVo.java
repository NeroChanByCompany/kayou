package com.nut.driver.app.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 支付订单
 *
 * @author MengJinyue
 * @email mengjinyue@esvtek.com
 * @date 2021-04-12 14:01:07
 */
@Data
public class OrderRefundVo {

	@ApiModelProperty(value = "子单商户号",required = true)
	private String dealerMerId;

	@ApiModelProperty(value = "子单门店号",required = true)
	private String dealerStoreId;

	@ApiModelProperty(value = "原交易的母单商户订单号（支付订单编号）",required = true)
	private String payOrderNumber;

	@ApiModelProperty(value = "原交易母单的总金额（合单交易填写合单交易的交易金额）",required = true)
	@NotNull(message = "totalRealCost 不能为空")
	private BigDecimal totalRealCost;

	@ApiModelProperty(value = "原交易母单的下单时间",required = true)
	private Date orderTime;

	@ApiModelProperty(value = "支付方式：对应字典表中type_code为21",required = true)
	private String payWay;

	@ApiModelProperty(value = "服务费百分数",required = true)
	@NotNull(message = "serviceFeePercentage 不能为空")
	private Integer serviceFeePercentage;

	@ApiModelProperty(value = "订单编号（子单）",required = true)
	private String orderNumber;

	@ApiModelProperty(value = "订单金额（子单）",required = true)
	private BigDecimal realCost;

	@ApiModelProperty(value = "退款单号（子单）",required = true)
	private String refundNumber;

	@ApiModelProperty(value = "退款金额（子单）",required = true)
	private BigDecimal refundCost;
}
