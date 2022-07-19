package com.nut.driver.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 进站超时预警表
 * 
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-22 11:00:52
 */
@Data
@TableName("car_station_stay_overtime")
public class CarStationStayOvertimeEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 自增主键
	 */
	@TableId
	private Long id;
	/**
	 * 服务站编码
	 */
	private String staCode;
	/**
	 * 车辆ID
	 */
	private String carId;
	/**
	 * 进入时间
	 */
	private Date inTime;
	/**
	 * 预警级别（1：橙色预警；2：红色预警）
	 */
	private Integer warnLevel;
	/**
	 * 车辆是否有工单（0：否；1：是）
	 */
	private Integer withWork;
	/**
	 * 回访状态（0：否；1：是）
	 */
	private Integer status;
	/**
	 * 下次回访时间
	 */
	private Date putOffTill;
	/**
	 * 预警解除时间
	 */
	private Date releaseTime;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 更新时间
	 */
	private Date updateTime;

}
