package com.nut.servicestation.common.constants;

/**
 * 处理方式枚举(data_dict:A075)
 * 注意：编码值不能与 {@link com.nut.servicestation.common.constants.PayTypeEnum} 重复
 */
public enum DealTypeEnum {

    /** 修复处理 */
    DEAL_DIRECT(11, "修复处理"),
    /** 换件维修 */
    DEAL_CHANGE(12, "换件维修");

    private int code;
    private String message;

    DealTypeEnum(final int code, final String message) {
        this.code = code;
        this.message = message;
    }

    public static String getMessage(int code) {
        for (com.nut.servicestation.common.constants.DealTypeEnum refuseOrCloseEnum : com.nut.servicestation.common.constants.DealTypeEnum.values()) {
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
