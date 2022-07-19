package com.nut.driver.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 服务活动及返修通知数据同步表
 * 
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-22 10:41:14
 */
@Data
@TableName("service_aar_notice")
public class ServiceAarNoticeEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 主键ID
	 */
	@TableId
	private Long id;
	/**
	 * 活动编号
	 */
	private String noticeCode;
	/**
	 * 活动主题
	 */
	private String noticeName;
	/**
	 * 活动类型（返修/服务）
	 */
	private String noticeType;
	/**
	 * 截至日期
	 */
	private Date endDt;
	/**
	 * 校验服务站（N/Y）
	 */
	private String chkService;
	/**
	 * 校验车辆（N/Y）
	 */
	private String checkVeh;
	/**
	 * 鉴定章号
	 */
	private String identNum;
	/**
	 * 服务站名称
	 */
	private String stationName;
	/**
	 * 服务站ID
	 */
	private String orgId;
	/**
	 * 最新底盘号
	 */
	private String chassisNoFinal;
	/**
	 * 原始底盘号
	 */
	private String chassisNo;
	/**
	 * 车型
	 */
	private String vehicleModel;
	/**
	 * 可执行次数
	 */
	private String caNum;
	/**
	 * 已执行次数
	 */
	private String alNum;
	/**
	 * 有效标记（N/Y）
	 */
	private String effectiveFlag;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 修改时间
	 */
	private Date updateTime;

}
