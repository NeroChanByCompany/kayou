package com.jac.app.job.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jac.app.job.entity.OrderEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author MengJinyue
 * @email mengjinyue@esvtek.com
 * @date 2021-04-12 14:01:08
 */
@Mapper
public interface OrderMapper extends BaseMapper<OrderEntity> {
}
