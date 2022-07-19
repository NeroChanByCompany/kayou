package com.nut.servicestation.app.form;


import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NutFormValidator
public class UpdatePasswordForm extends BaseForm {

    @NotNull(message = "旧密码不能为空" )
    @NotBlank(message = "旧密码不能为空" )
    private String oldPassword;

    @NotNull(message = "新密码不能为空" )
    @NotBlank(message = "新密码不能为空" )
    private String newPassword;

    private String accountName;


    @Override
    public String toString() {
        return "UpdatePasswordCommand{" +
                "oldPassword='" + oldPassword + '\'' +
                ", newPassword='" + newPassword + '\'' +
                '}';
    }

}
