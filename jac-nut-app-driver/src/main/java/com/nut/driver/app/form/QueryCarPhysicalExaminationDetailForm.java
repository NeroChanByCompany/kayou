package com.nut.driver.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Description: 车辆体检详情（包含重新体检）
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.form
 * @Author: yzl
 * @CreateTime: 2021-06-29 16:04
 * @Version: 1.0
 */
@Data
@NutFormValidator
@ApiModel("车辆体检详情实体")
public class QueryCarPhysicalExaminationDetailForm extends BaseForm {
    @NotNull(message = "车辆ID不能为空")
    @NotBlank(message = "车辆ID不能为空")
    @ApiModelProperty(name = "carId" , notes = "车辆ID" , dataType = "String")
    private String carId;

    @NotNull(message = "查询类型不能为空")
    @Range(min = 0, max = 1, message = "查询类型不正确")
    @ApiModelProperty(name = "type" , notes = "查询类型" , dataType = "Integer")
    private Integer type;
}
