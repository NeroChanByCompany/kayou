package com.nut.driver.app.form;


import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import com.nut.common.utils.RegexpUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 统计-图表
 */
@Data
@NutFormValidator
@ApiModel("车辆报表-行驶里程实体")
public class StatisticsCarReportChartForm extends BaseForm {

    @NotNull(message = "统计开始日期不能为空")
    @NotBlank(message = "统计开始日期不能为空")
    @Pattern(regexp = RegexpUtils.DATE_YYYYMMDD_EIGHT_REGEXP, message = "统计开始日期格式不正确")
    @ApiModelProperty(name = "beginDate" , notes = "统计开始日期" , dataType = "String")
    private String beginDate;

    @NotNull(message = "统计结束日期不能为空")
    @NotBlank(message = "统计结束日期不能为空")
    @Pattern(regexp = RegexpUtils.DATE_YYYYMMDD_EIGHT_REGEXP, message = "统计结束日期格式不正确")
    @ApiModelProperty(name = "endDate" , notes = "统计结束日期" , dataType = "String")
    private String endDate;
//    @NotNull(message = "区分标志不能为空")
//    @NotBlank(message = "区分标志不能为空")
    @Pattern(regexp = RegexpUtils.NUMBER_ONLY_ZERO_ONE_REGEXP, message = "区分标志只能为0或1")
    @ApiModelProperty(name = "chartType" , notes = "统计结束日期" , dataType = "String")
    private String chartType;

    @ApiModelProperty(name = "carId" , notes = "车辆ID" , dataType = "String")
    private String carId;

}
