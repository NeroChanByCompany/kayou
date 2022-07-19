package com.nut.common.enums;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.common.enums
 * @Author: yzl
 * @CreateTime: 2021-06-16 10:07
 * @Version: 1.0
 */
public enum LoginCheckEnum {

    // 司机,车主登录校验
    APP(1),
    // TBOSS登录校验
    OPERATE(2),
    // 服务APP登录校验
    SERVICE_STATION(3),
    /**
     * 模块为单独redis时，委托operate的接口做登录校验
     */
    OPERATE_DELEGATE(4),
    DEBUG(5),
    /**
     * 调用方为寰游
     */
    TSP(6),
    /**
     * 调用方为东浦CRM
     */
    DF_CRM(7),
    // 产销登录校验
    PRODUCT(8);

    private final int code;

    /**
     * 构造函数，枚举类型只能为私有
     */
    LoginCheckEnum(int nCode) {
        this.code = nCode;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String toString() {
        return String.valueOf(this.code);
    }
}
