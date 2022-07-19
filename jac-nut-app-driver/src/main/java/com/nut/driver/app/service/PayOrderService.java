package com.nut.driver.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.driver.app.entity.PayOrderEntity;
import com.nut.driver.app.form.BizContentForm;
import com.nut.driver.app.form.PayForm;

/**
 * 支付订单
 *
 * @author MengJinyue
 * @email mengjinyue@esvtek.com
 * @date 2021-04-12 14:01:07
 */
public interface PayOrderService extends IService<PayOrderEntity> {
    /**
     * 预支付（手机端弹出支付窗口）
     * @param payForm 订单号等信息
     * @return
     */
    HttpCommandResultWithData toPay(PayForm payForm);

    /**
     * 支付结果查询
     * @param payOrderNumber 订单编号
     * @return
     */
    HttpCommandResultWithData searchPaySuccessFlag(String payOrderNumber);

    /**
     * 支付后回调
     * @return
     */
    HttpCommandResultWithData payCallback(String payWay, BizContentForm bizContentForm);

    /**
     * 退款后回调
     * @return
     */
    HttpCommandResultWithData refundCallback(BizContentForm bizContentForm);
}

