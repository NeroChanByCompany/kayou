package com.nut.driver.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.form
 * @Author: yzl
 * @CreateTime: 2021-06-29 11:11
 * @Version: 1.0
 */
@Data
@NutFormValidator
@ApiModel("外出救援轨迹实体")
public class QueryHistoryJourneyForm extends BaseForm {
    /**
     * 工单号
     */
    @NotNull(message = "工单号不能为空")
    @ApiModelProperty(name = "woCode" , notes = "工单号" , dataType = "String")
    private String woCode;
}
