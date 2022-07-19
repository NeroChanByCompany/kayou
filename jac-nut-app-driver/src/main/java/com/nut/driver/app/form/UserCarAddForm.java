package com.nut.driver.app.form;


import com.nut.common.annotation.Length;
import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import com.nut.common.utils.RegexpUtils;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@NutFormValidator
@ApiModel("APP车辆品牌Entity")
public class UserCarAddForm extends BaseForm {

    /**
     * 品牌
     */
    @NotBlank(message = "品牌不能为空")
    @Length(maxLength = 10)
    @ApiModelProperty(name = "brand",notes = "品牌",dataType = "String")
    private String brand;
    /**
     * 车牌号
     */
    @Pattern(regexp = RegexpUtils.CAR_NUMBER, message = "车牌号格式不正确")
    @NotNull
    @ApiModelProperty(name = "carNumber",notes = "车牌",dataType = "String")
    private String carNumber;
    /**
     * 车辆vin
     */
    @Pattern(regexp = RegexpUtils.SEVENTEEN_VIN, message = "vin格式不正确")
    @NotNull
    @ApiModelProperty(name = "vin",notes = "底盘号",dataType = "String")
    private String vin;
    /**
     * 发动机号
     */
    @ApiModelProperty(name = "engineNum",notes = "发动机号",dataType = "String")
    private String engineNum;

    @ApiModelProperty(name = "carId" , notes = "车辆ID" , dataType = "String")
    private String carId;

    @ApiModelProperty(name = "license" , notes = "车牌号" , dataType = "String")
    private String license;

    @ApiModelProperty(name = "series" , notes = "车系" , dataType = "String")
    private String series;

    @ApiModelProperty(name = "color" , notes = "车身颜色" , dataType = "String")
    private String color;


}
