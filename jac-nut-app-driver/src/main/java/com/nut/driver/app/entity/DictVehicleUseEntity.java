package com.nut.driver.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 车辆用途字段表
 * 
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-29 09:29:42
 */
@Data
@TableName("dict_vehicle_use")
public class DictVehicleUseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * code
	 */
	@TableId
	private Long code;
	/**
	 * name
	 */
	private String name;
	/**
	 * 创建时间
	 */
	private Date createdTime;
	/**
	 * 最后修改时间
	 */
	private Date updatedTime;

}
