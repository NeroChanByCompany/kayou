package com.nut.driver.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import com.nut.common.utils.RegexpUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Pattern;

/**
 * @Description: 海量打点接口Form
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.form
 * @Author: yzl
 * @CreateTime: 2021-06-29 16:54
 * @Version: 1.0
 */
@NutFormValidator
@Data
@ApiModel("海量打点实体")
public class QueryCarPolymerizeForm extends BaseForm {
    private Long teamId;

    @Pattern(message = "左下角经度坐标必须为浮点数", regexp = RegexpUtils.POSITIVE_ONE_TO_SIX_RATIONAL_NUMBERS_REGEXP)
    @ApiModelProperty(name = "leftLongitude" , notes = "左下角经度坐标" , dataType = "String")
    private String leftLongitude;

    @Pattern(message = "左下角纬度坐标必须为浮点数", regexp = RegexpUtils.POSITIVE_ONE_TO_SIX_RATIONAL_NUMBERS_REGEXP)
    @ApiModelProperty(name = "leftLatitude" , notes = "左下角纬度坐标" , dataType = "String")
    private String leftLatitude;

    @Pattern(message = "右上角经度坐标必须为浮点数", regexp = RegexpUtils.POSITIVE_ONE_TO_SIX_RATIONAL_NUMBERS_REGEXP)
    @ApiModelProperty(name = "rightLongitude" , notes = "右上角经度坐标" , dataType = "String")
    private String  rightLongitude;

    @Pattern(message = "右上角纬度坐标必须为浮点数", regexp = RegexpUtils.POSITIVE_ONE_TO_SIX_RATIONAL_NUMBERS_REGEXP)
    @ApiModelProperty(name = "rightLatitude" , notes = "右上角纬度坐标" , dataType = "String")
    private String  rightLatitude;

    @Pattern(message = "地图缩放级别必须为非负整数", regexp = RegexpUtils.NATURAL_NUMBER)
    @ApiModelProperty(name = "zoom" , notes = "地图缩放级别" , dataType = "String")
    private String  zoom;
}
