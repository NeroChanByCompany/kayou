package com.nut.driver.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 帮助手册列表
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.form
 * @Author: yzl
 * @CreateTime: 2021-06-28 14:14
 * @Version: 1.0
 */
@Data
@NutFormValidator
@ApiModel("帮助手册实体")
public class OperationManualListForm extends BaseForm {

    @ApiModelProperty(name = "appCode" , notes = "生效范围" , dataType = "String")
    private String appCode;

}
