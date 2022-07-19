package com.nut.driver.app.form;

import com.nut.common.annotation.NutFormValidator;
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
public class AppFreezeTokenForm {
    @NotNull(message = "id不能为空")
    @NotBlank(message = "id不能为空")
    @ApiModelProperty(name = "id", notes = "用户唯一标识", dataType = "String")
    private Long id;

    @NotNull(message = "key不能为空")
    @NotBlank(message = "key不能为空")
    @ApiModelProperty(name = "key", notes = "加密key", dataType = "String")
    private String key;
    @ApiModelProperty(name = "remark", notes = "冻结or激活备注", dataType = "String")
    private String remark;
}
