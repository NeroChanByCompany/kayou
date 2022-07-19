package com.nut.servicestation.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import com.nut.common.utils.RegexpUtils;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 拒绝接单
 */
@Data
@NutFormValidator
public class RefuseOrderForm extends BaseForm {

    /**
     * 工单号
     */
    @NotNull(message = "工单号不能为空")
    @NotBlank(message = "工单号不能为空")
    private String woCode;

    /**
     * 拒单类型 1：缺配件、2：无技术能力、3：无法安排人/车、4：保内自行处理、5：保外另行处理。
     */
    @NotNull(message = "拒单类型不能为空")
    @Pattern(regexp = RegexpUtils.ONE__TO_FIVE_NATURAL_NUMBER, message = "拒单类型格式不正确")
    private String refuseType;

    /**
     * 拒单理由
     */
    @NotNull(message = "拒单理由不能为空")
    @Length(min = 5, max = 300, message = "拒单理由最少5个字最多300个字")
    private String refuseReason;

}