package com.nut.driver.app.form;;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Description: 我的预约详情
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.form;
 * @Author: yzl
 * @CreateTime: 2021-06-23 20:04
 * @Version: 1.0
 */
@Data
@NutFormValidator
@ApiModel("预约实体")
public class MyOrderDetailForm extends BaseForm {
    /**
     * 工单号
     */
    @NotNull(message = "工单号不能为空")
    @NotBlank(message = "工单号不能为空")
    @ApiModelProperty(name = "woCode" , notes = "工单号" , dataType = "String")
    private String woCode;
}
