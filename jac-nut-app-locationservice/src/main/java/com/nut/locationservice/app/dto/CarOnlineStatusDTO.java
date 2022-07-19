package com.nut.locationservice.app.dto;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @Description: 车辆状态
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.app.locationService.dto
 * @Author: yzl
 * @CreateTime: 2021-06-17 11:28
 * @Version: 1.0
 */
@Data
@ToString
@Accessors(chain = true)
public class CarOnlineStatusDTO {

    /**
     * 终端id（通信号）
     */
    private Long tid;

    /**
     * 在线状态
     */
    private String status;

    /**
     * 经度
     */
    private String lon;

    /**
     * 纬度
     */
    private String lat;

    /**
     * 油耗
     */
    private Float standardFuelCon;

    /**
     * 里程
     */
    private Float standardMileage;

    /**
     * gps时间
     */
    private Long gpsDate;

    /**
     * 当日里程
     */
    private Float dayMil;

    /**
     * 当日油耗
     */
    private Float dayOil;

    /**
     * 车速
     */
    private Integer speed;

    /**
     * 方向
     */
    private Integer direction;

    /**
     * 总里程
     */
    private Double mileageEngine;

    /**
     * 车辆状态（0:车辆离线; 1:在线静止; 2:在线行驶; 3:车辆断连）
     */
    private Integer onLineStatus;

}
