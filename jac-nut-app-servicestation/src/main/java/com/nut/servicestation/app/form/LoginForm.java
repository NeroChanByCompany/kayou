package com.nut.servicestation.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 服务APP登录接口
 */
@NutFormValidator
@Data
public class LoginForm extends BaseForm {

    @NotNull(message = "用户名不能为空")
    @NotBlank(message = "用户名不能为空")
    private String userName;

    @NotNull(message = "密码不能为空" )
    @NotBlank(message = "密码不能为空" )
    private String password;
    @NotNull(message = "设备Id不能为空" )
    @NotBlank(message = "设备Id不能为空" )
    private String deviceId;

    @NotNull(message = "sendMessageKey不能为空" )
    @NotBlank(message = "sendMessageKey不能为空" )
    private String sendMessageKey;

    @Override
    public String toString() {
        return "LoginCommand{" +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", sendMessageKey='" + sendMessageKey + '\'' +
                '}';
    }
}
