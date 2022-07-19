package com.nut.truckingteam.app.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 车辆表
 *
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-21 14:34:16
 */
@Data
@TableName("car")
public class CarEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 车辆ID
	 */
	private String id;
	/**
	 * vin
	 */
	private String carVin;
	/**
	 * 品牌
	 */
	private String carBrand;
	/**
	 * 车系ID
	 */
	private String carSeries;
	/**
	 * 车辆系列名
	 */
	private String carSeriesName;
	/**
	 * 车型ID
	 */
	private String carModel;
	/**
	 * 产品编码
	 */
	private String carModelCode;
	/**
	 * 基础车型
	 */
	private String carModelBase;
	/**
	 * 车辆型号名
	 */
	private String carModelName;
	/**
	 * 发动机类型
	 */
	private String engine;
	/**
	 * 发动机号
	 */
	private String engineNo;
	/**
	 * 是否vip 0 不是 1是
	 */
	private String isVip;
	/**
	 * 车主ID
	 */
	private String ownerId;
	/**
	 * 购车发票照片
	 */
	private String invoicePhoto;
	/**
	 * 发票号
	 */
	private String invoiceNo;
	/**
	 * 身份证号
	 */
	private String identityCard;
	/**
	 * 组织机构号
	 */
	private String organization;
	/**
	 *
	 */
	private Integer addFlag;
	/**
	 * 车牌号
	 */
	private String carNumber;
	/**
	 * 终端ID
	 */
	private String terminalId;
	/**
	 * 通信号
	 */
	private String autoTerminal;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 更新时间
	 */
	private Date updateTime;
	/**
	 * STD时间
	 */
	private Date salesDate;
	/**
	 * 经销商ID
	 */
	private Long tId;
	/**
	 * aak销售状态
	 */
	private Integer mbSalesStatus;
	/**
	 * aak销售日期-经销商到用户
	 */
	private Date mbSalesDate;
	/**
	 * STD销售状态
	 */
	private Integer salesStatus;
	/**
	 * 终端来源(0:东风 1:F9)
	 */
	private Integer tboxType;
	/**
	 * 车辆是否被绑定过，0未被绑定过 1绑定过
	 */
	private String isBind;
	/**
	 * 燃料标识别：1:燃油车；2：燃气车
	 */
	private String fuel;
	/**
	 *
	 */
	private String ps;
	/**
	 *
	 */
	private String emissionCode;
	/**
	 *
	 */
	private String emissionName;
	/**
	 *
	 */
	private Integer definedType;
	/**
	 *
	 */
	private Integer isDefined;
	/**
	 *
	 */
	private String frontAxleFactoryOne;
	/**
	 *
	 */
	private String frontAxleTypeOne;
	/**
	 *
	 */
	private String rearAxleFactoryOne;
	/**
	 *
	 */
	private String rearAxleTypeOne;
	/**
	 *
	 */
	private String frontAxleFactoryTwo;
	/**
	 *
	 */
	private String frontAxleTypeTwo;
	/**
	 *
	 */
	private String rearAxleFactoryTwo;
	/**
	 *
	 */
	private String rearAxleTypeTwo;
	/**
	 *
	 */
	private String vfactory;
	/**
	 *
	 */
	private Integer ishowwater;
	/**
	 * dms同步数据时间
	 */
	private Date dmsSyncTime;
	/**
	 * 备注
	 */
	private String remark;
	/**
	 * 数据来源：0-历史数据、1-DMS同步、2-TSP同步、3-车辆向TA注册、4-手动添加
	 */
	private Integer dataSource;
	/**
	 * 车联网flag  1：带车联网配置；2：不带车联网配置
	 */
	private Integer clwflag;
	/**
	 * 车辆信息是否已经补全, 1: 已经补全
	 */
	private Integer extInfoOk;
	/**
	 * 车系(基础车型)
	 */
	private String vehiclecode;
	/**
	 * 车辆类型(整车平台)
	 */
	private String vehicletype;

}
