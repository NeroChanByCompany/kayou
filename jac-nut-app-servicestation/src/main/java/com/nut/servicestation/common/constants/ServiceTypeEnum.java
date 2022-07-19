package com.nut.servicestation.common.constants;

/**
 * 服务类型枚举(data_dict:A081)
 * 注意：编码值不能与 {@link com.nut.servicestation.common.constants.ChargeTypeEnum} 重复
 */
public enum ServiceTypeEnum {

    SERVICE_TYPE_ZI_FEI(8, "首保"),
    SERVICE_TYPE_ZOU_BAO(2, "定保"),
    SERVICE_TYPE_LI_BAO(3, "保外保养"),
    SERVICE_TYPE_BAO_XIU(4, "售前索赔"),
    SERVICE_TYPE_SUO_PEI(5, "保内索赔"),
    SERVICE_TYPE_SAN_BAO(6, "配件三包"),
    SERVICE_TYPE_WEI_XIU(7, "保外维修");



    private int code;
    private String message;

    ServiceTypeEnum(final int code, final String message) {
        this.code = code;
        this.message = message;
    }

    public static String getMessage(int code) {
        for (com.nut.servicestation.common.constants.ServiceTypeEnum refuseOrCloseEnum : com.nut.servicestation.common.constants.ServiceTypeEnum.values()) {
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
