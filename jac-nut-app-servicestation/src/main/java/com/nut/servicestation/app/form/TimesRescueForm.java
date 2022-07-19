package com.nut.servicestation.app.form;


import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import com.nut.common.utils.RegexpUtils;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

/**
 * @Description: 二次外出
 */
@Data
@NutFormValidator
public class TimesRescueForm extends BaseForm {
    /**
     * 工单号
     */
    @NotNull(message = "工单号不能为空")
    @NotBlank(message = "工单号不能为空")
    private String woCode;
    /**
     * 申请原因
     */
    @NotNull(message = "申请原因不能为空")
    @NotBlank(message = "申请原因不能为空")
    @Pattern(regexp = RegexpUtils.ZERO_NEGATIVE_INTEGERS_REGEXP, message = "申请原因格式不正确")
    private String reasonCode;
    /**
     * 是否调件（1：是；其他：否）
     */
    private String transferParts;

    /**
     * 返站时里程
     */
    @NotNull(message = "里程数不能为空")
    @Pattern(regexp = RegexpUtils.INTEGE_FLOAT, message = "里程数格式不正确")
    private String endMileage;

    /**
     * 返站时地址
     */
    @NotNull(message = "返站地址不可为空")
    private String endAddress;

    /**
     * 一次实际往返里程
     */
    @NotNull(message = "实际往返里程不能为空")
    @Pattern(regexp = RegexpUtils.INTEGE_FLOAT, message = "里程数格式不正确")
    private String appOutMileage;

    /**
     * 二次外出原因
     */
    private String twiceOutCause;

    /**
     * 信息列表
     */
    private List<RescueTransferPartsListForm> paramList;

}
