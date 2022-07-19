package com.nut.driver.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author liuBing
 * @Classname QueryCarNewMaintainDetailsFrom
 * @Description TODO
 * @Date 2021/6/24 10:25
 */
@Data
@NutFormValidator
@ApiModel("推荐保养详情")
public class QueryCarNewMaintainDetailsForm extends BaseForm {
    /**
     * 底盘号
     */
    @NotNull(message = "车辆底盘号不能为空")
    @NotBlank(message = "车辆底盘号不能为空")
    @ApiModelProperty(name = "carVin",notes = "底盘号",dataType = "String")
    private String carVin;

    /**
     * 保养项目类编码
     */
    @ApiModelProperty(name = "classifyCode",notes = "保养项目类编码",dataType = "String")
    private String classifyCode;
}
