package com.nut.servicestation.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import com.nut.common.utils.RegexpUtils;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 查询预约车辆接口
 *
 * @author Administrator
 * @date 2019/5/31
 */
@Data
@NutFormValidator
public class OrderCarForm extends BaseForm {
    /**
     * 底盘号
     */
    @NotBlank(message = "底盘号不能为空")
    @Pattern(regexp = RegexpUtils.EIGHT_CHASSIS_NUMBER, message = "底盘号格式不正确")
    private String chassisNum;

}
