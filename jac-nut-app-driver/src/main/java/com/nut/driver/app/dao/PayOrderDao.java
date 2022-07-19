package com.nut.driver.app.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nut.driver.app.entity.PayOrderEntity;
import com.nut.driver.app.vo.PayOrderVo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付订单
 * 
 * @author MengJinyue
 * @email mengjinyue@esvtek.com
 * @date 2021-04-12 14:01:07
 */
@Mapper
public interface PayOrderDao extends BaseMapper<PayOrderEntity> {
    /**
     * 根据支付订单编号获取支付单信息（cmb支付用）
     * @param payOrderNumber 支付订单编号
     * @return
     */
    PayOrderVo getPayOrder(String payOrderNumber);
}
