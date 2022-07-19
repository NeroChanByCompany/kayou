package com.nut.driver.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Description: 获取服务站详情数据
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.driver.form
 * @Author: yzl
 * @CreateTime: 2021-06-23 14:09
 * @Version: 1.0
 */
@Data
@NutFormValidator
@ApiModel("服务站详情实体")
public class QueryServiceStationDetailForm extends BaseForm {
    @NotNull(message = "服务站id不能为空")
    @NotBlank(message = "服务站id不能为空")
    @ApiModelProperty(name = "stationId" , notes = "服务站ID" , dataType = "String")
    private String stationId;
}
