package com.nut.tripanalysis.app.form;


import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import com.nut.common.utils.RegexpUtils;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 月行程数据查询接口
 */
@Data
@NutFormValidator
public class QueryTripByMonthForm extends BaseForm {

    @Pattern(message = "月份格式不规范", regexp = RegexpUtils.DATE_YYYYMM_REGEXP)
    @NotBlank(message = "月份不能为空")
    @NotNull(message = "月份不能为空")
    private String month;

    @NotBlank(message = "车辆ID不能为空")
    @NotNull(message = "车辆ID月份不能为空")
    private String carId;

}
