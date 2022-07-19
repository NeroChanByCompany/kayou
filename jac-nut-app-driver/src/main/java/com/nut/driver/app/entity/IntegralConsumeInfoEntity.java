package com.nut.driver.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 积分消费信息
 *
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-15 19:38:03
 */
@Data
@TableName("integral_consume_info")
public class IntegralConsumeInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    @TableId
    private Long id;
    /**
     * 用户唯一性标识，唯一且不可变
     */
    private String uid;
    /**
     * 本次兑换扣除的积分
     */
    private String credits;
    /**
     * 自有商品商品编码
     */
    private String itemCode;
    /**
     * 自有商品商品编码(非必须字段)
     */
    private String appKey;
    /**
     * 接口appKey
     */
    private String timestamp;
    /**
     * 本次积分消耗的描述
     */
    private String description;
    /**
     * 兑吧订单号
     */
    private String orderNum;
    /**
     * 开发者的订单号
     */
    private String clwOrderNum;
    /**
     *
     */
    private String type;
    /**
     * 兑换商品的市场价值
     */
    private String facePrice;
    /**
     * 此次兑换实际扣除开发者账户费用，单位为分
     */
    private String actualPrice;
    /**
     *
     */
    private String ip;
    /**
     * 兑换类型
     */
    private String qq;
    /**
     *
     */
    private String phone;
    /**
     *
     */
    private String alipay;
    /**
     * 是否需要审核
     */
    private String waitAudit;
    /**
     *
     */
    private String params;
    /**
     * MD5签名
     */
    private String sign;
    /**
     * 兑换是否成功 0 还没确认 1 成功 2 失败
     */
    private String isSuccess;
    /**
     * 消费失败原因
     */
    private String errorMessage;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;

}
