package com.nut.driver.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * 
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-23 13:51:15
 */
@Data
@TableName("appointment_items")
public class AppointmentItemsEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Long id;
	/**
	 * 项目名称
	 */
	private String itemName;
	/**
	 * 项目类型 1：维修项，2：保养项
	 */
	private String itemType;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 创建人
	 */
	private String createUser;
	/**
	 * 
	 */
	private Date updateTime;
	/**
	 * 
	 */
	private String updateUser;

}
