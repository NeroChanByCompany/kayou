package com.nut.servicestation.app.form;

import com.nut.common.base.BaseForm;
import lombok.Data;

/**
 * 获取用户信息
 */
@Data
public class GetUserInfoForm extends BaseForm {

    private String userName;

    @Override
    public String toString() {
        return "GetUserInfoCommand{" +
                '}';
    }
}
