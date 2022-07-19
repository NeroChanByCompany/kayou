package com.nut.driver.app.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 支付订单
 *
 * @author MengJinyue
 * @email mengjinyue@esvtek.com
 * @date 2021-11-22 14:01:07
 */
@Data
@TableName("tm_pay_order")
public class PayOrderEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	@TableId
	private Long id;
	/**
	 * 总商户号
	 */
	private String merId;
	/**
	 * 收银员号
	 */
	private String merUserId;
	/**
	 * 支付订单编号
	 */
    private String payOrderNumber;
	/**
	 * 银行订单编号（第三方服务招商银行）
	 */
    private String bankOrderNumber;
	/**
	 * 银行的交易状态（银行的交易状态（支付宝/微信：C-订单已关闭 D-交易已撤销 P-交易在进行 F-交易失败 S-交易成功 R-转入退款  一网通：029 - 交易在进行 020 - 交易成功））
	 */
	private String bankTradeState;
	/**
	 * 支付方式：对应字典表中type_code为21
	 */
	private String payWay;
	/**
	 * 用户主键id
	 */
	private Long userId;
	/**
	 * 用户Ucid
	 */
	private String ucId;
	/**
	 * 原价
	 */
	@NotNull(message = "primeCost 不能为空")
	private BigDecimal primeCost;
	/**
	 * 优惠价
	 */
	@NotNull(message = "discountCost 不能为空")
	private BigDecimal discountCost;
	/**
	 * 实际付款价格（原价-优惠价+配送费）
	 */
	@NotNull(message = "realCost 不能为空")
	private BigDecimal realCost;
	/**
	 * 服务费（实际付款价格 * 服务费百分比）
	 */
	private BigDecimal serviceFee;
	/**
	 * 服务费百分数
	 */
	@NotNull(message = "serviceFeePercentage 不能为空")
	private Integer serviceFeePercentage;
	/**
	 * 下单时间
	 */
	private Date orderTime;
	/**
	 * 支付时间
	 */
	private Date payTime;
	/**
	 * 查询支付交易状态的次数
	 */
	private Integer searchPayCount;

	/**
	 * 创建人
	 */
	@JsonIgnore//api文档忽略此属性
	@TableField(value = "create_user", fill = FieldFill.INSERT) // 新增执行
	private String createUser;

	/**
	 * 创建时间
	 */
	@TableField(value = "create_time", fill = FieldFill.INSERT)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@JsonIgnore
	private Date createTime;

	/**
	 * 修改人
	 */
	@JsonIgnore
	@TableField(value = "update_user", fill = FieldFill.INSERT_UPDATE) // 新增和更新执行
	private String updateUser;

	/**
	 * 修改时间
	 */
	@JsonIgnore
	@TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date updateTime;

	/**
	 * 是否删除 0：否，1：是
	 */
	@JsonIgnore
	@TableLogic
	@TableField(select = false)
	private Integer delFlag;

}
