package com.nut.driver.app.form;


import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.driver.form
 * @Author: yzl
 * @CreateTime: 2021-06-18 11:07
 * @Version: 1.0
 */
@NutFormValidator
@Data
public class GetSetPictureInfoForm extends BaseForm {

    @NotNull(message = "actionCode不能为空")
    @NotBlank(message = "actionCode不能为空")
    @ApiModelProperty(name = "actionCode", notes = "动作编码", dataType = "String")
    private String actionCode;

    @ApiModelProperty(name = "type", notes = "app类别", dataType = "String")
    private String type;

}
