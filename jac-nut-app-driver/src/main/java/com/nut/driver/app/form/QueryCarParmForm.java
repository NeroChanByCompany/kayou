package com.nut.driver.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Description: 车辆参数
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.form
 * @Author: yzl
 * @CreateTime: 2021-06-29 17:45
 * @Version: 1.0
 */
@Data
@NutFormValidator
public class QueryCarParmForm extends BaseForm {
    @NotNull(message = "车辆id为空")
    @NotBlank(message = "车辆id为空白")
    public String carId;//车辆id
}
