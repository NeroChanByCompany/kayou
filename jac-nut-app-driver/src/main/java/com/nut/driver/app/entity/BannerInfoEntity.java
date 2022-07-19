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
 * @date 2021-06-28 15:29:42
 */
@Data
@TableName("banner_info")
public class BannerInfoEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Integer id;
	/**
	 * APP端版本
	 */
	private String appType;
	/**
	 * 类型
	 */
	private String bannerType;
	/**
	 * 名称
	 */
	private String bannerName;
	/**
	 * 状态
	 */
	private String bannerStatus;
	/**
	 * 位置
	 */
	private Integer bannerIndex;
	/**
	 * 图片路径
	 */
	private String imgPath;
	/**
	 * 
	 */
	private String bannerLink;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 删除标记
	 */
	private String delFlag;

}
