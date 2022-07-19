package com.nut.driver.app.dto;

import lombok.Data;

import java.util.List;

@Data
public class CarReportIdlDrivingDTO {

    private String totalTime = "0.0";

    private String idlTime = "0.0";

    private String idlTimePro = "0.0";

    List<CarReportIdlDrivingRankingDTO> list;

}
