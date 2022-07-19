package com.nut.driver.app.form;


import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import com.nut.common.utils.RegexpUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;


@Data
@Accessors(chain = true)
@ApiModel("统计实体")
@NutFormValidator
@EqualsAndHashCode(callSuper = true)
public class StatisticsCarReportForm extends BaseForm implements Serializable {
    private static final long serialVersionUID = 3793324454371846968L;

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
}
