package com.nut.driver.app.entity;

import com.baomidou.mybatisplus.annotation.TableField;
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
 * @date 2021-06-18 13:35:38
 */
@Data
@TableName("app_version")
public class ActionPictureSetEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 自增ID
	 */
	@TableId
	private Long id;
	/**
	 * 事件code
	 */
	private String actionCode;
	/**
	 * 事件名称
	 */
	private String actionName;
	/**
	 * 图片url
	 */
	private String pictureUrl;
	/**
	 * 显示顺序
	 */
	@TableField(exist = false)
	private String order;
	/**
	 * 内部版本号
	 */
	private String inVersion;
	/**
	 * 外部版本号
	 */
	private String outVersion;
	/**
	 * 说明
	 */
	@TableField(exist = false)
	private String describe;
	/**
	 * 文件大小
	 */
	private String fileSize;
	/**
	 * 1android 2 ios
	 */
	private String type;
	/**
	 * 更新时间
	 */
	private Date updateTime;
	/**
	 * 创建时间
	 */
	private Date careteTime;

}
