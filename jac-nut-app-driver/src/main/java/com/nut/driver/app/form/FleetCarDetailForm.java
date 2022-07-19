package com.nut.driver.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 车队车辆详情接口
 */
@Data
@NutFormValidator
@ApiModel("app车队车辆详情")
public class FleetCarDetailForm extends BaseForm {
    /**
     * 车队ID
     */
    @NotNull(message = "车队ID不能为空")
    @ApiModelProperty(name = "teamId" , notes = "车队ID" , dataType = "Long")
    private Long teamId;
    /**
     * 车辆ID
     */
    @NotBlank(message = "车辆ID不能为空")
    @ApiModelProperty(name = "carId" , notes = "车辆ID" , dataType = "String")
    private String carId;
    /**
     * 搜索关键字
     */
    @ApiModelProperty(name = "keyword" , notes = "关键字" , dataType = "String")
    private String keyword;


}
