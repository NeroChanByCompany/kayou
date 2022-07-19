package com.nut.driver.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Pattern;

/**
 * @Description: 我的预约列表
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.form
 * @Author: yzl
 * @CreateTime: 2021-06-27 13:38
 * @Version: 1.0
 */
@Data
@NutFormValidator
@ApiModel("预约列表实体")
public class MyOrdersForm extends BaseForm {
    /**
     * 查询待评价列表标志(为1时为查询待评价列表)
     */
    @Pattern(regexp = "^[0-2]$", message = "标志不合法！")
    @ApiModelProperty(name = "queryTag" , notes = "查询标志" , dataType = "String")
    private String queryTag;
}
