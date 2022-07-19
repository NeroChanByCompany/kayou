package com.nut.driver.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Description: 冷链车用提交表单
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.servicestation.app.form
 * @Author: yzl
 * @CreateTime: 2021-09-01 08:53
 * @Version: 1.0
 */
@Data
@NutFormValidator
public class ColdCarForm extends BaseForm {

    @NotBlank
    @NotNull
    private String carVin;

}
