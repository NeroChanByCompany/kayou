package com.nut.driver.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 车队与用户绑定关系表
 * 
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-16 15:48:59
 */
@Data
@TableName("flt_fleet_user_mapping")
public class FltFleetUserMappingEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 自增主键
	 */
	@TableId
	private Long id;
	/**
	 * 用户ID
	 */
	private Long userId;
	/**
	 * 车队ID
	 */
	private Long teamId;
	/**
	 * 角色（0：创建者；1：管理员；2：司机）
	 */
	private Integer role;
	/**
	 * 创建来源（1：APP；2：TBOSS）
	 */
	private Integer createType;
	/**
	 * APP创建人ID
	 */
	private Long createUserId;
	/**
	 * TBOSS创建人ID
	 */
	private String tbossUserId;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 更新时间
	 */
	private Date updateTime;

}
