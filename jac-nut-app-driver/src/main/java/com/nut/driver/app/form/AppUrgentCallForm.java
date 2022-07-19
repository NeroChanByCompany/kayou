package com.nut.driver.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Description: 功能 紧急电话服务查询接口 APP端使用
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.form
 * @Author: yzl
 * @CreateTime: 2021-06-28 14:35
 * @Version: 1.0
 */
@Data
@NutFormValidator
@ApiModel("救援电话实体")
public class AppUrgentCallForm extends BaseForm {
    /**
     *  救援电话ID
     */
    @NotBlank(message = "救援电话ID不能为空")
    @ApiModelProperty(name = "id" , notes = "救援电话ID" , dataType = "String")
    private String id;

}
