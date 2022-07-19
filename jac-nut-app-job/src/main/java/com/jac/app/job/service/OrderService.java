package com.jac.app.job.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jac.app.job.common.Result;
import com.jac.app.job.entity.OrderEntity;

/**
 * 优惠券
 *
 * @author MengJinyue
 * @email mengjinyue@esvtek.com
 * @date 2021-04-12 14:01:07
 */
public interface OrderService extends IService<OrderEntity> {
    /**
     * 定时查询是否支付成功
     * @return
     */
    Result searchPaySuccessFlag();

    /**
     * 定时远程提交套餐订单
     * @return
     */
    Result searchSimOrderFlag();
}

