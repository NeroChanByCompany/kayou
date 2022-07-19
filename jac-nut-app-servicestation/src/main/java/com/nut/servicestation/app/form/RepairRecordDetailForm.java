package com.nut.servicestation.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NutFormValidator
public class RepairRecordDetailForm extends BaseForm {
    /**
     * 工单号
     */
    @NotNull(message = "工单号不能为空")
    @NotBlank(message = "工单号不能为空")
    private String woCode;

    @NotNull(message = "维修ID不能为空")
    @NotBlank(message = "维修ID不能为空")
    private String operateId;

}
