package com.nut.servicestation.app.form;


import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import com.nut.common.utils.RegexpUtils;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @Description: 服务站附近车辆查询
 */
@Data
@NutFormValidator
public class NearbyCarsForm extends BaseForm {

    /**
     * 查询范围
     */
    @NotNull(message = "查询范围不能为空")
    @NotBlank(message = "查询范围不能为空")
    @Pattern(regexp = RegexpUtils.STATION_RANGE, message = "查询范围数值不正确")
    private String range;

}
