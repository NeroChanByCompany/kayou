package com.nut.servicestation.app.dto;

import lombok.Data;

import java.util.Date;

@Data
public class WoInfoDto {

    /** 工单号 */
    private String woCode;
    /** 车牌号 */
    private String carNumber;
    /** 预约服务站名称 */
    private String appoStationName;
    /** 预约到站时间 */
    private String appoArriveTime;
    /** 预约方式 */
    private Integer appoType;
    /** 工单类型 */
    private Integer woType;
    /** 工单状态 */
    private Integer woStatus;
    /** 建单时间 */
    private String timeCreate;
    /** 开始检查时间 */
    private String timeInspectBegin;
    /** 用户姓名 */
    private String appoUserName;
    /** 用户电话 */
    private String appoUserPhone;
    /** 车辆经度 */
    private String carLon;
    /** 车辆纬度 */
    private String carLat;
    /** 车辆位置 */
    private String carLocation;
    /** 车与服务站距离 */
    private Double carDistance;
    /** 维修项目 */
    private String repairItem;
    /** 保养项目 */
    private String maintainItem;
    /** 创建人ID */
    private String operatorId;
    /** 创建人名称 */
    private String operatorName;
    /** 用户反馈 */
    private String userComment;
    /** 购车日期 */
    private String carSaleDate;
    /** 行驶里程 */
    private Double mileage;
    /** 底盘号 */
    private String chassisNum;
    /** 公告车型 */
    private String gonggao;
    /** 发动机类型 */
    private String engineType;
    /** 发动机型号 */
    private String engineModel;
    /** 故障码 */
    private String faultCode;
    /** 整车平台 */
    private String seriseName;
    /** 服务站ID */
    private String stationId;
    /** 服务站编码 */
    private String stationCode;
    /** 服务站经度 */
    private String stationLon;
    /** 服务站纬度 */
    private String stationLat;
    /** 服务站省市编码 */
    private String areaCode;
    /** 送修人姓名 */
    private String shipperName;
    /** 送修人电话 */
    private String shipperPhone;
    /** 注册手机号 */
    private String registeredPhone;
    /** 是否紧急 */
    private String isEmergency;
    /** 待救援状态 */
    private String breakStatus;
    /** 载货情况 */
    private String loadDescription;
    /** 是否可以申请拒单 0：否；1：是 */
    private String canRefuse;
    /** 是否可以申请修改 0：否；1：是 */
    private String canModify;
    /** 是否可以申请关闭 0：否；1：是 */
    private String canClose;

    /** 被指派人ID */
    private String assignTo;

    /** 非维修车辆标记-标记账号 */
    private String nonServiceMarkAct;
    /** 非维修车辆标记-标记时间 */
    private String nonServiceMarkTime;
    /** 外出人员姓名 */
    private String rescueStaffName;
    /** 外出人员电话 */
    private String rescueStaffPhone;
    /** 外出人数 */
    private Integer rescueStaffNum;
    /** 救援车辆GPS设备号 */
    private String rescueCarDevice;
    /** 外出救援用是否调件中 0：否；1：是 */
    private String rescueIsTransferring;
    /** 外出类型 */
    private Integer rescueType;
    /** 是否展示保养计划 0：否；1：是*/
    private Integer isShowMaintain;
    /** 协议工单标识 */
    private Integer protocolMark;
    /** 结算单提交状态 */
    private Integer billSubStatus;
    /** 结算单驳回原因 */
    private String billReject;
    /** 预检预计完工时间 */
    private Date estimateTime;
    /** 预检预估费用 */
    private String estimateFee;

    /**
     * 车辆排放类型（国五/国六）
     */
    private String gasOutValue;

    /**
     * 国六车辆提示文本
     */
    private String workOderText;
}