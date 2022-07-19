package com.nut.driver.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @Description: 功能 紧急电话服务查询接口 APP端使用
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.form
 * @Author: yzl
 * @CreateTime: 2021-06-28 14:46
 * @Version: 1.0
 */
@Data
@NutFormValidator
@ApiModel("紧急电话实体")
public class QueryAppUrgentCallForm extends BaseForm {
    /** 名称 */
    @ApiModelProperty(name = "name" , notes = "名称" , dataType = "String")
    private String name;

    /** 1、常用,2、救援,3、保险公司 */
    @NotBlank(message = "type不能为空")
    @Pattern(regexp = "^[1-3]$", message = "type格式不正确")
    @ApiModelProperty(name = "type" , notes = "类型" , dataType = "String")
    private String type;
}
