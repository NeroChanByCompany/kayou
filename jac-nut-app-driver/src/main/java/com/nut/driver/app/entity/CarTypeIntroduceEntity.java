package com.nut.driver.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 车型介绍表
 * 
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-21 10:27:33
 */
@Data
@TableName("car_type_introduce")
public class CarTypeIntroduceEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 自增主键
	 */
	@TableId
	private Long id;
	/**
	 * 车型号
	 */
	private String carModel;
	/**
	 * 外观图片
	 */
	private String appearanceImg;
	/**
	 * 品系
	 */
	private String strains;
	/**
	 * 市场区隔
	 */
	private String marketSeparate;
	/**
	 * 平台
	 */
	private String platform;
	/**
	 * 驱动形式
	 */
	private String driverType;
	/**
	 * 重点对应行业
	 */
	private String industry;
	/**
	 * 车型系列
	 */
	private String series;
	/**
	 * 市场指导价
	 */
	private String guidancePrice;
	/**
	 * 整车重量
	 */
	private String vehicleWeight;
	/**
	 * 公告最大吨位
	 */
	private String maxTonnage;
	/**
	 * 整车箱长
	 */
	private String boxLength;
	/**
	 * 驾驶式类型
	 */
	private String cabType;
	/**
	 * 驾驶室翻转
	 */
	private String cabFlip;
	/**
	 * 空调
	 */
	private String isAirConditioner;
	/**
	 * 电动门窗
	 */
	private String isElectricWindow;
	/**
	 * 发动机-发动机型号
	 */
	private String engineModel;
	/**
	 * 发动机-排量
	 */
	private String displacement;
	/**
	 * 发动机-排放标准
	 */
	private String emissionStandards;
	/**
	 * 发动机-技术路线
	 */
	private String technologyRoute;
	/**
	 * 发动机-燃料种类
	 */
	private String fuelType;
	/**
	 * 发动机-发动机制动
	 */
	private String isBrake;
	/**
	 * 发动机-马力
	 */
	private String horsePower;
	/**
	 * 发动机-扭矩
	 */
	private String torque;
	/**
	 * 变速箱-变速箱型号
	 */
	private String transmissionModel;
	/**
	 * 变速箱-换挡方式
	 */
	private String gearType;
	/**
	 * 变速箱-最高/低档速比
	 */
	private String speedRatio;
	/**
	 * 前桥
	 */
	private String frontAxle;
	/**
	 * 中后桥型号
	 */
	private String middleRearAxleModel;
	/**
	 * 桥速比
	 */
	private String bridgeSpeedRatio;
	/**
	 * 车架
	 */
	private String frame;
	/**
	 * 轴距
	 */
	private String wheelbase;
	/**
	 * 悬架（板簧片数）
	 */
	private String supension;
	/**
	 * 油箱容量
	 */
	private String tankCapacity;
	/**
	 * 轮胎规格
	 */
	private String tireSpecification;
	/**
	 * 滚动半径
	 */
	private String rollingRadius;
	/**
	 * 轮胎品牌
	 */
	private String tireBrand;
	/**
	 * 轮胎花纹
	 */
	private String tirePattern;
	/**
	 * 轮辋
	 */
	private String rim;
	/**
	 * 制动器
	 */
	private String brake;
	/**
	 * 主动安全系统-车道偏离LDWS
	 */
	private String laneDeviate;
	/**
	 * 主动安全系统-碰撞预警FCWS
	 */
	private String collisionEarlyWarning;
	/**
	 * 主动安全系统-盲点检测BSD
	 */
	private String blindSpotTest;
	/**
	 * 主动安全系统-环景影像AVM
	 */
	private String ringViewImage;
	/**
	 * 主动安全系统-胎压监测TPMS
	 */
	private String tirePressureTest;
	/**
	 * 主动安全系统-疲劳检测DMS
	 */
	private String fatigueDetection;
	/**
	 * 主动安全系统-车辆紧急制动AEBS
	 */
	private String vehicleEmergencyBraking;
	/**
	 * 主动安全系统-自适应巡航ACC
	 */
	private String adaptiveCruise;
	/**
	 * 主动安全系统-车道保持LKA
	 */
	private String laneKeep;
	/**
	 * 主动安全系统-电子制动控制EBS
	 */
	private String electronicBrakeControl;
	/**
	 * 主动安全系统-电子稳定性控制ESC
	 */
	private String electStabilityControl;
	/**
	 * 轮间差速锁
	 */
	private String diffSpeedLock;
	/**
	 * 液力缓速器
	 */
	private String hydraulicRetarder;
	/**
	 * 北方版
	 */
	private String northVersion;
	/**
	 * 南方版
	 */
	private String sourthVersion;
	/**
	 * 居家包
	 */
	private String homePackage;
	/**
	 * 时尚包
	 */
	private String fashionPackage;
	/**
	 * 车型分类-使用路面
	 */
	private String usePavement;
	/**
	 * 道路条件
	 */
	private String dlCondition;
	/**
	 * 地形条件
	 */
	private String dxCondition;
	/**
	 * 车型分类-最佳经济转速
	 */
	private String optimumEconomyRotarySpeed;
	/**
	 * 车型分类-经济车速
	 */
	private String economySpeed;
	/**
	 * 其它配置
	 */
	private String otherConfig;
	/**
	 * 最新公告
	 */
	private String newNotice;
	/**
	 * 销售状态
	 */
	private String saleStatus;
	/**
	 * 车货总重(T)
	 */
	private String cargoWeight;
	/**
	 * 年运营里程（KM）
	 */
	private String mileageByYear;
	/**
	 * 车辆用途
	 */
	private String vehicleUsage;
	/**
	 * 单趟运距
	 */
	private String singleTripMileage;
	/**
	 * 自用or他用
	 */
	private String selfOrOther;
	/**
	 * 运营工况
	 */
	private String workCondition;
	/**
	 * 常用车速
	 */
	private String commonSpeed;
	/**
	 * 环绕式仪表盘
	 */
	private String surroundMeter;
	/**
	 * 铝合金传动轴
	 */
	private String alloyDriveShaft;
	/**
	 * 涉水桥
	 */
	private String wadeBridge;
	/**
	 * 发动机制动
	 */
	private String engineBreak;
	/**
	 * 东风阳光智联中后视镜
	 */
	private String dfRearViewMirror;
	/**
	 * 多媒体9寸大屏
	 */
	private String multimediaScreen;
	/**
	 * 取力器
	 */
	private String powerTakeOff;
	/**
	 * 手动or自动
	 */
	private String mtOrAt;
	/**
	 * 液力缓速器
	 */
	private String hydraulicRetarder2;
	/**
	 * 工况版本
	 */
	private String extraConfig;
	/**
	 * 厢长
	 */
	private String carriageLength;
	/**
	 * 单卧or双卧
	 */
	private String singleOrDouble;
	/**
	 * 公路or工地
	 */
	private String roadOrConstruction;
	/**
	 * 驱动
	 */
	private String driver;
	/**
	 * 小品系
	 */
	private String xpxApp;
	/**
	 * 是否显示
	 */
	private Integer isShow;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 更新时间
	 */
	private Date updateTime;
	/**
	 * 新老车型标识
	 */
	private String tcApp;
	/**
	 * 车速变化
	 */
	private String speedChange;
	/**
	 * 海拔高度
	 */
	private String hbgd;
	/**
	 * 环境温度
	 */
	private String hjwd;
	/**
	 * 砂尘环境
	 */
	private String schj;
	/**
	 * 车货总重字段描述建议
	 */
	private String chzzSuggest;
	/**
	 * 车货总重对车辆的影响
	 */
	private String chzzEffect;
	/**
	 * 道路条件字段描述建议
	 */
	private String dltjSuggest;
	/**
	 * 道路条件对车辆的影响
	 */
	private String dltjEffect;
	/**
	 * 地形条件字段描述建议
	 */
	private String dxtjSuggest;
	/**
	 * 地形条件对车辆的影响
	 */
	private String dxtjEffect;
	/**
	 * 砂尘环境字段描述建议
	 */
	private String schjSuggest;
	/**
	 * 砂尘环境对车辆的影响
	 */
	private String schjEffect;
	/**
	 * 车型标签
	 */
	private String prodTag;
	/**
	 * 可选颜色
	 */
	private String prodColor;

}
