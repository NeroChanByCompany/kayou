package com.nut.driver.app.service;

import com.nut.driver.app.form.SendSmsForm;

import java.util.Map;

/**
 * @Description: 发送短信
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.driver.service
 * @Author: yzl
 * @CreateTime: 2021-06-16 09:22
 * @Version: 1.0
 */
public interface SendSmsService {

    Map<String, Object> sendSms(SendSmsForm form);

}
