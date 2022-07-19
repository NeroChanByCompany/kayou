package com.nut.servicestation.app.dto;

import lombok.Data;

@Data
public class RepairHistoryDto {
    /**
     * 服务类型
     */
    private String serviceType;
    /**
     * 费用类型
     */
    private String chargeType;
    /**
     * 处理方式
     */
    private String dealType;
    /**
     * 付费方式
     */
    private String payType;
    /**
     * 故障描述
     */
    private String description;
    /**
     * 维修时间
     */
    private String repairTime;
    /**
     * 行驶里程
     */
    private String mileage;

}
