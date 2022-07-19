package com.nut.common.constant;

/**
 * @Description: 优惠券常量
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.common.constant
 * @Author: yzl
 * @CreateTime: 2021-06-18 15:37
 * @Version: 1.0
 */

public class CouponVal {

    /**
     * 优惠券类型,1:线下优惠劵 2:购车代金券 3:线上代金券 4:满减券 5:拼团活动优惠券
     */
    public static final String TYPE_OFFLINE = "1";
    public static final String TYPE_BUY_VEHICLE = "2";
    public static final String TYPE_ONLINE = "3";
    public static final String TYPE_FULLREDUCTION = "4";
    public static final String TYPE_GROUP = "5";

    /**
     * 优惠券状态，1:可用 2:停发 3:停兑 4:停发停兑
     */
    public static final String INFO_STATUS_AVAILABLE = "1";
    public static final String INFO_STATUS_STOPGIVE = "2";
    public static final String INFO_STATUS_STOPEXCHANGE = "3";
    public static final String INFO_STATUS_STOP_GIVE_AND_EXCHANGE = "4";

    /**
     * 适用类型，1:车系设置 2:指定底盘号 3:指定车型号 4:指定手机号 5:指定终端类型 6:指定手机号（仅限购车代金券）8:首保券
     *
     */
    public static final String APPLICABLE_TYPE_SERIES = "1";
    public static final String APPLICABLE_TYPE_VIN = "2";
    public static final String APPLICABLE_TYPE_MODEL = "3";
    public static final String APPLICABLE_TYPE_PHONE = "4";
    public static final String APPLICABLE_TYPE_TER = "5";
    public static final String APPLICABLE_TYPE_PHONE_FOR_BUY = "6";
    public static final String APPLICABLE_TYPE_MAINTENANCE = "8";

    /**
     * 发放类型，1：网点发放  2：非网点发放
     */
    public static final String GIVE_TYPE_BRANCH = "1";
    public static final String GIVE_TYPE_NOBRANCH = "2";

    /**
     * 兑换类型，1：同发放网点一致， 2：定义兑换网点
     */
    public static final String EXCHANGE_TYPE_SAMEGIVE = "1";
    public static final String EXCHANGE_TYPE_CUSTOM = "2";

    /**
     * 车系设置，不限
     */
    public static final String SERIES_UNLIMITED = "-1";

    /**
     * 兑换商品类型，1：所有商品， 2：特定商品
     */
    public static final String PRODUCT_TYPE_ALL = "1";
    public static final String PRODUCT_TYPE_SOME = "2";

    /**
     * 积分购车，积分状态状态1：未使用2已使用3已取消
     */
    public static final String SOURCE_NOT_USE = "1";
    public static final String SOURCE_OF_USE = "2";
    public static final String SOURCE_IS_CANCEL = "3";

}
