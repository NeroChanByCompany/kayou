package com.nut.driver.app.dto;

import lombok.Data;

@Data
public class CarReportOilCarListDTO {
    private String carNumber;

    private Double totalOil;

    private Double idlOil;

    private Double avgOil;

    private String carId;


    /**
     * 与第一名的差
     */
    private String difference;

}
