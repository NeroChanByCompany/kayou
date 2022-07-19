package com.nut.driver.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * @author MengJinyue
 * @description pay
 */
@Component
@Slf4j
public class CMBConfig {

    @Value("${cmb.baseUrl}")
    public String OPEN_API;

    //入参
    public static final String version_1 = "1.0";
    public static final String version_0_0_1 = "0.0.1";
    public static final String encoding = "UTF-8";
    public static final String signMethod_01 = "01";//RSA加签
    public static final String signMethod_02 = "02";//国密加签

    //出参
    /**一网通支付状态（029 - 交易在进行 020 - 交易成功）*/
    public static final String NET_PAY_STATUS_ONGING = "029";
    public static final String NET_PAY_STATUS_SUCCESS = "020";

    /**一网通退款状态（210-已直接退款 240-已授权退款）*/
    public static final String NET_REFUND_STATUS_ONGING = "240";
    public static final String NET_REFUND_STATUS_SUCCESS = "210";

    /**支付宝/微信支付状态（C-订单已关闭 D-交易已撤销 P-交易在进行 F-交易失败 S-交易成功 R-转入退款）*/
    public static final String ALIPAY_PAY_STATUS_ONGING = "P";
    public static final String ALIPAY_PAY_STATUS_SUCCESS = "S";

    /**支付宝/微信退款状态（ P 预退款完成 F 失败 S退款成功）*/
    public static final String ALIPAY_REFUND_STATUS_SUCCESS = "S";
    public static final String ALIPAY_REFUND_STATUS_ONGING = "P";
    public static final String ALIPAY_REFUND_STATUS_FAIL = "F";

    //appid
    @Value("${cmb.appId}")
    public  String appId ;
    //appSecret
    @Value("${cmb.appSecret}")
    public  String appSecret ;
    //商户私钥（国密SM2）
    @Value("${cmb.sm2_private_key}")
    public  String SM2_PRIVATE_KEY ;
    //招行公钥（国密SM2）
    @Value("${cmb.sm2_cmb_public_key}")
    public  String SM2_CMB_PUBLIC_KEY ;
    //回调地址
    @Value("${cmb.notify_url_netpay}")
    public  String NOTIFY_URL_NETPAY;
    //回调地址
    @Value("${cmb.notify_url_alipay}")
    public  String NOTIFY_URL_ALIPAY ;
    //回调地址（微信支付）
    @Value("${cmb.notify_url_wechat}")
    public  String NOTIFY_URL_WECHAT ;
    //回调地址（支付宝/微信退款）
    @Value("${cmb.refund_notify_url}")
    public  String REFUND_NOTIFY_URL ;
    //商户号
    @Value("${cmb.mer_id}")
    public  String MER_ID ;
    //收银员号
    @Value("${cmb.user_id}")
    public  String USER_ID ;
    //子商户号
    @Value("${cmb.sub_mer_id}")
    public  String SUB_MER_ID ;
    //子门店号
    @Value("${cmb.sub_store_id}")
    public  String SUB_STORE_ID ;

    //微信开放平台appid
    @Value("${cmb.wechat_appid}")
    public String WECHAT_APPID;
    //商户号
    @Value("${cmb.wechat_mer_id}")
    public  String WECHAT_MER_ID ;
    //收银员号
    @Value("${cmb.wechat_user_id}")
    public  String WECHAT_USER_ID ;
    //子商户号
    @Value("${cmb.wechat_sub_mer_id}")
    public  String WECHAT_SUB_MER_ID ;
    //子门店号
    @Value("${cmb.wechat_sub_store_id}")
    public  String WECHAT_SUB_STORE_ID ;


}

