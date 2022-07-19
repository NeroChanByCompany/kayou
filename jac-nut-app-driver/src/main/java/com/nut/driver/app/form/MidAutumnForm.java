package com.nut.driver.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Description: 中秋活动特典使用
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.form
 * @Author: yzl
 * @CreateTime: 2021-09-13 15:37
 * @Version: 1.0
 */
@Data
@NutFormValidator
public class MidAutumnForm extends BaseForm {

    @NotNull(message = "活动类型不能为空")
    @NotBlank(message = "活动类型不能为空")
    // 0：浏览车联网页面  1：推荐注册
    private Integer midAutumnType;
}
