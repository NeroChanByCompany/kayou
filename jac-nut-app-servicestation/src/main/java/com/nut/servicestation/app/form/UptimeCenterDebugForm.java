package com.nut.servicestation.app.form;

import com.nut.common.base.BaseForm;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * UptimeCenterDebugCommand
 *
 * @author Administrator
 * @date 2019/5/21
 */
@Data
public class UptimeCenterDebugForm extends BaseForm {
    @NotBlank(message = "key不能为空")
    private String key;
    @NotBlank(message = "step不能为空")
    private String step;
    private String type;
    private String mileage;
    private List<?> list;
    private String suffix;
}
