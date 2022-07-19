package com.nut.servicestation.common.constants;

/**
 * 费用类型枚举(data_dict:A082)
 * 注意：编码值不能与 {@link ServiceTypeEnum} 重复
 */
public enum ChargeTypeEnum {

    /** 保内 */
    CHARGE_TYPE_WARRANTY(11, "保内"),
    /** 保外 */
    CHARGE_TYPE_OVER_WARRANTY(12, "保外");

    private int code;
    private String message;

    ChargeTypeEnum(final int code, final String message) {
        this.code = code;
        this.message = message;
    }

    public static String getMessage(int code) {
        for (com.nut.servicestation.common.constants.ChargeTypeEnum refuseOrCloseEnum : com.nut.servicestation.common.constants.ChargeTypeEnum.values()) {
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
