package com.nut.driver.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.form
 * @Author: yzl
 * @CreateTime: 2021-06-29 10:04
 * @Version: 1.0
 */
@Data
@NutFormValidator
public class UserCarInfoForm extends BaseForm {

    /**
     * 车辆vin
     */
    @NotBlank
    @ApiModelProperty(value = "chassisNum", notes = "车辆vin", dataType = "String")
    private String chassisNum;
}
