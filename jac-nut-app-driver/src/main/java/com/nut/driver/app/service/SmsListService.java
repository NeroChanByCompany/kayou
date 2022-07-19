package com.nut.driver.app.service;

/**
 * @Description: 电话号码非法，发送验证码失败记录插入
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.service
 * @Author: yzl
 * @CreateTime: 2021-10-25 17:01
 * @Email: yzl379131121@163.com
 * @Version: 1.0
 */
public interface SmsListService {

    void recordPhone(String phone);
}
