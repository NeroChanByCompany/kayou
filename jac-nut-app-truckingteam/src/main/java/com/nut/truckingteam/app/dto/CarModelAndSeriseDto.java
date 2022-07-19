package com.nut.truckingteam.app.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 车辆信息DTO
 */
@Data
public class CarModelAndSeriseDto implements Serializable {
    private String seriseId; // 车系ID
    private String seriseName; // 车系名
    private String modelId;
    private String modelName;
    private String engine;

    private String emission;// 排放
    private String driverTypeName;// 驱动形式
    private String carTypeName;// 车辆类别

}