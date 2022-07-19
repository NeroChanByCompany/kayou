package com.nut.servicestation.app.form;



import com.nut.common.utils.StringUtil;
import lombok.Data;

import java.util.List;

/**
 * 发送邮件
 * @author zhangwei
 * @version 1.0
 * @since 2017-03-23
 */
@Data
public class SendEmailForm {
    private String wm;// 获取邮件信息的下标值
    private List<String> toEmails;// 收件人邮箱
    private List<String> ccEmails;// 抄送人邮箱
    private List<String> bccEmails;// 密送人邮箱
    private String subject;// 邮件标题
    private String content;// 邮件正文

    public String getWm() {
        return wm;
    }

    public void setWm(String wm) {
        if(StringUtil.isNull(wm)){
            wm = "1";
        }
        this.wm = wm;
    }

}
