package com.nut.driver.app.dto;

import lombok.Data;

import java.util.List;

@Data
public class CarReportMileageChartDTO {

    /**
     *总里程
     */
    private String totalMileage = "0.0";
    /**
     *车均里程
     */
    private String avgMileage = "0.0";
    /**
     *平均速度
     */
    private String avgSpeed = "0.0";

    private List<MileageDetailChartDTO> list;

}
