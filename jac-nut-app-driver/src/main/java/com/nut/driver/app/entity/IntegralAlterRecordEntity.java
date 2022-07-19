package com.nut.driver.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 积分变更记录
 * 
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-21 14:41:08
 */
@Data
@TableName("integral_alter_record")
public class IntegralAlterRecordEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 自增主键
	 */
	@TableId
	private Long id;
	/**
	 * 用户唯一性标识，唯一且不可变
	 */
	private String uid;
	/**
	 * 本次变更积分
	 */
	private String credits;
	/**
	 * 0消费1获取
	 */
	private String type;
	/**
	 * 积分项
	 */
	private String integralItem;
	/**
	 * 积分发放段 2运营 3服务
	 */
	private String integralResource;
	/**
	 * 订单号
	 */
	private String orderNum;
	/**
	 * 积分余额
	 */
	private Integer balance;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 更新时间
	 */
	private Date updateTime;

}
