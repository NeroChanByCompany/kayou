package com.jac.app.job.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
	private String payOrderNumber;
	/**
	 * 订单编号
	 */
	private String orderNumber;
	/**
	 * 用户主键ID
	 */
	private Long userId;

	/**
	 * 用户ID
	 */
	private String ucId;
	/**
	 * 用户手机号
	 */
	@NotNull(message = "phone 不能为空")
	private String phone;
	/**
	 * 经营商户号
	 */
	private String dealerMerId;
	/**
	 * 经营门店号
	 */
	private String dealerStoreId;
	/**
	 * 订单状态：对应字典表中type_code为20
	 */
	@NotNull(message = "orderStatus 不能为空")
	private String orderStatus;
	/**
	 * 实际付款价格（原价-优惠价+配送费）
	 */
	@NotNull(message = "realCost 不能为空")
	private BigDecimal realCost;

	/**
	 * 终端通信号，车辆接口返回 autoTerminal
	 */
	private String simNum;

	/**
	 * 终端ID 车辆接口返回 terminalId
	 */
	private String simId;
	/**
	 * 套餐ID，套餐查询接口 返回 ID
	 */
	private String pacId;
	/**
	 * 套餐名称,套餐查询接口 返回 setMealTitl
	 */
	private String paName;
	/**
	 * 套餐类别,套餐查询接口 返回 套餐类型
	 */
	private String pacType;
	/**
	 * 套餐流量,套餐查询接口 返回 dataUsageSize
	 */
	private String pacFlow;
	/**
	 * 套餐售价,套餐查询接口 返回 price
	 */
	private BigDecimal pacPrice;
	/**
	 * 套餐有效时间,套餐查询接口 返回 periodTypeName
	 */
	private String actiTime;
	/**
	 * 套餐描述,套餐查询接口 返回 setMealContent
	 */
	private String pacDesc;
	/**
	 * 是否已推送消息
	 */
	private Integer pushFlag;
	/**
	 * 完成时间
	 */
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
