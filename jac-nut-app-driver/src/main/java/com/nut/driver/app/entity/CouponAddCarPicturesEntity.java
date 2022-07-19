package com.nut.driver.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 优惠券加车照片表
 * 
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-18 15:50:56
 */
@Data
@TableName("coupon_add_car_pictures")
public class CouponAddCarPicturesEntity implements Serializable {

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
	 * 车辆ID
	 */
	private String carId;
	/**
	 * 发票代码
	 */
	private String code;
	/**
	 * 发票号码/号牌号码
	 */
	private String number;
	/**
	 * 机打代码
	 */
	private String machinePrintedCode;
	/**
	 * 机打号码
	 */
	private String machinePrintedNumber;
	/**
	 * 开票日期/发证日期
	 */
	private String issueDate;
	/**
	 * 机器编号
	 */
	private String machineNumber;
	/**
	 * 购买方名称
	 */
	private String buyerName;
	/**
	 * 购买方身份证号码/组织机构代码
	 */
	private String buyerOrganizationNumber;
	/**
	 * 购买方纳税人识别号
	 */
	private String buyerId;
	/**
	 * 销货单位名称
	 */
	private String sellerName;
	/**
	 * 销售方电话
	 */
	private String sellerPhone;
	/**
	 * 销售方纳税人识别号
	 */
	private String sellerId;
	/**
	 * 销售方账号
	 */
	private String sellerAccount;
	/**
	 * 销售方地址
	 */
	private String sellerAddress;
	/**
	 * 销售方开户行
	 */
	private String sellerBank;
	/**
	 * 车辆类型 
	 */
	private String vehicleType;
	/**
	 * 厂牌型号
	 */
	private String brandModel;
	/**
	 * 产地
	 */
	private String manufacturingLocation;
	/**
	 * 合格证号
	 */
	private String qualityCertificate;
	/**
	 * 进口证明书号
	 */
	private String importCertificate;
	/**
	 * 商检单号
	 */
	private String inspectionNumber;
	/**
	 * 发动机号码
	 */
	private String engineNumber;
	/**
	 * 车辆识别代号/车架号码
	 */
	private String vehicleIdentificationNumber;
	/**
	 * 吨位
	 */
	private String tonnage;
	/**
	 * 限乘人数
	 */
	private String seatingCapacity;
	/**
	 * 主管税务机关
	 */
	private String taxAuthority;
	/**
	 * 主管税务机关代码
	 */
	private String taxAuthorityCode;
	/**
	 * 完税凭证号码
	 */
	private String taxPaymentReceipt;
	/**
	 * 增值税税率或征收率
	 */
	private String taxRate;
	/**
	 * 增值税税额
	 */
	private String tax;
	/**
	 * 不含税价
	 */
	private String taxExclusivePrice;
	/**
	 * 价税合计
	 */
	private String total;
	/**
	 * 价税合计大写
	 */
	private String totalChinese;
	/**
	 * 税控码
	 */
	private String fiscalCode;
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
	 * 是否通过(1通过，0未通过)
	 */
	private String isValid;
	/**
	 * 图片地址
	 */
	private String picturePath;
	/**
	 * 审核人
	 */
	private String auditor;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 更新时间
	 */
	private Date updateTime;

}
