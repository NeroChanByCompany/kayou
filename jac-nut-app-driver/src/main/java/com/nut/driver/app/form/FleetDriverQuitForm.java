package com.nut.driver.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 司机退出车队接口
 */
@Data
@NutFormValidator
@ApiModel("app司机退出车队Entity")
public class FleetDriverQuitForm extends BaseForm {
    /**
     * 车队ID
     */
    @NotNull(message = "车队ID不能为空")
    @ApiModelProperty(name = "teamId" , notes = "车队ID" , dataType = "Long")
    private Long teamId;


    @Override
    public String toString() {
        return "FleetDriverQuitCommand{" +
                "teamId=" + teamId +
                "} " + super.toString();
    }

}
