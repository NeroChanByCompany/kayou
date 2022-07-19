package com.nut.driver.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author liuBing
 * @Classname ViolationForm
 * @Description TODO 获取车辆位置信息
 * @Date 2021/8/20 13:10
 */
@Data
@ApiModel("获取车辆位置信息")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@NutFormValidator
public class ViolationForm extends BaseForm implements Serializable {
    private static final long serialVersionUID = 7309194671860932401L;
    /**
     * 车辆类型 02燃油车 51新能源车
     */
    @ApiModelProperty(name = "type" , notes = "车辆类型" , dataType = "String")
    @NotBlank(message = "车辆类型不能为空")
    private String type;
    /**
     * 车牌号
     */
    @ApiModelProperty(name = "plateno" , notes = "车牌号" , dataType = "String")
    @NotBlank(message = "车牌号不能为空")
    private String plateno;
    /**
     * 发动机号
     */
    @ApiModelProperty(name = "engineno" , notes = "发动机号" , dataType = "String")
    @NotBlank(message = "发动机号不能为空")
    private String engineno;
    /**
     * 车架号
     */
    @ApiModelProperty(name = "frameno" , notes = "车架号" , dataType = "String")
    @NotBlank(message = "车架号不能为空")
    private String frameno;

}
