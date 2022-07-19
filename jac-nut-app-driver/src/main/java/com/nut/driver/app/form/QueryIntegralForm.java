package com.nut.driver.app.form;


import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 积分模型
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.driver.form
 * @Author: yzl
 * @CreateTime: 2021-06-21 13:33
 * @Version: 1.0
 */
@Data
@ApiModel(value = "查询积分Form")
public class QueryIntegralForm extends BaseForm {
    @ApiModelProperty(name = "ucId",notes = "用户ucid",dataType = "String")
    private String ucId;

}
