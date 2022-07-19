package com.nut.driver.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import com.nut.common.utils.RegexpUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@NutFormValidator
public class EditMaintenanceInfoForm extends BaseForm {
    /**
     * 自定义保养记录id
     */
    @ApiModelProperty(name = "maintainId", notes = "自定义保养记录id", dataType = "String")
    @NotNull(message = "自定义保养记录id不能为空")
    @NotBlank(message = "自定义保养记录id不能为空")
    private String maintainId;


    /**
     * 保养名称
     */
    @NotNull(message = "保养名称不能为空")
    @NotBlank(message = "保养名称不能为空")
    @ApiModelProperty(name = "maintainName", notes = "保养名称", dataType = "String")
    private String maintainName;

    /**
     * 提醒方式 1：里程；2：时间
     */
    @NotNull(message = "提醒方式不能为空")
    @NotBlank(message = "提醒方式不能为空")
    @Pattern(regexp = RegexpUtils.ONE__TO_TWO_NATURAL_NUMBER, message = "提醒方式值只能为1或2")
    @ApiModelProperty(name = "maintainType", notes = "提醒方式（1：里程；2：时间）", dataType = "String")
    private String maintainType;

    /**
     * 提醒内容
     */
    @NotNull(message = "提醒内容不能为空")
    @NotBlank(message = "提醒内容不能为空")
    @ApiModelProperty(name = "maintainDescribe", notes = "提醒内容", dataType = "String")
    private String maintainDescribe;

    /**
     * 车辆id
     */
    @NotNull(message = "车辆ID不能为空")
    @NotBlank(message = "车辆ID不能为空")
    @ApiModelProperty(name = "carId", notes = "车辆id", dataType = "String")
    private String carId;

    /**
     * 车牌号
     */
    @NotNull(message = "车牌号不能为空")
    @NotBlank(message = "车牌号不能为空")
    @ApiModelProperty(name = "carNumber", notes = "车牌号", dataType = "String")
    private String carNumber;

    /**
     * 备注
     */
    @ApiModelProperty(name = "remarks", notes = "备注", dataType = "String")
    private String remarks;

}
