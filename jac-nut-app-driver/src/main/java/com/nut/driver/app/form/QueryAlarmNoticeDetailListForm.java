package com.nut.driver.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Description: 查询报警通知详情列表
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.form
 * @Author: yzl
 * @CreateTime: 2021-06-30 10:54
 * @Version: 1.0
 */
@NutFormValidator
@Data
@ApiModel("报警通知实体")
public class QueryAlarmNoticeDetailListForm extends BaseForm {
    /**
     * 车辆ID
     */
    @NotNull(message = "车辆ID不能为空")
    @NotBlank(message = "车辆ID不能为空")
    @ApiModelProperty(name = "carId" , notes = "车辆ID" , dataType = "String")
    private String carId;
}
