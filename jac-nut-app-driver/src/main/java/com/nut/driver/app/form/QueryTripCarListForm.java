package com.nut.driver.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import com.nut.common.utils.RegexpUtils;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.form
 * @Author: yzl
 * @CreateTime: 2021-06-30 09:40
 * @Version: 1.0
 */
@Data
@NutFormValidator
@ApiModel("x")
public class QueryTripCarListForm extends BaseForm {
    /**
     * 查询日期
     */
    @NotNull(message = "查询日期不能为空！")
    @NotBlank(message = "查询日期不能为空！")
    @Pattern(message = "日期格式不合法！", regexp = RegexpUtils.DATE_BARS_DAY_REGEXP)
    private String queryTime;

    /**
     * 车牌号
     */
    private String carNumber;
}
