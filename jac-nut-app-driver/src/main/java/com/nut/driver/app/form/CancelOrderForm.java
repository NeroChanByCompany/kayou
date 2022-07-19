package com.nut.driver.app.form;


import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import com.nut.common.utils.RegexpUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @Description: 取消预约
 */
@Data
@NutFormValidator
public class CancelOrderForm extends BaseForm {
    /**
     * 工单号
     */
    @ApiModelProperty(name = "woCode",notes = "工单号",dataType = "String")
    @NotBlank(message = "工单号不能为空")
    private String woCode;
    /**
     * 取消原因
     */
    @ApiModelProperty(name = "cancelReason",notes = "取消原因",dataType = "String")
    @NotBlank(message = "取消原因不能为空")
    @Pattern(regexp = RegexpUtils.POSITIVE_INTEGER_REGEXP, message = "取消原因格式不正确")
    private String cancelReason;
}
