package com.nut.driver.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import com.nut.common.utils.RegexpUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;


@Data
@NutFormValidator
@ApiModel(value = "添加车辆保养信息")
public class AddMaintenanceInfoForm extends BaseForm {

    /**
     * 车队ID
     */
    @ApiModelProperty(value = "teamId",notes = "车队ID",dataType = "String")
    private String teamId;
    /**
     * 保养名称
     */
    @NotNull(message = "保养名称不能为空")
    @NotBlank(message = "保养名称不能为空")
    @ApiModelProperty(value = "maintainName",notes = "保养名称",dataType = "String")
    private String maintainName;

    /**
     * 提醒方式 1：里程；2：时间
     */
    @NotNull(message = "提醒方式不能为空")
    @NotBlank(message = "提醒方式不能为空")
    @Pattern(regexp = RegexpUtils.ONE__TO_TWO_NATURAL_NUMBER, message = "提醒方式值只能为1或2")
    @ApiModelProperty(value = "maintainType",notes = "提醒方式 1：里程；2：时间",dataType = "String")
    private String maintainType;

    /**
     * 提醒内容
     */
    @NotNull(message = "提醒内容不能为空")
    @NotBlank(message = "提醒内容不能为空")
    @ApiModelProperty(value = "maintainDescribe",notes = "提醒内容",dataType = "String")
    private String maintainDescribe;

    /**
     * 车辆id
     */
    @NotNull(message = "车辆id不能为空")
    @NotBlank(message = "车辆id不能为空")
    @ApiModelProperty(value = "carId",notes = "车辆id",dataType = "String")
    private String carId;

    /**
     * 车牌号
     */
    @NotNull(message = "车牌号不能为空")
    @NotBlank(message = "车牌号不能为空")
    @ApiModelProperty(value = "carNumber",notes = "车牌号",dataType = "String")
    private String carNumber;

    /**
     * 备注
     */
    @ApiModelProperty(value = "remarks",notes = "备注",dataType = "String")
    private String remarks;

    /**
     * 保养项目id列表 id用“,”分割
     */
    @ApiModelProperty(value = "maintainItemIdList",notes = "保养项目id列表 id用“,”分割",dataType = "String")
    private String maintainItemIdList;
}
