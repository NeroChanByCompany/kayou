package com.nut.driver.app.dto;

import lombok.Data;

/**
 * 车队车辆列表接口返回DTO
 */
@Data
public class FleetCarsDto {
    private String carId;
    private String carNumber;
    private String chassisNum;
    private String lon;
    private String lat;
    private String location;
    private Integer bound;

}
