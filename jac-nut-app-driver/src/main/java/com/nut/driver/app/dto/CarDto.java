package com.nut.driver.app.dto;

import lombok.Data;

import java.math.BigInteger;

/**
 * @Description: 车辆信息dto
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.dto
 * @Author: yzl
 * @CreateTime: 2021-06-27 13:45
 * @Version: 1.0
 */
@Data
public class CarDto {
    private String carId;
    private String carNumber;
    private String mainDriver;
    private String subDriver;
    private String vin;
    private String autoTerminal;
    private Integer online;
    private String teamId;
    private String teamName;
    private String pTeamId; // 父车队ID
    private String carType; // 车型
    private BigInteger status; //状态
    private Integer isCurrent; // 是否是司机版选择的当前车辆
    private String carSeriesId; // 车系ID
    private String carSeriesName; // 车系名
    private Long routeId; // 线路ID
    private String routeName; // 线路名

    private String carModelId;
    private String carModelName;
    private String engine;

    private String emission;// 排放
    private String driverTypeName;// 驱动形式
    private String carTypeName;// 车辆类别
}
