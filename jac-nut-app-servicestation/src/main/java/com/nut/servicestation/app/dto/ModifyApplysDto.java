package com.nut.servicestation.app.dto;

import lombok.Data;

import java.util.Date;

/**
 * @Description: 修改申请审核列表
 */
@Data
public class ModifyApplysDto {
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
     * 申请原因
     */
    private String applyReason;
    /**
     * 申请时间
     */
    private String applyTime;
    /**
     * 工单类型
     */
    private Integer woType;
    /**
     * 预约方式
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

    /**
     * 申请时间
     */
    private Date applyModifyDate;
}
