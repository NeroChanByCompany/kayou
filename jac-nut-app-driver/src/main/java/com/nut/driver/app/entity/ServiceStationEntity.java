package com.nut.driver.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import lombok.Data;

/**
 * 服务站
 *
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-21 19:50:45
 */
@Data
@TableName("hy_service_station")
public class ServiceStationEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	@TableId
	private Long id;
	/**
	 * 服务站名称
	 */
	private String stationName;
	/**
	 * 简称
	 */
	private String nameForshort;
	/**
	 * 地址
	 */
	private String address;
	/**
	 * 经度
	 */
	private Long longitude;
	/**
	 * 纬度
	 */
	private Long latitude;
	/**
	 * 半径
	 */
	private Integer radius;
	/**
	 * 省
	 */
	private Integer povince;
	/**
	 * 市
	 */
	private Integer city;
	/**
	 * 服务经理
	 */
	private String serviceManager;
	/**
	 * 电话
	 */
	private String phone;
	/**
	 * 传真
	 */
	private String fax;
	/**
	 * 推送内容
	 */
	private String messages;
	/**
	 * 分组ID
	 */
	private Long teamId;
	/**
	 * 最大滞留时间
	 */
	private Long strandedMaxTime;
	/**
	 * 星级
	 */
	private Integer starLevel;
	/**
	 * 图片地址
	 */
	private String picture;
	/**
	 * 服务类型0服务类(配件)1配件类
	 */
	private Integer serviceType;
	/**
	 * 服务内容，通过_连接多个类容编号
	 */
	private String serviceContent;
	/**
	 * 配件明细，通过_连接多个类容编号
	 */
	private String partsContent;
	/**
	 * 服务器半径
	 */
	private Integer serviceRadius;
	/**
	 *
	 */
	private String serviceCode;
	/**
	 *
	 */
	private String toolContent;
	/**
	 *
	 */
	private String stationType;
	/**
	 * 创建时间
	 */
	private Long creatDate;
	/**
	 * 创建者
	 */
	private Long accountId;
	/**
	 * 固定电话
	 */
	private String fixedTelephone;
	/**
	 *
	 */
	private Integer mbProvinceId;
	/**
	 *
	 */
	private Integer mbCityId;
	/**
	 * 客户编码
	 */
	private String agencyCode;
	/**
	 * 是否上目录
	 */
	private String directoryFlg;
	/**
	 * 有效性
	 */
	private String effctive;

}
