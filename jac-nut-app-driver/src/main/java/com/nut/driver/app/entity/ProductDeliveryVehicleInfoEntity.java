package com.nut.driver.app.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author liuBing
 * @Classname ProductDeliveryVehicleInfoEntity
 * @Description TODO
 * @Date 2021/6/25 13:50
 */
@Data
public class ProductDeliveryVehicleInfoEntity {


    private Long id;

    private Integer deliveryStatus;

    private String invoiceNo;

    private Long saleId;

    private String dealerName;

    private String dealerCode;

    private String dealerLandline;

    private String dealerPhone;

    private String dealerTrainer;

    private String carId;

    private String carType;

    private String vin;

    private Date manufacturingDate;

    private Date salesDate;

    private Date deliveryDate;

    private String mileage;

    private String carNumber;

    private String customerCompany;

    private String customerName;

    private String customerLandline;

    private String customerPhone;

    private String identityCard;

    private String remarks;

    private Date createTime;

    private Date updateTime;

}
