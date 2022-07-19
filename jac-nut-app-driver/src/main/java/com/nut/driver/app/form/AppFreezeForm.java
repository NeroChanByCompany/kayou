package com.nut.driver.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author liuBing
 * @Classname AppFreezeForm
 * @Description TODO
 * @Date 2021/9/14 16:48
 */
@Data
@Accessors(chain = true)
@NutFormValidator
@ApiModel(value = "冻结用户")
public class AppFreezeForm {

    @NotNull(message = "手机号不能为空")
    @NotBlank(message = "手机号不能为空")
    @ApiModelProperty(name = "phone", notes = "手机号", dataType = "String")
    private String phone;
    @NotNull(message = "用户类型不能为空")
    @NotBlank(message = "用户类型不能为空")
    @ApiModelProperty(name = "appType", notes = "用户类型 0 用户 1 车队", dataType = "String")
    private String appType;

    @NotNull(message = "key不能为空")
    @NotBlank(message = "key不能为空")
    @ApiModelProperty(name = "key", notes = "加密key", dataType = "String")
    private String key;
    @ApiModelProperty(name = "remark", notes = "冻结or激活备注", dataType = "String")
    private String remark;
}
