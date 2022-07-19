package com.nut.truckingteam.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 车与车主绑定关系表
 *
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-28 13:12:38
 */
@Data
@TableName("flt_car_owner_mapping")
public class FltCarOwnerMappingEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 自增主键
	 */
	@TableId
	private Long id;
	/**
	 * 车辆ID
	 */
	private String carId;
	/**
	 * 用户ID
	 */
	private Long userId;
	/**
	 * 创建来源（1：APP；2：TBOSS）
	 */
	private Integer createType;
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
