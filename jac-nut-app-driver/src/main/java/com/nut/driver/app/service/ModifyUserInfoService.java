package com.nut.driver.app.service;

import com.nut.driver.app.form.ModifyUserInfoForm;

/**
 * @author liuBing
 * @Classname ModifyUserInfoService
 * @Description TODO
 * @Date 2021/6/17 9:33
 */
public interface ModifyUserInfoService {
    /**
     * 获取验证码
     * @param form
     * @return
     */
    String checkSmsCode2(ModifyUserInfoForm form);
}
