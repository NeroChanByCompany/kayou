package com.nut.driver.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import lombok.Data;

/**
 * @Description: 新增卡友圈消息
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.form
 * @Author: yzl
 * @CreateTime: 2021-07-29 15:24
 * @Version: 1.0
 */
@NutFormValidator
@Data
public class AddUnreadMessageForm extends BaseForm {
    /**
     * 接收者
     */
    private String receiverId;
    /**
     * 消息标题
     */
    private String title;
    /**
     * 消息内容
     */
    private String content;

    /**
     * 设备登录标识
     * @return
     */
    private String deviceType;

    /**
     * 帖子ID
     */
    private String invitationId;
}
