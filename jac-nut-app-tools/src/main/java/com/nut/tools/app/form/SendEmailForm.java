package com.nut.tools.app.form;


import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 发送邮件
 * @author liubing
 * @version 1.0
 */
@Data
@NutFormValidator
public class SendEmailForm extends BaseForm implements Serializable {
    private static final long serialVersionUID = 8199816813334633508L;
    private String wm;// 获取邮件信息的下标值
    private List<String> toEmails;// 收件人邮箱
    private List<String> ccEmails;// 抄送人邮箱
    private List<String> bccEmails;// 密送人邮箱
    private String subject;// 邮件标题
    private String content;// 邮件正文


}
