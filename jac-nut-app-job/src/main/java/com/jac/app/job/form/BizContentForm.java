package com.jac.app.job.form;

import lombok.Data;

/**
 * @author MengJinyue
 * @create 2021/4/16
 * @Describtion
 */
@Data
public class BizContentForm {
    /**
     * 支付宝/微信
     */
    private String userId;
    private String tradeState;//交易状态（ -订单已关闭 D-交易已撤销 P-交易在进行 F-交易失败 S-交易成功 R-转入退款）/ 退款状态（P 预退款完成 F 失败 S 退款成功）
    private String currencyCode;//交易币种: 默认156，目前只支持人民币（156）
    private String txnTime;//订单生成时间 / 退款订单生成时间：格式为YYYYMMDDhhmmss
    private String endDate;//订单完成日期
    private String endTime;//订单完成时间

    /**
     * 微信/支付宝支付成功查询结果
     */
    private String merId;//商户号
    private String orderId;//商户订单号
    private String openId;//用户标识
    private String payBank;
    private String cmbOrderId;//招行生成的订单号
    private String dscAmt;//优惠金额
    private String payType;//支付方式：ZF：支付宝 WX：微信 YL：银联
    private String thirdOrderId;
    private String txnAmt;//交易金额

    /**
     * 微信/支付宝关闭订单结果
     */
    private String closeState;//关闭订单状态

    /**
     * 微信/支付宝退款成功查询结果
     */
    private String refundAmt;//退款金额，单位为分
    private String refundDscAmt;//优惠金额，单位为分
    private String refundServiceFee;//服务费退款金额，微信支付宝单位为分，一网通单位为元
    private String mchReserved;//商户保留域（若退款交易有上送此字段，则返回此字段）
    private String refundState;//退款处理状态(P 退款正在处理（原交易为微信渠道）S 退款成功（原交易为支付宝、银联渠道）)

    /**
     * 一网通
     */
    private String dateTime;//返回时间（格式：yyyyMMddHHmmss）
    private String merchantNo;//商户号
    private String date;//商户订单日期
    private String orderNo;//商户订单号
    private String bankSerialNo;//银行的订单流水号
    private String orderStatus;//支付状态（020：已立即结帐；029：立即结账结果未知）/ 退款状态（210-已直接退款 240-已授权退款）
    private String currency;//交易币种，固定为：“10”
    private String fee;//费用金额 单位是元
    private String bankTime;//支付-银行受理时间 / 退款-银行处理时间 格式：HHmmss
    private String settleAmount;//结算金额
    private String discountAmount;//优惠金额
    private String billAmount;

    /**
     * 一网通支付成功查询结果
     */
    private String amount;//订单金额
    private String bankDate;//银行受理日期 格式：yyyyMMdd
    private String settleTime;//银行处理时间 格式：HHmmss
    private String cardType;//
    private String settleDate;//银行处理日期 格式：yyyyMMdd

    /**
     * 一网通退款成功查询结果
     */
    private String refundAmount;//退款金额
    private String acceptDate;//银行受理日期
    private String acceptTime;//银行受理时间
    private String description;//退款描述
    private String uniqueUserId;//支付用户标识
}
