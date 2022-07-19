package com.nut.truckingteam.app.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 车辆信息DTO
 */
@Data
public class CarOilWearDto implements Serializable {
    private String carId;
    private String carNumber;
    private String vin;
    private String autoTerminal;
    private String seriesId;
    private String seriseName;
    private String modelId;
    private String modelName;
    private String engine;

}
