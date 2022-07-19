package com.nut.servicestation.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NutFormValidator
public class RepairHistoryForm extends BaseForm {

    /**
     * 底盘号
     */
    @NotNull(message = "底盘号不能为空")
    @NotBlank(message = "底盘号不能为空")
    private String chassisNum;

    /**
     * 操作记录ID
     * (不提供给前端)
     */
    private Integer operateCode;

}
