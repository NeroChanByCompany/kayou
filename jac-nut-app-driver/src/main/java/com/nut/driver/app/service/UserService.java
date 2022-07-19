package com.nut.driver.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.Result;
import com.nut.driver.app.dto.AppLoginDTO;
import com.nut.driver.app.dto.RegisterDTO;
import com.nut.driver.app.dto.StarSignDto;
import com.nut.driver.app.dto.UserInfoDTO;
import com.nut.driver.app.entity.UserEntity;
import com.nut.driver.app.form.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户表
 *
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-15 16:45:02
 */
public interface UserService extends IService<UserEntity> {

    /**
     * 注册接口
     *
     * @param form
     * @return
     */
    RegisterDTO register(RegisterForm form);

    /**
     * 忘记密码
     *
     * @param form
     * @return
     */
    String forgetPassword(ForgetPasswordForm form);

    /**
     * 获取验证码
     *
     * @param form
     * @return
     */
    void smsVerificationCode(SmsVerificationCodeForm form) throws Exception;

    /**
     * @Author liuBing
     * @Description //TODO 账号登录
     * @Date 17:42 2021/6/16
     * @Param [form]
     * @return java.lang.Object
     **/
    AppLoginDTO appLogin(AppLoginForm form);

    /**
     * 用户详情
     * @param form
     * @return
     */
    UserInfoDTO getUserInfo(GetUserInfoForm form);

    /**
     * 登出
      * @param form
     */
    void logout(AppLogoutForm form);

    /**
     * 修改密码
     * @param form
     * @return
     */
    String updatePassword(UpdatePasswordForm form);

    /**
     * 修改用户信息
     * @param form
     */
    void modifyUserInfo(ModifyUserInfoForm form);

    /**
     * 修改手机号
     * @param form
     */
    void modifyPhone(ModifyPhoneForm form);

    /**
     * 用户头像上传
     * @param form
     * @return
     */
    String uploadUserPicUrl(UploadUserPicUrlForm form);

    HttpCommandResultWithData updateUserSignature(UpdateUserSignatureForm form);

    /**
     * @Author liuBing
     * @Description //TODO 根据id 查询ucId
     * @Date 16:37 2021/5/27
     * @Param [receiverId]
     * @return java.lang.String
     **/
    public String selectUcIdById(String userId);

    /**
     * 注销用户信息
     * @param form ucid
     * @return
     */
    void cancellation(AppLogoutForm form);

    /**
     * 拉黑用户并加入黑名单
     * @param form
     */
    void freezeUser(AppFreezeForm form);

    /**
     * 激活用户
     * @param form
     */
    void activateUser(AppFreezeForm form);
    /**
     * 拉黑用户并加入黑名单 ucId版
     * @param form
     */
    void freezeByUcId(AppFreezeTokenForm form);

    /**
     * 用户网红显示标识
     * @param form
     */
    StarSignDto starSign(StarSignForm form);
}

