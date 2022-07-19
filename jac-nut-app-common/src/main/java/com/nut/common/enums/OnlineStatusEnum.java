package com.nut.common.enums;

/**
 * @Description: 车辆在线状态枚举
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.common.enums
 * @Author: yzl
 * @CreateTime: 2021-06-17 13:29
 * @Version: 1.0
 */
public enum OnlineStatusEnum {

    /**
     * 车辆离线
     */
    OL_STS_OFFLINE(0, "车辆离线"),
    /**
     * 在线静止
     */
    OL_STS_STILL(1, "在线静止"),
    /**
     * 在线行驶
     */
    OL_STS_MOVING(2, "在线行驶"),
    /**
     * 车辆断连
     */
    OL_STS_ABSENT(3, "车辆断连");

    private int code;
    private String message;

    OnlineStatusEnum(final int code, final String message) {
        this.code = code;
        this.message = message;
    }

    public static String getMessage(int code) {
        for (OnlineStatusEnum e : OnlineStatusEnum.values()) {
            if (e.code() == code) {
                return e.message;
            }
        }
        return null;
    }

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }

}
