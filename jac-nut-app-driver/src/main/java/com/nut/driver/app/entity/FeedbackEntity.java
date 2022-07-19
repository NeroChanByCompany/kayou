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
 * @date 2021-06-28 09:57:39
 */
@Data
@TableName("feedback")
public class FeedbackEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 用户ID
	 */
	@TableId
	private Long id;
	/**
	 * 意见反馈内容
	 */
	private String message;
	/**
	 * 
	 */
	private String phone;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 更新时间
	 */
	private Date updateTime;
	/**
	 * 创建者
	 */
	private String createUser;
	/**
	 * 修改者
	 */
	private String updateUser;

}
