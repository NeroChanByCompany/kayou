package com.jac.app.job.dto;


import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 订单上报内容对象
 */
@Data
public class OrderReportDetailDto {

    @JSONField(ordinal = 11)
    private String vin;

    @JSONField(ordinal = 4)
    private String dealerMame;

    @JSONField(ordinal = 1)
    private String appRoNo;

    @JSONField(ordinal = 2)
    private String roStartTime;

    @JSONField(ordinal = 3)
    private String licenseNo;

    @JSONField(ordinal = 5)
    private String dealerCode;

    @JSONField(ordinal = 6)
    private String ownerName;

    @JSONField(ordinal = 7)
    private String ownerMobile;

    @JSONField(ordinal = 8)
    private String arriveStation;

    @JSONField(ordinal = 9)
    private String roType;

    @JSONField(ordinal = 10)
    private String mileage;

    @JSONField(ordinal = 12)
    private String delName;

    @JSONField(ordinal = 13)
    private String delMobile;

    @JSONField(ordinal = 14)
    private String faultDescription;


}
