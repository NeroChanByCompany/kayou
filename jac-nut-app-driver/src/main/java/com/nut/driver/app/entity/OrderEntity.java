package com.nut.driver.app.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单
 *
 * @author MengJinyue
 * @email mengjinyue@esvtek.com
 * @date 2021-11-23 14:01:08
 */
@Data
@TableName("tm_order")
public class OrderEntity  implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	@TableId
	private Long id;

	/**
	 * 支付订单编号
	 */
	@ApiModelProperty(value = "支付订单编号",required = false)
	private String payOrderNumber;
	/**
	 * 订单编号
	 */
	@ApiModelProperty(value = "订单编号",required = false)
	private String orderNumber;
	/**
	 * 用户主键ID
	 */
	@ApiModelProperty(value = "用户主键ID",required = true)
	private Long userId;

	/**
	 * 用户ID
	 */
	@ApiModelProperty(value = "用户ID",required = true)
	private String ucId;

	/**
	 * 车牌号
	 */
	@ApiModelProperty(value = "车牌号",required = true)
	private String carNumber;

	/**
	 * 用户手机号
	 */
	@ApiModelProperty(value = "用户手机号",required = true)
	@NotNull(message = "phone 不能为空")
	private String phone;
	/**
	 * 子单商户号
	 */
	@ApiModelProperty(value = "经营商户号",required = true)
	private String dealerMerId;
	/**
	 * 子单门店号
	 */
	@ApiModelProperty(value = "经营门店号",required = true)
	private String dealerStoreId;
	/**
	 * 订单状态：对应字典表中type_code为30
	 */
	@ApiModelProperty(value = "订单状态：对应字典表中type_code为30",required = true)
	@NotNull(message = "orderStatus 不能为空")
	private String orderStatus;
	/**
	 * 实际付款价格（原价-优惠价+配送费）
	 */
	@ApiModelProperty(value = "实际付款价格（原价-优惠价+配送费）",required = true)
	@NotNull(message = "realCost 不能为空")
	private BigDecimal realCost;

	@ApiModelProperty(value = "终端通信号，车辆接口返回 autoTerminal",required = true)
	private String simNum;

	@ApiModelProperty(value = "终端ID 车辆接口返回 terminalId",required = true)
	private String simId;

	@ApiModelProperty(value = "套餐ID，套餐查询接口 返回 ID",required = true)
	private String pacId;

	@ApiModelProperty(value = "套餐名称,套餐查询接口 返回 setMealTitl",required = true)
	private String paName;

	@ApiModelProperty(value = "套餐类别,套餐查询接口 返回 套餐类型",required = true)
	private String pacType;

	@ApiModelProperty(value = "套餐流量,套餐查询接口 返回 dataUsageSize",required = true)
	private String pacFlow;

	@ApiModelProperty(value = "套餐售价,套餐查询接口 返回 price",required = true)
	private BigDecimal pacPrice;

	@ApiModelProperty(value = "套餐有效时间,套餐查询接口 返回 periodTypeName",required = true)
	private String actiTime;

	@ApiModelProperty(value = "套餐描述,套餐查询接口 返回 setMealContent",required = true)
	private String pacDesc;
	/**
	 * 完成时间
	 */
	@ApiModelProperty(value = "完成时间",required = false)
	private Date completeTime;

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
