package com.nut.driver.app.dto;

import lombok.Data;

@Data
public class CarReportDrivingCarListDTO {

    private String carNumber;

    private Double totalTime;

    private Double drivingTime;

    private Double idlTime;

    private String carId;

}
