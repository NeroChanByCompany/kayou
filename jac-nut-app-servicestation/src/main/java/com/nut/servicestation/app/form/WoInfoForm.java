package com.nut.servicestation.app.form;


import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 工单详情接口（APP与TBOSS共用逻辑，与服务流程合并）
 */
@Data
@NutFormValidator
public class WoInfoForm extends BaseForm {

    /** 工单号 */
    @NotBlank(message = "工单号不能为空")
    private String woCode;

    private Integer noProcess;
}
