package com.nut.driver.common.constants;

/**
 * @author liuBing
 * @Classname RedisConstant
 * @Description TODO redis操作的一些常量
 * @Date 2021/8/24 10:52
 */
public class RedisConstant {

    /**
     * 验证码有效期
     */
    public static final String SMS_CODE_VALID_PERIOD = "SMS_VERIFICATION_CODE";
    /**
     * 验证码发送时间限制
     */
    public static final String SMS_CODE_TIME = "SMS_PHONE_TIME_CODE";
    /**
     * 验证码发送次数限制
     */
    public static final String SMS_CODE_FREQUENCY="SMS_FREQUENCY_TIME_CODE";
    /**
     * 验证码有效次数限制
     */
    public static final String SMS_CODE_CHECK="SMS_PHONE_CHECK_CODE";
    /**
     * 简单限流 没隔十秒钟允许访问一次
     */
    public static final String INTERFACE_ACCESS_RESTRICTION="INTERFACE_ACCESS_CHECK_CODE";

}
