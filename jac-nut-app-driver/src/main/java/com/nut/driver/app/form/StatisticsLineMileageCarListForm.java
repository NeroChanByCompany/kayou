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
 * 统计-里程车辆列表
 */
@Data
@NutFormValidator
@ApiModel("车辆报表统计实体")
public class StatisticsLineMileageCarListForm extends BaseForm {
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

    @ApiModelProperty(name = "keyWord" , notes = "关键字" , dataType = "String")
    private String keyWord;

    @ApiModelProperty(name = "returnAll" , notes = "是否返回全部数据" , dataType = "String")
    private String returnAll;


    @ApiModelProperty(name = "carIds", notes = "车辆id", dataType = "String")
    private String carIds;

    @ApiModelProperty(name = "fleetId", notes = "车辆id", dataType = "String")
    private String fleetId;

}
