package com.nut.driver.app.domain;


import lombok.Data;

import java.util.Date;

@Data
public class IntegralConsumeInfo {
    private Long id;
    /**用户唯一性标识，唯一且不可变**/
    private String uid;
    /**本次兑换扣除的积分**/
    private String credits;
    /**自有商品商品编码(非必须字段)**/
    private String itemCode;
    /**接口appKey，应用的唯一标识**/
    private String appKey;
    /**1970-01-01开始的时间戳，毫秒为单位。**/
    private String timestamp;
    /**本次积分消耗的描述(带中文，请用utf-8进行url解码)**/
    private String description;
    /**兑吧订单号(请记录到数据库中)**/
    private String orderNum;
    /**开发者的订单号**/
    private String clwOrderNum;
    /**兑换类型：alipay(支付宝), qb(Q币), coupon(优惠券), object(实物), phonebill(话费), phoneflow(流量), virtual(虚拟商品),game(游戏), hdtool(活动抽奖),sign(签到)所有类型不区分大小写**/
    private String type;
    /**兑换商品的市场价值，单位是分，请自行转换单位**/
    private Long facePrice;
    /**此次兑换实际扣除开发者账户费用，单位为分**/
    private Long actualPrice;
    /**用户ip，不保证获取到**/
    private String ip;
    /**直冲商品Q币商品，QQ号码回传参数，其他商品不传该参数**/
    private String qq;
    /**直冲类话费商品手机号回传参数，非话费商品不传该参数**/
    private String phone;
    /**支付宝充值商品支付宝账号参数回传，非支付宝商品不传该参数**/
    private String alipay;
    /**是否需要审核(如需在自身系统进行审核处理，请记录下此信息)**/
    private Boolean waitAudit;
    /**用详情参数，不同的类型，请求时传不同的内容，中间用英文冒号分隔。(支付宝类型带中文，请用utf-8进行解码) 实物商品：返回收货信息(姓名:手机号:省份:城市:区域:街道:详细地址)、支付宝：返回账号信息(支付宝账号:实名)、话费：返回手机号、QB：返回QQ号**/
    private String params;
    /**MD5签名**/
    private String sign;
    private String isSuccess;
    private String errorMessage;
    private Date createTime;
    private Date updateTime;

    @Override
    public String toString() {
        return "IntegralConsumeInfo{" +
                "uid=" + uid +
                ", credits='" + credits + '\'' +
                ", itemCode='" + itemCode + '\'' +
                ", appKey='" + appKey + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", description='" + description + '\'' +
                ", orderNum='" + orderNum + '\'' +
                ", type='" + type + '\'' +
                ", facePrice='" + facePrice + '\'' +
                ", actualPrice='" + actualPrice + '\'' +
                ", ip='" + ip + '\'' +
                ", qq='" + qq + '\'' +
                ", phone='" + phone + '\'' +
                ", alipay='" + alipay + '\'' +
                ", waitAudit='" + waitAudit + '\'' +
                ", params='" + params + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
