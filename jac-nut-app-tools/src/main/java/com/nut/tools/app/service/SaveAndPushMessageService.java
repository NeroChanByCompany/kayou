package com.nut.tools.app.service;

import com.nut.tools.app.form.PushMesForm;

import java.util.List;

/**
 * @author liuBing
 * @Classname SaveAndPushMessageService
 * @Description TODO
 * @Date 2021/6/22 19:38
 */
public interface SaveAndPushMessageService {

    public void saveCarMessageRecord(String messageId, String title, String content, String appType, PushMesForm from);

    public void savePushUserMessageRecord(String messageId, String title, String content, String appType, PushMesForm from, List<String> notReceiverIdList);
}
