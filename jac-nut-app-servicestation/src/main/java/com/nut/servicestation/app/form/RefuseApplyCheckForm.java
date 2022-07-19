package com.nut.servicestation.app.form;

import com.nut.common.base.BaseForm;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @Description: 拒单申请审核
 */
@Data
public class RefuseApplyCheckForm extends BaseForm {
    /**
     * 工单号
     */
    @NotNull(message = "工单号不能为空" )
    @NotBlank(message = "工单号不能为空" )
    private String woCode;

    /**
     * 审核结果	1：同意，2：驳回
     */
    @NotNull(message = "审核结果不能为空" )
    @NotBlank(message = "审核结果不能为空" )
    @Pattern(regexp = "^[1-2]$", message = "审核结果格式不正确" )
    private String checkResult;

    /**
     * 审核说明
     */
    @Length(message = "审核说明最多可输入100个字",max=100 )
    private String checkExplain;


    @Override
    public String toString() {
        return "RefuseApplyCheckCommand{" +
                "woCode='" + woCode + '\'' +
                ", checkResult='" + checkResult + '\'' +
                ", checkExplain='" + checkExplain + '\'' +
                '}';
    }

}
