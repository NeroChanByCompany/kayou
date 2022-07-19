package com.nut.driver.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.form
 * @Author: yzl
 * @CreateTime: 2021-09-27 09:13
 * @Version: 1.0
 */
@Data
@NutFormValidator
public class GuoQingForm extends BaseForm {

    @NotNull(message = "活动类型不能为空")
    @NotBlank(message = "活动类型不能为空")
    // 0：浏览商城
    private Integer guoQingType;
}
