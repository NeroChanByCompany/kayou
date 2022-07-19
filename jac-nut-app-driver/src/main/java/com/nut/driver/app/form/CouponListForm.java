package com.nut.driver.app.form;


import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import com.nut.common.utils.RegexpUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @Description: 优惠券查询表单
 */
@Data
@NutFormValidator
public class CouponListForm extends BaseForm {
    /**
     * 使用标识，1待使用 2已使用 3已过期
     */
    @NotNull(message = "使用标识不能为空")
    @NotBlank(message = "使用标识不能为空")
    @Pattern(regexp = RegexpUtils.ONE_TO_THREE_NATURAL_NUMBER, message = "使用标识只能为1或2或3")
    @ApiModelProperty(value = "cumStatus",notes = "使用标识：1待使用 2已使用 3已过期",dataType = "String")
    private String cumStatus;
}
