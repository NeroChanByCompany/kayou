package com.nut.servicestation.app.form;


import com.nut.common.base.BaseForm;
import lombok.Data;

/**
 * 修改用户信息
 */
@Data
public class UpdateUserInfoForm extends BaseForm {
    /** 姓名 */
    private String accountName;
    /** 联系方式 */
    private String phone;


    @Override
    public String toString() {
        return "UpdateUserInfoCommand{" +
                "accountName='" + accountName + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }

}
