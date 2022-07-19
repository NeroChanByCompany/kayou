package com.nut.servicestation.app.dto;

import lombok.Data;

import java.util.Date;

/**
 * @Description: 拒单申请审核列表
 */
@Data
public class RefuseApplysDto {
    /**
     * 工单状态
     */
    private String woStatus;
    /**
     * 底盘号
     */
    private String chassisNum;
    /**
     * 工单号
     */
    private String woCode;
    /**
     * 拒单类型编码
     */
    private String refuseApplyType;

    /**
     * 拒单类型名称
     */
    private String refuseApplyValue;
    /**
     * 拒单原因
     */
    private String refuseApplyReason;
    /**
     * 拒单时间
     */
    private String refuseApplyTime;
    /**
     * 工单类型
     */
    private String woType;
    /**
     * 预约方式
     */
    private String appointmentType;
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
    /**
     * 拒单时间
     */
    private Date applyRefuseDate;
}
