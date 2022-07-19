package com.nut.driver.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 抽奖用户
 * 
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-29 10:43:26
 */
@Data
@TableName("activity_lucky_user")
public class ActivityLuckyUserEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Long id;
	/**
	 * 活动代码
	 */
	private String activeCode;
	/**
	 * 抽奖手机号
	 */
	private String phone;
	/**
	 * 奖品代码
	 */
	private String awardCode;
	/**
	 * 邮寄姓名
	 */
	private String receiveName;
	/**
	 * 邮寄电话
	 */
	private String receivePhone;
	/**
	 * 邮寄地址
	 */
	private String receiveAddress;
	/**
	 * 备注
	 */
	private String remark;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 更新时间
	 */
	private Date updateTime;
	/**
	 * APP类型,0,司机端，1，车队端
	 */
	private String appType;

}
