package com.nut.driver.app.dto;

import lombok.Data;

@Data
public class FleetReportOilDto {
    private String fleetName;

    private Double totalOil;

    private String avgOil;

    private String idlOil;

    private String creator;

    private String carNumber;

}
