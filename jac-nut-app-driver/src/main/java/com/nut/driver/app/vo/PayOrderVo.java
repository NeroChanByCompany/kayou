package com.nut.driver.app.vo;

import com.nut.driver.app.entity.PayOrderEntity;
import lombok.Data;

import java.util.List;

/**
 * 支付订单
 *
 * @author MengJinyue
 * @email mengjinyue@esvtek.com
 * @date 2021-04-12 14:01:07
 */
@Data
public class PayOrderVo extends PayOrderEntity {

	/**
	 * 订单集合
	 */
	private List<OrderVo> orderList;
}
