package com.nut.servicestation.app.form;


import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 开始检查接口
 */
@Data
@NutFormValidator
public class StartInspectForm extends BaseForm {

    /** 工单号 */
    @NotBlank(message = "工单号不能为空")
    private String woCode;

}
