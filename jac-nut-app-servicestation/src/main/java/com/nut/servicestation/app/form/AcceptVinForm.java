package com.nut.servicestation.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Description: 确认前台传递过来的VIN
 */
@Data
@NutFormValidator
public class AcceptVinForm extends BaseForm {

    /**
     * 车辆VIN号
     */
    @NotNull(message = "底盘号不能为空")
    @NotBlank(message = "底盘号不能为空")
    @Length(min = 8, max = 17, message = "车辆底盘号不正确")
    private String carVin;

}
