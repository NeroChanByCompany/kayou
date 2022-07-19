package com.nut.tripanalysis.app.pojo;

import lombok.Data;

/**
 * 平均油耗排行榜dto
 */
@Data
public class OilwearRankingPojo {

    private String carId;//车辆id
    private String carNumber;//车牌号
    private String carBrand;//品牌
    private String carModel;//车型
    private String modelId;// 车型Id
    private Double avgOilwear;//平均油耗
    private Integer ranking;//同车型排名
    private Integer percentage;//超过百分比0~100

    public OilwearRankingPojo(String carId, String carNumber, String carBrand, String carModel, String modelId) {
        this.carId = carId;
        this.carNumber = carNumber;
        this.carBrand = carBrand;
        this.carModel = carModel;
        this.modelId = modelId;
        this.avgOilwear = 0D;
        this.ranking = -1;
        this.percentage = -1;
    }

}
