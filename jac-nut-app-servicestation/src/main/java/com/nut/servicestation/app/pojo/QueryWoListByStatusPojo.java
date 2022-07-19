package com.nut.servicestation.app.pojo;

import lombok.Data;

import java.util.Date;

/**
 * @Description: 工单列表
 */
@Data
public class QueryWoListByStatusPojo {
    private Integer woStatus;// 工单状态
    private String woStatusValue;// 工单状态内容
    private String chassisNum;// 底盘号
    private String carNumber;// 车牌号
    private String woCode;// 工单号
    private String carLocation;// 车辆位置
    private String carLon;// 车辆经度
    private String carLat;// 车辆纬度
    private String woCodeTime;// 工单时间
    private Integer woType;// 工单类型
    private Integer appointmentType;// 预约方式	1：司机App，2：400客服
    private String repairItem;// 维修项目
    private String maintainItem;// 保养项目
    private Date timeCreate;// 建单时间
    private Date timeAccept;// 接单时间
    private Date timeDepart;// 出发时间
    private Date timeReceive;// 接车时间
    private Date timeInspected;// 接车时间
    private Date timeClose;// 工单结束时间（维修完成或客服同意关闭）
    private Integer rescueType;// 外出类型
    private Integer protocolMark;//协议工单标识
    private Integer billSubStatus;// 结算单提交状态
    private String assignTo;//工单指派人员
    private Integer outOfInsurance;//关联的维修项目是否有保外服务类型 1是 0否
}
