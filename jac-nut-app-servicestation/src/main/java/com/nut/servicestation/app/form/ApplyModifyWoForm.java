package com.nut.servicestation.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Description: 申请修改工单
 */
@Data
@NutFormValidator
public class ApplyModifyWoForm extends BaseForm {

    /**
     * 工单号
     */
    @NotNull(message = "工单号不能为空！")
    @NotBlank(message = "工单号不能为空！")
    private String woCode;

    /**
     * 修改原因
     */
    @NotNull(message = "请输入修改原因！")
    @NotBlank(message = "请输入修改原因！")
    @Length(max = 300, message = "修改原因最多可输入300个字或字符！")
    private String modifyReason;

}
