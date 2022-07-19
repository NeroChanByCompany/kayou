package com.nut.tripanalysis.app.form;



import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import com.nut.common.utils.RegexpUtils;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 查询指定日期的行程列表接口
 */
@Data
@NutFormValidator
public class QueryTripByDayForm extends BaseForm {

    @Pattern(message = "日期格式不规范", regexp = RegexpUtils.DATE_BARS_REGEXP_SIMPLE)
    @NotBlank(message = "日期不能为空")
    @NotNull(message = "日期不能为空")
    private String day;

    @NotBlank(message = "车辆ID不能为空")
    @NotNull(message = "车辆ID不能为空")
    private String carId;

}
