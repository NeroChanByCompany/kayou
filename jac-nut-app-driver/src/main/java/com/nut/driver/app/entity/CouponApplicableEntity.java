package com.nut.driver.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 适用车型或适用用户表
 * 
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-18 15:50:56
 */
@Data
@TableName("coupon_applicable")
public class CouponApplicableEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 主键ID
	 */
	@TableId
	private Long id;
	/**
	 * 优惠劵ID
	 */
	private Long infoId;
	/**
	 * 适用类型，1:车系设置 2:指定底盘号 3:指定车型号 4:指定手机号 8:首保券
	 */
	private String applicableType;
	/**
	 * 适用号码，不限-1
	 */
	private String applicableNumber;
	/**
	 * 排放，不限为-1
	 */
	private String applicableVehicleEmission;
	/**
	 * 型号，不限为-1
	 */
	private String applicableVehicleModel;
	/**
	 * 平台，不限为-1
	 */
	private String applicableVehiclePlatform;
	/**
	 * 驱动，不限为-1
	 */
	private String applicableVehicleDrive;
	/**
	 * 排量，不限为-1
	 */
	private String applicableVehicleDisplacement;
	/**
	 * 创建时间
	 */
	private Date createTime;

}
