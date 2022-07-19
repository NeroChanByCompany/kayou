package com.nut.driver.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.driver.app.entity.OrderEntity;
import com.nut.driver.app.form.InsertOrderForm;
import com.nut.driver.app.form.OrderForm;

import java.io.IOException;

/**
 * 订单
 *
 * @author MengJinyue
 * @email mengjinyue@esvtek.com
 * @date 2021-04-12 14:01:08
 */
public interface OrderService extends IService<OrderEntity> {
}

