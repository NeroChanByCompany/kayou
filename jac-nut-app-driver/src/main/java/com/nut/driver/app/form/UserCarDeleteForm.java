package com.nut.driver.app.form;


import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 用户删车接口
 */
@Data
@ApiModel("app删除车辆Entity")
public class UserCarDeleteForm extends BaseForm {
    /**
     * 车辆ID
     */
    @NotBlank(message = "车辆ID不能为空")
    @ApiModelProperty(name = "carId" , notes = "车辆ID" , dataType = "String")
    private String carId;


    @Override
    public String toString() {
        return "UserCarDeleteCommand{" +
                "carId='" + carId + '\'' +
                "} " + super.toString();
    }
}
