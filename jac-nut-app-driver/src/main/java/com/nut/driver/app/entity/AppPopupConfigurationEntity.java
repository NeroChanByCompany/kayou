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
 * @date 2021-06-28 09:40:50
 */
@Data
@TableName("app_popup_configuration")
public class AppPopupConfigurationEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 弹屏主键id
	 */
	@TableId
	private Long id;
	/**
	 * APP端类型（1 客户App）
	 */
	private String appType;
	/**
	 * 标题
	 */
	private String title;
	/**
	 * 图片地址
	 */
	private String picUrl;
	/**
	 * 链接地址
	 */
	private String popupLink;
	/**
	 * 显示时间
	 */
	private Date displayTime;
	/**
	 * 结束时间
	 */
	private Date endTime;
	/**
	 * 点击数
	 */
	private Long hitsNumber;
	/**
	 * 删除标记（0 未删除 1 删除）
	 */
	private String delFlag;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 更新时间
	 */
	private Date updateTime;
	/**
	 * 创建人id
	 */
	private String createUserId;
	/**
	 * 开屏页停留时间,单位，秒
	 */
	private String stopTime;

}
