package com.nut.servicestation.app.dto;

import lombok.Data;

/**
 * @Description: 关闭申请审核列表
 */
@Data
public class CloseApplysDto {
    /**
     * 工单状态
     */
    private String woStatus;
    /**
     * 申请时状态
     */
    private Integer applyWoStatus;
    /**
     * 底盘号
     */
    private String chassisNum;
    /**
     * 工单号
     */
    private String woCode;
    /**
     * 关闭类型
     */
    private Integer closeType;
    /**
     * 关闭类型名称
     */
    private String closeValue;
    /**
     * 关闭原因
     */
    private String closeReason;
    /**
     * 申请时间
     */
    private String applyTime;
    /**
     * 工单类型 1：进出站，2：外出救援
     */
    private Integer woType;
    /**
     * 预约方式 1： 400客服，2：司机App
     */
    private Integer appointmentType;
    /**
     * 服务站
     */
    private String serviceStationName;
    /**
     * 服务站编码
     */
    private String serviceStationCode;
    /**
     * 服务站id
     */
    private String stationId;

}
