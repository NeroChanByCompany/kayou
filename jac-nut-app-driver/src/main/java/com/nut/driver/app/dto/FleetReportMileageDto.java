package com.nut.driver.app.dto;

import lombok.Data;

@Data
public class FleetReportMileageDto {

    private String fleetName;

    private Double totalMileage;

    private String avgMileage;

    private String avgSpeed;

    private String creator;

    private String carNumber;
}
