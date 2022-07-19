package com.nut.servicestation.app.form;


import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 确认接单
 *
 * @author Sunyu
 * @date 2018/5/10
 */
@Data
@NutFormValidator
public class AcceptOrderForm extends BaseForm {

    /**
     * 工单号
     */
    @NotNull(message = "工单号不能为空")
    @NotBlank(message = "工单号不能为空")
    private String woCode;

    /**
     * 指派人员ID
     */
    private String assignTo;


}