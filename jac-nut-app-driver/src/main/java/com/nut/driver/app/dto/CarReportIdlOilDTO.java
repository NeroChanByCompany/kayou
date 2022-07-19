package com.nut.driver.app.dto;

import lombok.Data;

import java.util.List;

@Data
public class CarReportIdlOilDTO {

    private String totalOil = "0.0";

    private String totalIdlOil = "0.0";

    private String idlOilPro = "0.0";

    private List<CarReportIdlOilRankingDTO> list;
}
