package com.nut.driver.app.dto;

import lombok.Data;

@Data
public class CarReportMileageCarListDTO {
    private String carNumber;

    private Double totalMileage;

    private Double avgSpeed;

    private String carId;

}
