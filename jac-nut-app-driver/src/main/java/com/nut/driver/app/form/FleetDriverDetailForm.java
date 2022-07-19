package com.nut.driver.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 车队司机详情接口
 */
@Data
@NutFormValidator
@ApiModel("app查询司机详情Entity")
public class FleetDriverDetailForm extends BaseForm {
    /**
     * 车队ID
     */
    @NotNull(message = "车队ID不能为空")
    @ApiModelProperty(name = "teamId" , notes = "车队ID" , dataType = "Long")
    private Long teamId;

    /**
     * 司机用户ID
     */
    @NotNull(message = "司机用户ID不能为空")
    @ApiModelProperty(name = "driverId" , notes = "司机ID" , dataType = "Long")
    private Long driverId;

    @Override
    public String toString() {
        return "FleetDriverDetailCommand{" +
                "teamId=" + teamId +
                ", driverId=" + driverId +
                "} " + super.toString();
    }
}
