package com.nut.common.enums;

/**
 * @author liuBing
 * @Classname UserListenerEnum
 * @Description TODO 用户动作监听 0 登录 1 登出 2 注销
 * @Date 2021/9/13 13:51
 */
public enum UserListenerEnum {

    /**
     * 登录
     */
    LOGIN(0, "登录"),
    /**
     * 登出
     */
    LOGOUT(1, "登出"),
    /**
     * 注销
     */
    CANCELLATION(2, "注销")
    ;

    private Integer code;
    private String message;

    UserListenerEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static UserListenerEnum getEnum(Integer code) {
        for (UserListenerEnum e : UserListenerEnum.values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }
}
