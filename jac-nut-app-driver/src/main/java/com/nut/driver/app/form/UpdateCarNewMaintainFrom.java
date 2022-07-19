package com.nut.driver.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author liuBing
 * @Classname UpdateCarNewMaintainFrom
 * @Description TODO
 * @Date 2021/6/24 13:54
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("更新车辆保养信息")
@NutFormValidator
public class UpdateCarNewMaintainFrom extends BaseForm {
    @NotNull(message = "车辆保养id不能为空")
    @NotBlank(message = "车辆保养id不能为空")
    @ApiModelProperty(name = "carMaintainIdList",notes = "车辆保养id",dataType = "String")
    private String carMaintainIdList;

    @NotNull(message = "保养里程不能为空")
    @NotBlank(message = "保养里程不能为空")
    @ApiModelProperty(name = "maintainMileage",notes = "保养里程",dataType = "String")
    private String maintainMileage;

    @NotNull(message = "保养时间不能为空")
    @NotBlank(message = "保养时间不能为空")
    @ApiModelProperty(name = "maintainMileage",notes = "保养时间",dataType = "String")
    private String maintainTime;

    /**
     * 保养更新来源：客户APP：1；服务APP：2；crm同步：3
     */
    @ApiModelProperty(name = "maintainSource",notes = "保养更新来源：客户APP：1；服务APP：2；crm同步：3",dataType = "String")
    private Integer maintainSource =  1;

}
