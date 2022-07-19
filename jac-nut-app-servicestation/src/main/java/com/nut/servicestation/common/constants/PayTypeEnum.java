package com.nut.servicestation.common.constants;

/**
 * 付费方式枚举(data_dict:A071)
 * 注意：编码值不能与 {@link DealTypeEnum} 重复
 */
public enum PayTypeEnum {

    /** 厂家付费 */
    PAY_BY_FACTORY(1, "厂家付费"),
    /** 用户自费 */
    PAY_BY_USER(2, "用户自费");

    private int code;
    private String message;

    PayTypeEnum(final int code, final String message) {
        this.code = code;
        this.message = message;
    }

    public static String getMessage(int code) {
        for (com.nut.servicestation.common.constants.PayTypeEnum refuseOrCloseEnum : com.nut.servicestation.common.constants.PayTypeEnum.values()) {
            if (refuseOrCloseEnum.code() == code) {
                return refuseOrCloseEnum.message;
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
