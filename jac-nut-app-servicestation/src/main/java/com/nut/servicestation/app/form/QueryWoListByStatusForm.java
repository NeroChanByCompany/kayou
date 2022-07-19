package com.nut.servicestation.app.form;

import com.nut.common.annotation.EmojiForbid;
import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import com.nut.common.utils.RegexpUtils;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @Description: 工单列表
 */
@Data
@NutFormValidator
public class QueryWoListByStatusForm extends BaseForm {
    @NotNull(message = "工单状态不能为空")
    @NotBlank(message = "工单状态不能为空")
    @Pattern(regexp = RegexpUtils.POSITIVE_INTEGER_REGEXP, message = "工单状态格式不正确")
    private String woStatus;

    /**
     * 工单类型 0：进出站和外出救援，1：进出站，2：外出救援
     */
    @NotNull(message = "工单类型不能为空")
    @NotBlank(message = "工单类型不能为空")
    @Pattern(regexp = RegexpUtils.ZERO__TO_TWO_NUMBER, message = "工单类型格式不正确")
    private String woType;

    /**
     * 底盘号/车牌号
     */
    @EmojiForbid(message = "请您输入正确的底盘号/车牌号！")
    @Length(max = 200, message = "请您输入正确的底盘号/车牌号！")
    private String keyValue;

    /**
     * 工单结束时间-开始
     */
    private String timeCloseStart;

    /**
     * 工单结束时间-结束
     */
    private String timeCloseEnd;

}

