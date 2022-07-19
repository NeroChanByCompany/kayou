package com.nut.common.constant;

/**
 * @Description: APP枚举类型
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.common.constant
 * @Author: yzl
 * @CreateTime: 2021-06-15 11:32
 * @Version: 1.0
 */
public enum AppTypeEnum {
    /**
     * APP类型
     */
    APP_C(1, "司机APP"),
    TBOSS(3, "TBOSS"),
    TJH(5, "TBOSS"),
    INVITE(6, "邀请注册");

    private int code;
    private String message;

    AppTypeEnum(final int code, final String message) {
        this.code = code;
        this.message = message;
    }

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }
}
