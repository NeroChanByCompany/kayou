package com.nut.driver.app.form;

import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 车辆体检
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.form
 * @Author: yzl
 * @CreateTime: 2021-06-29 15:40
 * @Version: 1.0
 */
@Data
@ApiModel("车辆体检实体")
public class QueryCarPhysicalExaminationForm extends BaseForm {
    @ApiModelProperty(name = "carNumber" , notes = "车牌号" , dataType = "String")
    private String carNumber;
}
