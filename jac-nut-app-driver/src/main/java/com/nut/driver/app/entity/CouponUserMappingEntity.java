package com.nut.driver.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 优惠券与用户关系表
 * 
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-18 15:50:56
 */
@Data
@TableName("coupon_user_mapping")
public class CouponUserMappingEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 主键ID
	 */
	@TableId
	private Long cumId;
	/**
	 * 代金券ID
	 */
	private Long infoId;
	/**
	 * 用户ID
	 */
	private Long userId;
	/**
	 * 券码
	 */
	private String cumNumber;
	/**
	 * 使用标识，1待使用 2已使用 3已过期
	 */
	private String cumStatus;
	/**
	 * 发放网点ID
	 */
	private Long giveBranchId;
	/**
	 * 发放网点类型，1为经销商 2为服务站
	 */
	private String giveBranchType;
	/**
	 * 获得券时间
	 */
	private String cumGiveTime;
	/**
	 * 兑换时间
	 */
	private String cumExchangeTime;
	/**
	 * 兑换网点ID
	 */
	private Long exchangeBranchId;
	/**
	 * 兑换网点类型，1为经销商 2为服务站
	 */
	private String exchangeBranchType;
	/**
	 * 审批提交时间
	 */
	private String cumApprovalTime;
	/**
	 * 审批状态，1已提交 2已完成 3审核未通过
	 */
	private String cumApprovalStatus;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 绑车送券底盘号
	 */
	private String cumVin;
	/**
	 * 购车优惠券绑定底盘号
	 */
	private String carCouponsVin;
	/**
	 * 审核完成时间
	 */
	private String cumCompleteTime;
	/**
	 * 拒绝内容
	 */
	private String rejectContent;
	/**
	 * 购车积分-id
	 */
	private Long scoreId;
	/**
	 * 订单号
	 */
	private String orderId;
	/**
	 * 是否新手机号注册状态：1 新注册
	 */
	private String newRegStatus;
	/**
	 * 新手机号注册
	 */
	private String newRegPhone;
	/**
	 * 优惠券有效期类型,1:不限 2:收券之后几天内 3:自定义
	 */
	private String infoValid;
	/**
	 * 优惠券有效期开始时间，格式yyyy-MM-dd
	 */
	private String infoValidStartDate;
	/**
	 * 优惠券有效期结束时间，格式yyyy-MM-dd
	 */
	private String infoValidEndDate;
	/**
	 * 当优valid等于2时，惠券有效期几天内
	 */
	private String infoValidDay;

}
