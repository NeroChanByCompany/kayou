package com.nut.common.enums;

/**
 * @author liuBing
 * @Classname LoginTypeEnum
 * @Description TODO
 * @Date 2021/9/14 10:24
 */
public enum LoginTypeEnum {

    /**
     * 密码
     */
    PASSWORD(0, "密码"),
    /**
     * 短信验证码
     */
    SMS_CODE(1, "短信验证码"),
    /**
     * 未知密码
     */
    UNDEFINED(2, "短信验证码"),

    ;

    private Integer code;
    private String message;

    LoginTypeEnum(Integer code, String message) {
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

    public static LoginTypeEnum getEnum(Integer code) {
        for (LoginTypeEnum e : LoginTypeEnum.values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }
}
