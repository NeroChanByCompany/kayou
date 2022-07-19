package com.nut.driver.app.dto;

import lombok.Data;

import java.util.List;

@Data
public class FleetReportBaseDto {

    private int fleetTotal;

    private List<FleetReportMileageDto> mileageList;

    private List<FleetReportOilDto> oilList;

    private List<FleetReportDrivingDto> drivingList;

    private List<FleetReportBadDrivingDto> badDrivingList;

}
