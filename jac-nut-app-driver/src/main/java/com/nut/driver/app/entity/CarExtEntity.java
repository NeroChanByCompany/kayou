package com.nut.driver.app.entity;



import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 车辆扩展表
 */
@Data
@TableName("car_ext")
public class CarExtEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 车辆表 id
	 */
	private Long carId;
	/**
	 * 车辆vin
	 */
	private String vin;
	/**
	 * 车牌号
	 */
	private String license;
	/**
	 * 车辆身份
	 */
	private String identity;
	/**
	 * 品牌
	 */
	private String brand;
	/**
	 * 车系
	 */
	private String series;
	/**
	 * 发动机号
	 */
	private String engineNum;
	/**
	 * 车辆颜色
	 */
	private String color;
	/**
	 * 车辆用途
	 */
	private Integer vehicleUse;
	/**
	 * 行业
	 */
	private String industry;
	/**
	 * 货物类型
	 */
	private String typeOfGoods;
	/**
	 * 货车类型
	 */
	private String typeOfVan;
	/**
	 * 核定载重（吨）
	 */
	private String ratedLoad;
	/**
	 * 货车重量(吨）
	 */
	private String weight;
	/**
	 * 货车长度（米）
	 */
	private String length;
	/**
	 * 货车宽度(米）
	 */
	private String width;
	/**
	 * 保险到期时间
	 */
	private Date insuranceDate;
	/**
	 * 保险金额
	 */
	private String insuredAmount;
	/**
	 * 保险公司
	 */
	private String insuranceCompany;
	/**
	 * 换车周期(年）
	 */
	private String transferCycle;

	/**
	 * 创建时间
	 */
	private Date createdTime;

	/**
	 * 最后修改时间
	 */
	private Date updatedTime;



	@Override
	public String toString() {
		return "CarExtEntity{" +
				"carId=" + carId +
				", vin='" + vin + '\'' +
				", license='" + license + '\'' +
				", identity='" + identity + '\'' +
				", brand='" + brand + '\'' +
				", series='" + series + '\'' +
				", engineNum='" + engineNum + '\'' +
				", color='" + color + '\'' +
				", vehicleUse=" + vehicleUse +
				", industry='" + industry + '\'' +
				", typeOfGoods='" + typeOfGoods + '\'' +
				", typeOfVan='" + typeOfVan + '\'' +
				", ratedLoad='" + ratedLoad + '\'' +
				", weight='" + weight + '\'' +
				", length='" + length + '\'' +
				", width='" + width + '\'' +
				", insuranceDate=" + insuranceDate +
				", insuredAmount='" + insuredAmount + '\'' +
				", insuranceCompany='" + insuranceCompany + '\'' +
				", transferCycle='" + transferCycle + '\'' +
				", createdTime=" + createdTime +
				", updatedTime=" + updatedTime +
				'}';
	}
}
