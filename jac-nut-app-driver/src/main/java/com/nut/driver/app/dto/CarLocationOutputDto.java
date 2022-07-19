package com.nut.driver.app.dto;

import lombok.Data;

/**
 * @Description: 获取车辆位置的出参
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.driver.dto
 * @Author: yzl
 * @CreateTime: 2021-06-22 09:45
 * @Version: 1.0
 */
@Data
public class CarLocationOutputDto {
    private String vin;//车辆vin码

    private Double lon;//经度坐标

    private Double lat;//纬度坐标

    private Double mileage;//里程

    private Long upTime;//数据上传时间

    private Long cumulativeRunningTime;//发动机累计运行时间

    private String status;//状态

    private Double aveMileage;//里程

    private Double standardFuelCon;//标准油耗

    /**
     * 速度
     */
    private Double speed;
    /**
     * 发动机转速
     */
    private Double rotation;
    /**
     * 燃油液位
     */
    private Double oilValue;
    /**
     * 发动机冷却水温度
     */
    private Double coolingWaterTem;
    /**
     * 机油压力
     */
    private Double oilPressure;
    /**
     * 大气温度
     */
    private Double atmosphericTem;

    /**
     * 总里程
     */
    private Double mileageEngine;
    /**
     * 尿素箱液位
     */
    private Double ureaLevel;

    /**
     * 剩余油量百分比
     **/
    private Double oilValuePercent;
    private Double avgOil;

}
