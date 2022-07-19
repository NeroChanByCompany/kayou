package com.nut.driver.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * 
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-16 15:49:32
 */
@Data
@TableName("work_order")
public class WorkOrderEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 自增主键
	 */
	@TableId
	private Long id;
	/**
	 * 工单号
	 */
	private String woCode;
	/**
	 * 工单状态
	 */
	private Integer woStatus;
	/**
	 * 工单类型(1：进出站、2：外出救援)
	 */
	private Integer woType;
	/**
	 * 协议工单标识(默认1：非协议；2：协议)
	 */
	private Integer protocolMark;
	/**
	 * 建单时间
	 */
	private Date timeCreate;
	/**
	 * 接单时间
	 */
	private Date timeAccept;
	/**
	 * 出发时间
	 */
	private Date timeDepart;
	/**
	 * 预计到达时间
	 */
	private Date timeArriveExpected;
	/**
	 * 接车时间
	 */
	private Date timeReceive;
	/**
	 * 开始检查时间
	 */
	private Date timeInspectBegin;
	/**
	 * 检查结束时间
	 */
	private Date timeInspected;
	/**
	 * 维修方案确定时间
	 */
	private Date timeRepairPhoto;
	/**
	 * 工单结束时间（维修完成或客服同意关闭）
	 */
	private Date timeClose;
	/**
	 * 申请拒单时间
	 */
	private Date timeApplyrefuse;
	/**
	 * 申请修改时间
	 */
	private Date timeApplymodify;
	/**
	 * 申请关闭时间
	 */
	private Date timeApplyclose;
	/**
	 * 被指派人员ID
	 */
	private String assignTo;
	/**
	 * 申请拒单类型编码
	 */
	private Integer refuseType;
	/**
	 * 申请拒单理由
	 */
	private String refuseReason;
	/**
	 * 申请拒单次数
	 */
	private Integer refuseTimes;
	/**
	 * 申请关闭类型编码
	 */
	private Integer closeType;
	/**
	 * 申请关闭理由
	 */
	private String closeReason;
	/**
	 * 申请关闭次数
	 */
	private Integer closeTimes;
	/**
	 * VIN（目前car表car_vin存的是底盘号）
	 */
	private String chassisNum;
	/**
	 * 服务站ID
	 */
	private String stationId;
	/**
	 * 服务站代码
	 */
	private String stationCode;
	/**
	 * 服务站名称
	 */
	private String stationName;
	/**
	 * 服务站省市编码
	 */
	private String areaCode;
	/**
	 * 预约服务站ID
	 */
	private String appoStationId;
	/**
	 * 预约到站时间
	 */
	private Date appoArriveTime;
	/**
	 * 预约方式
	 */
	private Integer appoType;
	/**
	 * 预约人ID
	 */
	private Long appoUserId;
	/**
	 * 预约人姓名
	 */
	private String appoUserName;
	/**
	 * 预约人电话
	 */
	private String appoUserPhone;
	/**
	 * 送修人姓名
	 */
	private String sendToRepairName;
	/**
	 * 送修人电话
	 */
	private String sendToRepairPhone;
	/**
	 * 车辆经度
	 */
	private String carLon;
	/**
	 * 车辆纬度
	 */
	private String carLat;
	/**
	 * 车辆位置
	 */
	private String carLocation;
	/**
	 * 车与服务站距离（米）
	 */
	private String carDistance;
	/**
	 * 车与服务站距离（米，拍照时）
	 */
	private String carStationDistance;
	/**
	 * 人与车距离（米，拍照时）
	 */
	private String manCarDistance;
	/**
	 * 人与服务站距离（米，拍照时）
	 */
	private String manStationDistance;
	/**
	 * 人站距离限制（1：距离限制；2：异常审核中；3：距离不限制）
	 */
	private Integer appStationLimit;
	/**
	 * 人车距离限制（1：距离限制；2：异常审核中；3：距离不限制）
	 */
	private Integer appCarLimit;
	/**
	 * tbox连接状态（1：断连，2：连接正常，3：其他）
	 */
	private Integer tboxConnectStatus;
	/**
	 * 维修记录（1：维修，2：不维修）
	 */
	private Integer tboxRepairRecord;
	/**
	 * 是否提示断联维修（0：否；1：是）
	 */
	private Integer tboxRepairAlert;
	/**
	 * 维修项目
	 */
	private String repairItem;
	/**
	 * 保养项目
	 */
	private String maintainItem;
	/**
	 * 注册手机号
	 */
	private String registeredPhone;
	/**
	 * 用户反馈
	 */
	private String userComment;
	/**
	 * 行驶里程km
	 */
	private String mileage;
	/**
	 * 客户等级(暂时无用)
	 */
	private Integer customerLevel;
	/**
	 * 申请修改理由
	 */
	private String modifyReason;
	/**
	 * 申请修改次数
	 */
	private Integer modifyTimes;
	/**
	 * 外出人员姓名
	 */
	private String rescueStaffName;
	/**
	 * 外出人员电话
	 */
	private String rescueStaffPhone;
	/**
	 * 外出人数
	 */
	private Integer rescueStaffNum;
	/**
	 * 救援车辆GPS设备号
	 */
	private String rescueCarDevice;
	/**
	 * 二次救援车辆GPS设备号
	 */
	private String secondRescueCarDevice;
	/**
	 * 接车异常标志(1：接车异常；0：正常)
	 */
	private Integer isAbnormalReceive;
	/**
	 * 外出救援出发时设备ID
	 */
	private String deviceId;
	/**
	 * tboss处理人ID
	 */
	private String operatorId;
	/**
	 * 催单次数
	 */
	private Integer remindTimes;
	/**
	 * 最新一次催单时间
	 */
	private Date lastRemindTime;
	/**
	 * 取消预约原因
	 */
	private String cancelReason;
	/**
	 * 是否紧急
	 */
	private Integer isEmergency;
	/**
	 * 待救援状态
	 */
	private Integer breakStatus;
	/**
	 * 载货情况
	 */
	private String loadDescription;
	/**
	 * 外出救援次数
	 */
	private Integer timesRescueNumber;
	/**
	 * 外出救援用是否调件中（0：否；1：调件中；2：已调过件）
	 */
	private Integer rescueIsTransferring;
	/**
	 * 外出类型(默认1：正常救援；2：随叫随到)
	 */
	private Integer rescueType;
	/**
	 * 一次轨迹点是否完整	0：不完整，1：完整，2：初始态（不是0或1都为初始态）
	 */
	private Integer pointCompleteness;
	/**
	 * 二次轨迹点是否完整	0：不完整，1：完整，2：初始态（不是0或1都为初始态）
	 */
	private Integer pointCompletenessTwo;
	/**
	 * 预检预计完工时间
	 */
	private Date estimateTime;
	/**
	 * 预检预估费用
	 */
	private String estimateFee;
	/**
	 * 结算单提交状态（1：未提交，2：已提交，3已驳回）
	 */
	private Integer billSubStatus;
	/**
	 * 结算单驳回原因
	 */
	private String billReject;
	/**
	 * 车管驳回理由
	 */
	private String rejectionReason;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 更新时间
	 */
	private Date updateTime;
	/**
	 * aaa
	 */
	private Integer serviceType1;
	/**
	 * aaa
	 */
	private Integer serviceType;
	/**
	 * 是否诊断过，0没有 1 诊断过
	 */
	private Integer checkStatus;

}
