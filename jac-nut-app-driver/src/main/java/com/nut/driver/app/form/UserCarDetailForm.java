package com.nut.driver.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Description: 用户车辆详情
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.form
 * @Author: yzl
 * @CreateTime: 2021-06-29 17:19
 * @Version: 1.0
 */
@Data
@NutFormValidator
public class UserCarDetailForm extends BaseForm {
    /**
     * 车辆ID
     */
    @NotBlank(message = "车辆ID不能为空")
    @ApiModelProperty(name = "carId", notes = "车辆id", dataType = "String")
    private String carId;
    /**
     * 只获取实时信息标识（1：是，当设置此值时，返回字段只有实时信息，其他字段均返回null）
     */
    @ApiModelProperty(name = "realtimeFlag", notes = "只获取实时信息标识（1：是，当设置此值时，返回字段只有实时信息，其他字段均返回null）", dataType = "String")
    private String realtimeFlag;
}
