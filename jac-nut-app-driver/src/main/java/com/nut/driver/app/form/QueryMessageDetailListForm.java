package com.nut.driver.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Description: 查询消息详情列表
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.form
 * @Author: yzl
 * @CreateTime: 2021-06-27 13:22
 * @Version: 1.0
 */
@Data
@NutFormValidator
@ApiModel("消息列表实体")
public class QueryMessageDetailListForm extends BaseForm {
    /**
     * 消息类型
     */
    @NotNull(message = "消息类型不能为空")
    @ApiModelProperty(name = "messageType" , notes = "消息类型" , dataType = "Integer")
    private Integer messageType;

}
