package com.nut.driver.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Description: 用户自定义
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.form
 * @Author: yzl
 * @CreateTime: 2021-06-27 15:46
 * @Version: 1.0
 */
@Data
@NutFormValidator
@ApiModel("用户自定义实体")
public class QueryUserConfForm extends BaseForm {
    @NotBlank(message = "topic不能为空")
    @ApiModelProperty(name = "topic" , dataType = "String")
    private String topic;

    @NotBlank(message = "key不能为空")
    @ApiModelProperty(name = "key" , dataType = "String")
    private String key;
}
