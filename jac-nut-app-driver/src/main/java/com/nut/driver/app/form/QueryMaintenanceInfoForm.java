package com.nut.driver.app.form;


import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author liuBing
 * @Classname QueryMaintenanceInfoFrom
 * @Description TODO
 * @Date 2021/6/24 15:12
 */
@Data
@NutFormValidator
@ApiModel(value = "查询自定义保养项目")
public class QueryMaintenanceInfoForm extends BaseForm {

    /**
     * 自定义保养记录id
     */
    @NotNull(message = "自定义保养记录id不能为空")
    @NotBlank(message = "自定义保养记录id不能为空")
    @ApiModelProperty(name = "maintainId",notes = "自定义保养记录id",dataType = "String")
    private String maintainId;
}
