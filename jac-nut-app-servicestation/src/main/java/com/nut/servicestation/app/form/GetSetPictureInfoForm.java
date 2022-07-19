package com.nut.servicestation.app.form;


import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NutFormValidator
public class GetSetPictureInfoForm extends BaseForm {

    @NotNull(message = "actionCode不能为空")
    @NotBlank(message = "actionCode不能为空")
    private String actionCode;

    private String type;

}
