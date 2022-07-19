package com.nut.driver.app.dto;

import lombok.Data;

import java.util.List;

@Data
public class CarReportBadDrivingChartDTO {

    private String[] nameList;

    private List<BadDrivingChartPieDTO> list;

}
