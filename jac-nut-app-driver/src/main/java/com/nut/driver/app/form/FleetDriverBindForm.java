package com.nut.driver.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


/**
 * 车队与司机绑定接口
 */
@Data
@NutFormValidator
@ApiModel("app车队与司机绑定Entity")
public class FleetDriverBindForm extends BaseForm {
    /**
     * 车队ID
     */
    @NotNull(message = "车队ID不能为空")
    @ApiModelProperty(value = "teamId",notes = "车队ID",dataType = "Long")
    private Long teamId;
    /**
     * 司机用户ID（支持半角逗号分隔）
     */
    @NotBlank(message = "司机用户ID不能为空")
    @ApiModelProperty(value = "driverId",notes = "司机用户Id",dataType = "Long")
    private String driverId;


    @Override
    public String toString() {
        return "FleetDriverBindCommand{" +
                "teamId=" + teamId +
                ", driverId='" + driverId + '\'' +
                "} " + super.toString();
    }

}
