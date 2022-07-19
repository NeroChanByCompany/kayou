package com.nut.servicestation.app.dto;

import lombok.Data;

@Data
public class OrderCarDto {
    /**
     * 车辆ID
     */
    private String carId;
    /**
     * 车牌号
     */
    private String carNumber;
    /**
     * 已行驶里程（单位km）
     */
    private String mileage;
    /**
     * 是否有本站未完成工单  0：否；1：是
     */
    private String onlineWo;

    /** 24小时内是否创建过工单（true：是；false：否） */
    private Boolean orderStatus;
    /** 24小时内创建过工单的提示文案 */
    private String orderStatusMessage;

}
