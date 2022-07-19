package com.nut.driver.app.dto;

import lombok.Data;

import java.util.List;

@Data
public class CarReportOilChartDTO {

    /**
     *总油耗
     */
    private String totalOil = "0.0";
    /**
     *平均油耗
     */
    private String avgOil = "0.0";
    /**
     *怠速油耗
     */
    private String idlOil = "0.0";

    private List<OilDetailChartDTO> list;
}
