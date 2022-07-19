package com.nut.driver.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 车队与车辆绑定接口
 */
@Data
@NutFormValidator
@ApiModel("app车队与车辆绑定Entity")
public class FleetCarBindForm extends BaseForm {
    /**
     * 车队ID
     */
    @NotNull(message = "车队ID不能为空")
    @ApiModelProperty(name = "teamId" , notes = "车队ID" , dataType = "Long")
    private Long teamId;
    /**
     * 车辆ID（支持半角逗号分隔）
     */
    @NotBlank(message = "车辆ID不能为空")
    @ApiModelProperty(name = "carId" , notes = "车辆ID" , dataType = "String")
    private String carId;


    @Override
    public String toString() {
        return "FleetCarBindCommand{" +
                "teamId=" + teamId +
                ", carId='" + carId + '\'' +
                "} " + super.toString();
    }
}
