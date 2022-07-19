package com.nut.driver.app.form;


import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 用户删除联系人接口
 */
@Data
@NutFormValidator
@ApiModel("app删除司机Entity")
public class UserContactDeleteForm extends BaseForm {
    /**
     * 联系人用户ID
     */
    @NotNull(message = "联系人用户ID不能为空")
    @ApiModelProperty(name = "delUserId" , notes = "联系人用户ID" , dataType = "Long")
    private Long delUserId;


    @Override
    public String toString() {
        return "UserContactDeleteCommand{" +
                "delUserId=" + delUserId +
                "} " + super.toString();
    }
}
