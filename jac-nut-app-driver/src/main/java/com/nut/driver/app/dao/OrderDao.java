package com.nut.driver.app.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nut.driver.app.entity.OrderEntity;
import com.nut.driver.app.form.OrderForm;
import com.nut.driver.app.vo.OrderVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 订单
 * 
 * @author MengJinyue
 * @email mengjinyue@esvtek.com
 * @date 2021-04-12 14:01:08
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
    /**
     * 查询订单列表
     * @param orderForm
     * @return
     */
    List<OrderVo> listOrder(OrderForm orderForm);
}
