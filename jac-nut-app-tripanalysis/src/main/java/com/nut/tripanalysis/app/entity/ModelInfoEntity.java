package com.nut.tripanalysis.app.entity;

import lombok.Data;

import java.util.Date;

@Data
public class ModelInfoEntity {
    private Long modelId;

    private Long seriseId;

    private String modelName;

    private String gearBox;

    private String engine;

    private String rearAxleRatio;

    private String tireModel;

    private Float avgOilWear;

    private Date createTime;

    private String emissionCode;

    private String publishCode;

}