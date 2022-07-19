package com.nut.driver.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Description: 查询车辆拓展信息
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.form
 * @Author: yzl
 * @CreateTime: 2021-06-29 09:52
 * @Version: 1.0
 */
@Data
@NutFormValidator
@ApiModel(value = "查询车辆拓展信息Form")
public class QueryCarExtForm extends BaseForm {
    /**
     * 车辆Id
     */
    @NotNull(message = "车辆Id不能为空")
    @ApiModelProperty(name = "carId",notes = "车辆Id",dataType = "Long")
    private Long carId;

}
