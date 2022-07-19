package com.nut.driver.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * APP端登录接口
 */
@Data
@NutFormValidator
@ApiModel("线路实体")
public class LineForm extends BaseForm {

    @ApiModelProperty(name = "id" , notes = "线路ID" , dataType = "String")
    private String id;

    @NotNull(message = "起点城市名称不能为空")
    @NotBlank(message = "起点城市名称不能为空")
    @ApiModelProperty(name = "startCityName" , notes = "起点城市" , dataType = "String")
    private String startCityName;


    @NotNull(message = "终点城市名称不能为空")
    @NotBlank(message = "终点城市名称不能为空")
    @ApiModelProperty(name = "endCityName" , notes = "终点城市" , dataType = "String")
    private String endCityName;

    @NotNull(message = "车队ID不能为空")
    @NotBlank(message = "车队ID不能为空")
    @ApiModelProperty(name = "fleetId" , notes = "车队ID" , dataType = "String")
    private String fleetId;
}
