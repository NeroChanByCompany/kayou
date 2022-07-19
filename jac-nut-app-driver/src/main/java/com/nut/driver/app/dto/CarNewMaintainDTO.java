package com.nut.driver.app.dto;

import lombok.Data;

/**
 * @author liuBing
 * @Classname CarNewMaintainDTO
 * @Description TODO
 * @Date 2021/6/24 10:32
 */
@Data
public class CarNewMaintainDTO {

    /**
     * 车辆id
     */
    private String carId;

    /**
     * 车牌号
     */
    private String carNumber;

    /**
     * 底盘号
     */
    private String chassisNum;

    /**
     * 已行驶总里程
     */
    private String totalmileage;

    /**
     * 车辆协议类型（0：非协议车，1：协议车）
     */
    private String protocolType;
    /**
     * 下次保养项目
     */
    private Integer nextMaintenanceCount;
}
