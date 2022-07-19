package com.nut.driver.app.dto;

import lombok.Data;

@Data
public class CarReportIdlOilRankingDTO {

    private String carNumber;

    private Double idlOil;

    private String proportion ;
    /**
     * 平均油耗
     */
    private Double avgOil;

}
