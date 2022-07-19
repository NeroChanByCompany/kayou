package com.nut.driver.app.form;


import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 车辆与司机解绑接口
 */
@Data
@NutFormValidator
@ApiModel("app司机与车辆解绑Entity")
public class CarDriverUnbindForm extends BaseForm {
    /**
     * 车队ID
     */
    @NotNull(message = "车队ID不能为空")
    @ApiModelProperty(name = "teamId" , notes = "车队ID" , dataType = "Long")
    private Long teamId;
    /**
     * 多选标识
     * 1：carId逗号分隔、driverId一个
     * 2：carId一个、driverId逗号分隔
     */
    @NotNull(message = "多选标识不能为空")
    @ApiModelProperty(name = "multipleFlag" , notes = "多选标识" , dataType = "Integer")
    private Integer multipleFlag;
    /**
     * 车辆ID（支持半角逗号分隔）
     */
    @NotBlank(message = "车辆ID不能为空")
    @ApiModelProperty(name = "carId" , notes = "车辆ID" , dataType = "String")
    private String carId;
    /**
     * 司机用户ID（支持半角逗号分隔）
     */
    @NotBlank(message = "司机用户ID不能为空")
    @ApiModelProperty(name = "driverId" , notes = "司机ID" , dataType = "String")
    private String driverId;


    @Override
    public String toString() {
        return "CarDriverUnbindCommand{" +
                "teamId=" + teamId +
                ", multipleFlag=" + multipleFlag +
                ", carId='" + carId + '\'' +
                ", driverId='" + driverId + '\'' +
                "} " + super.toString();
    }

}
