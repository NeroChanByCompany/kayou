package com.nut.driver.app.form;;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.form;
 * @Author: yzl
 * @CreateTime: 2021-06-23 19:10
 * @Version: 1.0
 */
@Data
@NutFormValidator
public class DbgCallUploadTspInfForm extends BaseForm {
    @NotBlank(message = "工单号不能为空")
    private String woCode;

    private String crmType;
    private String queryOnly = "1";
    private String suffix;
}
