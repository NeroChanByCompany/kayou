package com.nut.driver.common.em;

/**
 * @description: 字典
 * @author: MengJinyue
 * @createTime: 2021/4/23 10:51
 * @version:1.0
 */
public enum DictEnum {


    /************************** 套餐状态 **************************/
    PACKAGE_STATUS_START ( "100201","套餐状态（启用）"),
    PACKAGE_STATUS_ON( "100202","套餐状态（上架）"),
    PACKAGE_STATUS_OFF( "100203","套餐状态（下架）"),
    PACKAGE_STATUS_DISABLE( "100204","套餐状态（禁用）"),

    /************************** 套餐类型 **************************/
    PACKAGE_TYPE_FIXED_FUEL ( "100401","套餐类型（加油包）"),
    PACKAGE_TYPE_FIXED_TOTAL ( "100402","套餐类型（总量服务包）"),
    PACKAGE_TYPE_FIXED_MONTH ( "100403","套餐类型（分月服务包）"),

    /************************** 订单状态 **************************/
    ORDER_STATUS_WAIT_PAY ( "2000","订单状态（待付款）"),
    ORDER_STATUS_WAIT_RECEIVE ( "2001","订单状态（运营商处理中）"),
    ORDER_STATUS_WAIT_COMPLETE ( "2002","订单状态（已完成）"),
    ORDER_STATUS_CANCEL ( "2003","订单状态（已取消（未付款））"),

    /************************** 支付方式 **************************/
    PAY_WAY_WECHAT_APP ( "2100","支付方式（微信APP）"),
    PAY_WAY_WECHAT_OFFICIAL ( "2101","支付方式（微信公众号）"),
    PAY_WAY_WECHAT_MINI ( "2102","支付方式（微信小程序）"),
    PAY_WAY_ALI ( "2103","支付方式（支付宝）"),
    PAY_WAY_NET ( "2104","支付方式（一网通）");

    private String code;
    private String message;

    DictEnum(String code, String message) {
        this.code =code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
