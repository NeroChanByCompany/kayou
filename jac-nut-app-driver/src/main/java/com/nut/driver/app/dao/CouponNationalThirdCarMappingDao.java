package com.nut.driver.app.dao;

import com.nut.driver.app.entity.CouponNationalThirdCarMappingEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nut.driver.app.pojo.CouponNationalThirdCarInfoPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 国三优惠券绑定国三车关系表
 *
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-21 10:11:15
 */
@Mapper
public interface CouponNationalThirdCarMappingDao extends BaseMapper<CouponNationalThirdCarMappingEntity> {

    /**
     * @Description TODO 查询国三券绑定的国三车信息
     */
    CouponNationalThirdCarInfoPojo getNationalThirdCarToCoupon(@Param("userId") String autoIncreaseId, @Param("cumId") String cumId);
}
