package com.nut.driver.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 车辆与车管对应关系
 * 
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-21 20:15:25
 */
@Data
@TableName("maintain_vehicle_tube")
public class MaintainVehicleTubeEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@TableId
	private Integer id;
	/**
	 * 底盘号
	 */
	private String chassisNo;
	/**
	 * 车牌号
	 */
	private String licenseNo;
	/**
	 * 总公司id
	 */
	private String companyId;
	/**
	 * 总公司名称
	 */
	private String company;
	/**
	 * 分拨区id
	 */
	private String ereaId;
	/**
	 * 分拨区名称
	 */
	private String erea;
	/**
	 * 送修预约审核手机号
	 */
	private String mobile;
	/**
	 * 方案单审核手机号
	 */
	private String projectMobile;
	/**
	 * 结算单审核手机号
	 */
	private String clearingMobile;
	/**
	 * crm车辆id
	 */
	private String trukId;
	/**
	 * 是否可用：0：可用  1：不可用
	 */
	private Integer deleteFlag;
	/**
	 * 创建时间
	 */
	private Date createDate;
	/**
	 * 更新时间
	 */
	private Date updateDate;

}
