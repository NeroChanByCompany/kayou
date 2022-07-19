package com.nut.driver.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 优惠券主表
 * 
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-18 15:50:56
 */
@Data
@TableName("coupon_info")
public class CouponInfoEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 主键ID
	 */
	@TableId
	private Long id;
	/**
	 * 优惠券类型,1:线下优惠劵 2:购车代金券 3:线上代金券
	 */
	private String infoType;
	/**
	 * 优惠券名称
	 */
	private String infoName;
	/**
	 * 优惠券内容
	 */
	private String infoContent;
	/**
	 * 优惠券面额，大于0
	 */
	private String infoValue;
	/**
	 * 满减面额，大于0
	 */
	private String infoFullDecre;
	/**
	 * 优惠券有效期类型,1:不限 2:收券之后几天内 3:自定义
	 */
	private String infoValid;
	/**
	 * 当优valid等于2时，惠券有效期几天内
	 */
	private String infoValidDay;
	/**
	 * 当优valid等于3时，自定义开始时间，格式yyyy-MM-dd
	 */
	private String infoValidStartDate;
	/**
	 * 当优valid等于3时，自定义结束时间，格式yyyy-MM-dd
	 */
	private String infoValidEndDate;
	/**
	 * 优惠券总数类型，1:不限 2:自定义
	 */
	private String infoTotalCountType;
	/**
	 * 优惠券总数，当totalCountType等于2时填写
	 */
	private String infoTotalCount;
	/**
	 * 使用规则说明
	 */
	private String infoRule;
	/**
	 * 是否国三：0否 1是
	 */
	private Long isNationalThird;
	/**
	 * 适用类型，1:车系设置 2:指定底盘号 3:指定车型号 4:指定手机号 5:指定终端类型 6:指定手机号（仅限购车代金券） 8:首保券
	 */
	private String applicableType;
	/**
	 * 发放类型，1：网点发放  2：非网点发放
	 */
	private String giveType;
	/**
	 * 发放开始时间
	 */
	private String giveTimeStart;
	/**
	 * 发放结束时间
	 */
	private String giveTimeEnd;
	/**
	 * 触发事件，1：客户绑车
	 */
	private String giveTrigger;
	/**
	 * 兑换类型，1：同发放网点一致， 2：定义兑换网点
	 */
	private String exchangeType;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 状态，1:可用 2:停发 3:停兑 4:停发停兑
	 */
	private String infoStatus;
	/**
	 * 修改时间
	 */
	private String updateTime;
	/**
	 * 修改tboss用户名
	 */
	private String updateUser;
	/**
	 * 兑换商品类型1所有2特定
	 */
	private String productType;

}
