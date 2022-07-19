package com.nut.driver.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;

/**
 * 车队与司机解绑接口
 */
@Data
@NutFormValidator
@ApiModel("app车队与司机解绑Entity")
public class FleetDriverUnbindForm extends BaseForm {
    /**
     * 车队ID
     */
    @NotNull(message = "车队ID不能为空")
    @ApiModelProperty(name = "teamId" , notes = "车队ID" , dataType = "Long")
    private Long teamId;
    /**
     * 司机用户ID（支持半角逗号分隔）
     */
    @NotBlank(message = "司机用户ID不能为空")
    @ApiModelProperty(name = "driverId" , notes = "司机ID" , dataType = "Long")
    private String driverId;


    @Override
    public String toString() {
        return "FleetDriverUnbindCommand{" +
                "teamId=" + teamId +
                ", driverId='" + driverId + '\'' +
                "} " + super.toString();
    }

}
