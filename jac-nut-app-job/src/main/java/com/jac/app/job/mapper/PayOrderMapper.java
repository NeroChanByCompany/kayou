package com.jac.app.job.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jac.app.job.entity.PayOrderEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付订单
 * 
 * @author MengJinyue
 * @email mengjinyue@esvtek.com
 * @date 2021-04-12 14:01:07
 */
@Mapper
public interface PayOrderMapper extends BaseMapper<PayOrderEntity> {
}
