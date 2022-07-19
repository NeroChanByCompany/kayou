package com.nut.common.utils;

/**
 * @Description: 常量工具类
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.common.utils
 * @Author: yzl
 * @CreateTime: 2021-06-15 10:17
 * @Version: 1.0
 */

public class Constants {

    // 用户注册
    public static final int REGISTER_TIMEOUT = 30;

    // 快捷登录接口事物回滚时间
    public static final int QUICKLOGIN_TIMEOUT = 5;

    // 修改用户信息接口事物回滚时间
    public static final int MODIFYUSER_TIMEOUT = 5;

    // APP端登录接口事物回滚时间
    public static final int APPLOGIN_TIMEOUT = 5;

    // APP端退出登录接口事物回滚时间
    public static final int LOGOUT_TIMEOUT = 5;
    // 绑定手机号接口事物回滚时间
    public static final int BINDMOBILE_TIMEOUT = 5;

    //司机app当前车队车辆接口事物回滚时间
    public static final int UPDPASSWORD_TIMEOUT = 5;

    //appType 车队版
    @Deprecated
    public static final String APPTYPE_FLEET = "1";

    //appType 司机版
    @Deprecated
    public static final String APPTYPE_DRIVER = "2";

    //appType 车队web版
    @Deprecated
    public static final String APPTYPE_WEB = "3";

    public static final int LOGIN_ERROR_NUM = 3;

    public static final String KAPTCHA_REDIS_KEY = "KAPTCHA_REDIS_KEY";

    public static final String LOGIN_ERROR_NUM_KEY = "LOGIN_ERROR_NUM_KEY";

    /**
     * 自助注册用户
     */
    public static final int OWN_REGISTER = 1;
    /**
     * tboss创建
     */
    public static final int TBOSS_CREATER = 3;
    /**
     * 推介会用户
     */
    public static final int TUIJIEHUI_REGISTER = 5;

    /**
     * 积分弹出框标题
     */
    public static final String SCORE_POPUP_TITLE = "恭喜您已获得注册积分！";
    /**
     * 积分弹出框内容
     */
    public static final String SCORE_POPUP_CONTENT = "您已完成注册获得20积分积分在积分商城可以兑换精美礼品哦~";

    /**
     * 参数签名使用
     */
    public static final String FIELD_SIGN = "sign";

    /**
     * 邀请用户
     */
    public static final int INVITE_REGISTER = 6;

    /**
     * 邀请注册判断手机号是否已注册
     */
    public static final String PHONE_IS_REGISTERED = "该手机号已注册！";

    /**
     * 邀请注册判成功
     */
    public static final String INVITE_REGISTERED_SUCCESS = "注册成功，快下载APP来体验吧~";

    /**
     * 需要新手指引
     */
    public static final int NEED_NOVICE_TAG = 1;

    /**
     * 邀请注册用户首次登录返回积分商城code
     */
    public static final String INVITE_REGISTER_CODE = "2";

    @Deprecated
    public class MessageCode {
        public static final int MESSAGE_CODE_GOOD_OPINION = 35;//货源认证审核
    }
}
