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
 * 不良驾驶按车辆统计列表
 */
@Data
@NutFormValidator
@ApiModel("不良驾驶按车辆统计列表实体")
public class BadDrivingCountByCarForm extends BaseForm {

    /**
     * 统计开始时间
     */
    @NotNull(message = "统计开始日期不能为空")
    @NotBlank(message = "统计开始日期不能为空")
    @Pattern(regexp = RegexpUtils.DATE_YYYYMMDD_EIGHT_REGEXP, message = "统计开始日期格式不正确")
    @ApiModelProperty(name = "beginDate" , notes = "统计开始日期" , dataType = "String")
    private String beginDate;

    /**
     * 统计结束时间
     */
    @NotNull(message = "统计结束日期不能为空")
    @NotBlank(message = "统计结束日期不能为空")
    @Pattern(regexp = RegexpUtils.DATE_YYYYMMDD_EIGHT_REGEXP, message = "统计结束日期格式不正确")
    @ApiModelProperty(name = "endDate" , notes = "统计结束日期" , dataType = "String")
    private String endDate;

    @ApiModelProperty(name = "keyWord" , notes = "关键字" , dataType = "String")
    private String keyWord;

    @ApiModelProperty(name = "returnAll" , notes = "是否返回全部数据" , dataType = "String")
    private String returnAll;

}
