package com.nut.common.enums;

/**
 * @author liuBing
 * @Classname ActionCodeEnum
 * @Description TODO
 * @Date 2021/11/2 17:10
 */
public enum ActionCodeEnum {

    /**
     * 客户版本
     */
    KEHU_VERSION("kehu_version", "客户版本"),
    /**
     * 车队版本
     */
    FLEET_VERSION("fleet_version", "车队版本"),
    /**
     * 服务版本
     */
    SERVICE_VERSION("service_version","服务版本");

    private String code;
    private String message;

    ActionCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
