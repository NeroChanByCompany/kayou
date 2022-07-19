package com.nut.servicestation.app.service;

import com.nut.servicestation.app.dto.UserInfoDto;
import com.nut.servicestation.app.form.*;

/**
 * @author liuBing
 * @Classname UserService
 * @Description TODO
 * @Date 2021/6/28 15:47
 */
public interface UserService {
    /**
     * 用户登录
     * @param form
     * @return
     */
    UserInfoDto login(LoginForm form);

    /**
     * 获取用户信息
     * @param form
     * @return
     */
    UserInfoDto getUserInfo(GetUserInfoForm form);

    /**
     * 修改密码
     * @param form
     * @return
     */
    UserInfoDto updatePassword(UpdatePasswordForm form);

    /**
     * 修改用户信息
     * @param form
     * @return
     */
    UserInfoDto updateUserInfo(UpdateUserInfoForm form);

    void logout(LogoutForm form);
    /**
     * 根据userId获取用户信息
     *
     * @param userId        userId
     * @param needCheckAuth 是否需要查询建单权限
     * @return UserInfoDto
     */
    UserInfoDto getUserInfoByUserId(String userId, boolean needCheckAuth);

    /**
     * 注销当前用户信息
     * @param form
     */
    void cancellation(LogoutForm form);

    /**
     * 查询账号名根据accountId
     * @param accountId
     * @return
     */
    String queryUserNameById(Long accountId);
}
