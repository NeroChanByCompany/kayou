package com.nut.driver.app.form;

import com.nut.common.annotation.EmojiForbid;
import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * 用户创建车队接口
 * <p>y
 */
@Data
@NutFormValidator
@ApiModel("app创建车队Entity")
public class UserFleetAddForm extends BaseForm {
    /**
     * 车队名称
     */
    @NotBlank(message = "车队名称不能为空")
    @EmojiForbid(message = "车队名称存在非法字符")
    @Length(max = 20, message = "车队名称最多可输入20个字")
    @ApiModelProperty(name = "name" , notes = "车队名称" , dataType = "String")
    private String name;
    /**
     * 车队头像
     */
    @NotBlank(message = "车队头像不能为空")
    @Length(max = 200, message = "车队头像长度超出限制")
    @ApiModelProperty(name = "avatar" , notes = "车队头像" , dataType = "String")
    private String avatar;


    @Override
    public String toString() {
        return "UserFleetAddCommand{" +
                "name='" + name + '\'' +
                ", avatar='" + avatar + '\'' +
                "} " + super.toString();
    }

}
