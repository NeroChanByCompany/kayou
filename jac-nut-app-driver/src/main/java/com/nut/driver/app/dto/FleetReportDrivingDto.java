package com.nut.driver.app.dto;

import lombok.Data;

@Data
public class FleetReportDrivingDto {

    private String fleetName;

    private Double totalTime;

    private String drivingTime;

    private String idlTime;

    private String creator;

    private String carNumber;

}
