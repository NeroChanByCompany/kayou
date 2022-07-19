package com.nut.driver.app.service;

import com.alibaba.fastjson.JSONObject;
import com.nut.driver.app.dto.AppLoginDTO;
import com.nut.driver.app.entity.UserEntity;
import com.nut.driver.app.form.AppLoginForm;
import com.nut.driver.app.form.RegisterForm;

/**
 * @Description: 用户中心
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.driver.service
 * @Author: yzl
 * @CreateTime: 2021-06-15 17:47
 * @Version: 1.0
 */

public interface UserCenterService {

    public JSONObject register(RegisterForm form);

    /**
     * 登录
     * @param form
     * @param user
     * @return
     */
    public AppLoginDTO login(AppLoginForm form, UserEntity user);
}
