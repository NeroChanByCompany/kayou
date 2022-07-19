package com.nut.locationservice.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Description: 生成车辆故障信息文件
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.app.locationService.form
 * @Author: yzl
 * @CreateTime: 2021-06-17 09:10
 * @Version: 1.0
 */
@Data
@Accessors(chain = true)
@ToString
@NutFormValidator
public class GenerateCarInfoFileForm extends BaseForm implements Serializable {

    /**
     * 处理日期
     */
    @NotNull(message = "处理日期不能为空")
    @NotBlank(message = "处理日期不能为空")
    @ApiModelProperty(name = "procDate", notes = "处理日期", dataType = "String")
    private String procDate;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    @NotBlank(message = "用户ID不能为空")
    @ApiModelProperty(name = "usr", notes = "用户Id", dataType = "String")
    private String usr;

    /**
     * 密码
     */
    @NotNull(message = "密码不能为空")
    @NotBlank(message = "密码不能为空")
    @ApiModelProperty(name = "pwd", notes = "密码", dataType = "String")
    private String pwd;

}
