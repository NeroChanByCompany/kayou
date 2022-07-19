package com.nut.driver.app.dao;

import com.nut.driver.app.entity.CouponProductEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券兑换商品
 * 
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-18 15:50:57
 */
@Mapper
public interface CouponProductDao extends BaseMapper<CouponProductEntity> {
	
}
