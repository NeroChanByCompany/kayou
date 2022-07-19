package com.nut.driver.app.form;

import com.nut.common.annotation.EmojiForbid;
import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * 用户编辑车队接口
 */
@Data
@NutFormValidator
@ApiModel("app编辑车队Entity")
public class UserFleetUpdateForm extends BaseForm {
    /**
     * 车队ID
     */
    @NotNull(message = "车队ID不能为空")
    @ApiModelProperty(name = "teamId" , notes = "车队ID" , dataType = "Long")
    private Long teamId;
    /**
     * 车队名称（空则不更新）
     */
    @EmojiForbid(message = "车队名称存在非法字符")
    @Length(max = 20, message = "车队名称最多可输入20个字")
    @ApiModelProperty(name = "name" , notes = "车队名称" , dataType = "String")
    private String name;
    /**
     * 车队头像（空则不更新）
     */
    @Length(max = 200, message = "车队头像长度超出限制")
    @ApiModelProperty(name = "avatar" , notes = "车队头像" , dataType = "String")
    private String avatar;

    @Override
    public String toString() {
        return "UserFleetUpdateCommand{" +
                "teamId=" + teamId +
                ", name='" + name + '\'' +
                ", avatar='" + avatar + '\'' +
                "} " + super.toString();
    }

}
