package com.nut.driver.app.dto;

import lombok.Data;

/**
 * @author liuBing
 * @Classname QueryDriverMaintenanceDTO
 * @Description TODO
 * @Date 2021/6/24 14:33
 */
@Data
public class QueryDriverMaintenanceDTO {
    /**
     * 保养记录id
     */
    private String maintainId;

    /**
     * 保养名称
     */
    private String maintainName;

    /**
     * 提醒方式 1：里程；2：时间
     */
    private String maintainType;

    /**
     * 提醒内容
     */
    private String maintainDescribe;

    /**
     * 保养项目总数
     */
    private String maintainItemCount;

    /**
     * 车辆id
     */
    private String carId;

    /**
     * 车牌号
     */
    private String carNumber;

    /**
     * 保养状态  	0：需要保养车辆；1：未到保养车辆；2：已完成保养车辆
     */
    private String maintainStatus;

    /**
     * 底盘号（vin后8位）
     */
    private String vin;
}
