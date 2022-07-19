package com.nut.driver.app.form;

import com.nut.common.base.BaseForm;
import com.nut.common.utils.RegexpUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @author liuBing
 * @Classname StatisticsCarLinReportForm
 * @Description TODO 线路车辆报表form
 * @Date 2021/7/26 17:23
 */
@Data
@Accessors(chain = true)
@ApiModel("线路统计报表")
public class StatisticsCarLinReportForm extends BaseForm {

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
    /**
     * 车队id
     */
    @ApiModelProperty(name = "fleetId" , notes = "车队id" , dataType = "String")
    private String fleetId;

    /**
     * 车辆id
     */
    @ApiModelProperty(name = "carids" , notes = "车辆id" , dataType = "String")
    private String carids;
}
