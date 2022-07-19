package com.nut.tripanalysis.app.dto;

import lombok.Data;

/**
 * 平均油耗排行榜dto
 */
@Data
public class AvgOilwearRankingDto {

    private String carId;
    private String carNumber;//车牌号
    private String carBrand;//品牌
    private String carModel;//车型
    private String modelId;// 车型Id
    private Double avgOilwear;//平均油耗
    private Integer ranking;//同车型排名
    private Integer percentage;//超过百分比0~100
    private String myCar;
    private String vin;
    private Double mileage;//当天的行程里程

    public AvgOilwearRankingDto(String carId, String carNumber, String carBrand, String carModel, String modelId, Double avgOilwear, Integer ranking, Integer percentage) {
        this.carId = carId;
        this.carNumber = carNumber;
        this.carBrand = carBrand;
        this.carModel = carModel;
        this.modelId = modelId;
        this.avgOilwear = avgOilwear;
        this.ranking = ranking;
        this.percentage = percentage;
    }

}
