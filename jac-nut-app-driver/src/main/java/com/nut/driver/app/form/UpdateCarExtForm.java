package com.nut.driver.app.form;

import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;

/**
 * 车辆扩展信息更新
 *
 */
@Data
@ApiModel(value = "车辆拓展信息更新Form")
public class UpdateCarExtForm extends BaseForm {

    /**
     * 车辆表 id
     */
    @ApiModelProperty(name = "carId",notes = "车辆Id",dataType = "Long")
    private Long carId;
    /**
     * 车辆vin
     */
    @ApiModelProperty(name = "vin",notes = "车辆vin",dataType = "String")
    private String vin;
    /**
     * 车牌号
     */
    @ApiModelProperty(name = "license",notes = "车牌号",dataType = "String")
    private String license;
    /**
     * 车辆身份
     */
    @ApiModelProperty(name = "identity",notes = "车辆身份",dataType = "String")
    private String identity;
    /**
     * 品牌
     */
    @ApiModelProperty(name = "brand",notes = "品牌",dataType = "String")
    private String brand;
    /**
     * 车系
     */
    @ApiModelProperty(name = "series",notes = "车系",dataType = "String")
    private String series;
    /**
     * 发动机号
     */
    @ApiModelProperty(name = "engineNum",notes = "发动机号",dataType = "String")
    private String engineNum;
    /**
     * 车辆颜色
     */
    @ApiModelProperty(name = "color",notes = "车辆颜色",dataType = "String")
    private String color;
    /**
     * 车辆用途
     */
    @ApiModelProperty(name = "vehicleUse",notes = "车辆用途",dataType = "String")
    private Integer vehicleUse;
    /**
     * 行业
     */
    @ApiModelProperty(name = "industry",notes = "行业",dataType = "String")
    private String industry;
    /**
     * 货物类型
     */
    @ApiModelProperty(name = "typeOfGoods",notes = "货物类型",dataType = "String")
    private String typeOfGoods;
    /**
     * 货车类型
     */
    @ApiModelProperty(name = "typeOfVan",notes = "货车类型",dataType = "String")
    private String typeOfVan;
    /**
     * 核定载重（吨）
     */
    @ApiModelProperty(name = "ratedLoad",notes = "核定载重",dataType = "String")
    private String ratedLoad;
    /**
     * 货车重量(吨）
     */
    @ApiModelProperty(name = "weight",notes = "货车重量",dataType = "String")
    private String weight;
    /**
     * 货车长度（米）
     */
    @ApiModelProperty(name = "length",notes = "货车长度",dataType = "String")
    private String length;
    /**
     * 货车宽度(米）
     */
    @ApiModelProperty(name = "width",notes = "货车宽度",dataType = "String")
    private String width;
    /**
     * 保险到期时间
     */
    @ApiModelProperty(name = "insuranceDate",notes = "保险到期时间",dataType = "String")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date insuranceDate;
    /**
     * 保险金额
     */
    @ApiModelProperty(name = "insuredAmount",notes = "保险金额",dataType = "String")
    private String insuredAmount;
    /**
     * 保险公司
     */
    @ApiModelProperty(name = "insuranceCompany",notes = "保险公司",dataType = "String")
    private String insuranceCompany;
    /**
     * 换车周期(年）
     */
    @ApiModelProperty(name = "transferCycle",notes = "换车周期",dataType = "String")
    private String transferCycle;

    /**
     * 更新时间
     */
    @ApiModelProperty(name = "updateTime",notes = "更新时间",dataType = "String")
    private Date updateTime;
}
