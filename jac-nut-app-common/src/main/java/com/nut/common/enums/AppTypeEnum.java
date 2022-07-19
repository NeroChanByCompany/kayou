package com.nut.common.enums;

/**
 * @author liuBing
 * @Classname AppTypeEnum
 * @Description TODO 用户类型
 * @Date 2021/8/20 11:38
 */
public enum AppTypeEnum {
    /**
     * 用户
     */
    USER(0, "用户"),
    /**
     * 车队
     */
    FLEET(1, "车队"),
    /**
     * 服务
     */
    SERVICE(2,"服务");

    private Integer code;
    private String message;

    AppTypeEnum(Integer code, String message) {
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
}
