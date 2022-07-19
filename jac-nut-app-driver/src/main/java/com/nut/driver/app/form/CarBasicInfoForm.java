package com.nut.driver.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * @author liuBing
 * @Classname CarBasicInfoForm
 * @Description TODO
 * @Date 2021/8/3 13:48
 */
@ApiModel("获取车辆基础信息")
@Data
@Accessors(chain = true)
@NutFormValidator
public class CarBasicInfoForm extends BaseForm {
    /**
     * 车辆底盘号
     */
    @NotBlank(message = "车辆底盘号不能为空")
    @ApiModelProperty(name = "carVin",notes = "车辆底盘号",dataType = "String")
    private String carVin;
}
