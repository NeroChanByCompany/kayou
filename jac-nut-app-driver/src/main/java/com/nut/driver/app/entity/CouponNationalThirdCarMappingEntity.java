package com.nut.driver.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 国三优惠券绑定国三车关系表
 * 
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-21 10:11:15
 */
@Data
@TableName("coupon_national_third_car_mapping")
public class CouponNationalThirdCarMappingEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Long id;
	/**
	 * 用户ID
	 */
	private Long userId;
	/**
	 * 国三换购优惠券id
	 */
	private Long cumId;
	/**
	 * 车辆ID
	 */
	private String carId;
	/**
	 * 发票号码/号牌号码
	 */
	private String number;
	/**
	 * 车辆类型 
	 */
	private String vehicleType;
	/**
	 * 所有人
	 */
	private String name;
	/**
	 * 住址
	 */
	private String address;
	/**
	 * 使用性质
	 */
	private String useCharacter;
	/**
	 * 品牌型号
	 */
	private String model;
	/**
	 * 发动机号码
	 */
	private String engineno;
	/**
	 * 车辆识别代号
	 */
	private String vin;
	/**
	 * 注册日期
	 */
	private String registerDate;
	/**
	 * 档案编码
	 */
	private String fileno;
	/**
	 * 核定载人数 
	 */
	private String approvedPassengers;
	/**
	 * 总质量
	 */
	private String grossMass;
	/**
	 * 整备质量
	 */
	private String unladenMass;
	/**
	 * 核定载质量
	 */
	private String approvedLoad;
	/**
	 * 外廓尺寸
	 */
	private String dimension;
	/**
	 * 准牵引总质量
	 */
	private String tractionMass;
	/**
	 * 备注
	 */
	private String remarks;
	/**
	 * 检验记录
	 */
	private String inspectionRecord;
	/**
	 * 条码号
	 */
	private String codeNumber;
	/**
	 * 底盘号
	 */
	private String chassisno;
	/**
	 * 身份证号或者组织机构号或者车牌号
	 */
	private String identityCard;
	/**
	 * 是否是驾驶证照片(1（驾驶证照片），0(购车发票照片))
	 */
	private String isDrive;
	/**
	 * 是否换购(1换购，0未换购)
	 */
	private String isValid;
	/**
	 * 图片地址
	 */
	private String picturePath;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 更新时间
	 */
	private Date updateTime;

}
