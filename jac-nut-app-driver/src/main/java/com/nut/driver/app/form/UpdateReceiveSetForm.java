package com.nut.driver.app.form;

import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @Description: 消息和报警通知接收设置
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.form
 * @Author: yzl
 * @CreateTime: 2021-06-30 11:16
 * @Version: 1.0
 */
@Data
@ApiModel("消息和报警通知实体")
public class UpdateReceiveSetForm extends BaseForm {
    /**
     * 消息类型
     */
    @NotNull(message = "消息类型不能为空")
    @ApiModelProperty(name = "messageType" , notes = "消息类型" , dataType = "Integer")
    private Integer messageType;

    /**
     * 消息接收状态
     */
    @NotNull(message = "消息接收状态不能为空")
    @Min(value = 0, message = "消息接收状态的值不合法")
    @Max(value = 1, message = "消息接收状态的值不合法")
    @ApiModelProperty(name = "receiveState" , notes = "消息接收状态" , dataType = "Integer")
    private Integer receiveState;
}
