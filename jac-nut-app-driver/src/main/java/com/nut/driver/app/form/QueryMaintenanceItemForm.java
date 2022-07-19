package com.nut.driver.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author liuBing
 * @Classname QueryMaintenanceItemForm
 * @Description TODO 查询保养信息下保养项目列表
 * @Date 2021/6/27 13:45
 */
@Data
@NutFormValidator
public class QueryMaintenanceItemForm extends BaseForm {

    /**
     * 自定义保养记录id
     */
    @NotNull(message = "自定义保养记录id不能为空")
    @NotBlank(message = "自定义保养记录id不能为空")
    @ApiModelProperty(name = "maintainId", notes = "自定义保养记录Id", dataType = "String")
    private String maintainId;

}
