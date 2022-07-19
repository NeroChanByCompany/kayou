package com.nut.tools.app.service;

import com.nut.tools.app.form.PushMesForm;

/**
 * @author liuBing
 * @Classname PushMessageService
 * @Description TODO
 * @Date 2021/6/22 17:51
 */
public interface PushMessageService {
    /**
     * 推送消息
     * @param from
     */
    void pushMessageGetTemplate(PushMesForm from);
}
