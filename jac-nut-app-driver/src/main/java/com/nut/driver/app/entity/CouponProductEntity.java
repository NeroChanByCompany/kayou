package com.nut.driver.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 优惠券兑换商品
 * 
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-18 15:50:57
 */
@Data
@TableName("coupon_product")
public class CouponProductEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 主键ID
	 */
	@TableId
	private Long id;
	/**
	 * 优惠券ID
	 */
	private Long infoId;
	/**
	 * 商品ID
	 */
	private String productId;
	/**
	 * 创建时间
	 */
	private Date createTime;

}
