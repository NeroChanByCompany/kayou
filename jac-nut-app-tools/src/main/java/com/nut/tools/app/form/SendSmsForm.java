package com.nut.tools.app.form;


import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import lombok.Data;

import java.io.Serializable;

/**
 * 发送短信接口
 * @author liuBing
 */
@Data
@NutFormValidator
public class SendSmsForm extends BaseForm implements Serializable {

    private static final long serialVersionUID = 1719945209549006343L;
    private String phone; // 手机号（必填）
    private String content; // 短信内容（必填）
    private String sign; // 签名（可选）


}
