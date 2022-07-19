package com.nut.driver.app.dto;

import lombok.Data;

import java.util.List;

@Data
public class CarReportDrivingChartDTO {

    /**
     *行驶时长
     */
    private String totalTime = "0.0";
    /**
     *驾驶时长
     */
    private String drivingTime = "0.0";
    /**
     *怠速时长
     */
    private String idlTime = "0.0";

    List<DrivingDetailChartDTO> list;

}
