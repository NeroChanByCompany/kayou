package com.nut.servicestation.app.dto;

import lombok.Data;

/**
 * @Description: 工单列表
 */
@Data
public class QueryWoListByStatusDto {
    private String woStatus;// 工单状态
    private String woStatusValue;// 工单状态内容
    private String chassisNum;// 底盘号
    private String carNumber;// 车牌号
    private String woCode;// 工单号
    private String carLocation;// 车辆位置
    private String carLon;// 车辆经度
    private String carLat;// 车辆纬度
    private String workTime;// 工单时间
    private Integer woType;// 工单类型
    private Integer rescueType;// 外出类型
    private Integer appointmentType;// 预约方式	1：司机App，2：400客服
    private Integer appointmentItem;// 预约项目	1：维修项目，2：保养项目，3：维修和保养项目
    private Integer protocolMark;//协议工单标识
    private String assignTo;//指派人员
    private Integer outOfInsurance;//关联的维修项目是否有保外服务类型 1是 0否

}
