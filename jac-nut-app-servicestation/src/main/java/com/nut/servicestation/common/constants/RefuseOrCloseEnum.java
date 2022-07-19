package com.nut.servicestation.common.constants;

/**
 * @author admin
 * @Description: 拒单/申请关闭类型枚举(A072)
 * on 2018/2/6.
 */
public enum RefuseOrCloseEnum {

    NO_PARTS (1, "缺配件"),
    NO_ABILITY (2, "无技术能力"),
    NO_RESOURCE (3, "无法安排人/车"),
    WITH_INSURANCE (4, "保内自行处理"),
    WITHOUT_INSURANCE (5, "保外另行处理"),
    OTHER_REASONS (6, "其它");

    private int code;
    private String message;

    RefuseOrCloseEnum(final int code, final String message) {
        this.code = code;
        this.message = message;
    }

    public static String getMessage(int code) {
        for (com.nut.servicestation.common.constants.RefuseOrCloseEnum refuseOrCloseEnum : com.nut.servicestation.common.constants.RefuseOrCloseEnum.values()) {
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
