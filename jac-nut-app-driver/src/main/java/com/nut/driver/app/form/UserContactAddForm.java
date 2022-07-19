package com.nut.driver.app.form;

import com.nut.common.annotation.EmojiForbid;
import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import com.nut.common.utils.RegexpUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 用户添加联系人接口
 */
@Data
@NutFormValidator
@ApiModel("app新增司机Entity")
public class UserContactAddForm extends BaseForm {
    /**
     * 昵称
     */
    @NotBlank(message = "昵称不能为空")
    @EmojiForbid(message = "昵称存在非法字符")
    @Length(max = 20, message = "昵称最多可输入20个字")
    @ApiModelProperty(name = "nickname" , notes = "昵称" , dataType = "String")
    private String nickname;
    /**
     * 电话
     */
    @NotBlank(message = "电话不能为空")
    @Pattern(regexp = RegexpUtils.MOBILE_PHONE_REGEXP, message = "请输入11位手机号")
    @ApiModelProperty(name = "phone" , notes = "手机号" , dataType = "String")
    private String phone;


    @Override
    public String toString() {
        return "UserContactAddCommand{" +
                "nickname='" + nickname + '\'' +
                ", phone='" + phone + '\'' +
                "} " + super.toString();
    }

}
