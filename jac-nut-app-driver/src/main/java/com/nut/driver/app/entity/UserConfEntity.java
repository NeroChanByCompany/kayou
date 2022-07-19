package com.nut.driver.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户配置表
 * 
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-27 15:47:51
 */
@Data
@TableName("user_conf")
public class UserConfEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Long id;
	/**
	 * 用户id
	 */
	private Long userId;
	/**
	 * 主题
	 */
	private String topic;
	/**
	 * 名称
	 */
	private String key;
	/**
	 * 单位
	 */
	private String value;
	/**
	 * 创建时间
	 */
	private Date createdTime;
	/**
	 * 最后修改时间
	 */
	private Date updatedTime;

}
