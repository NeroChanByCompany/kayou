package com.nut.driver.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Description: 车队与管理员绑定
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.form
 * @Author: yzl
 * @CreateTime: 2021-06-28 10:26
 * @Version: 1.0
 */
@Data
@NutFormValidator
public class FleetAdminBindForm extends BaseForm {
    /**
     * 车队ID
     */
    @ApiModelProperty(name = "teamId",notes = "车队ID",dataType = "Long")
    @NotNull(message = "车队ID不能为空")
    private Long teamId;
    /**
     * 管理员用户ID（支持半角逗号分隔）
     */
    @ApiModelProperty(name = "adminId",notes = "管理员用户ID",dataType = "String")
    @NotBlank(message = "管理员用户ID不能为空")
    private String adminId;
}
