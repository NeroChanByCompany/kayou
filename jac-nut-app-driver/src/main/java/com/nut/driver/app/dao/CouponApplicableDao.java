package com.nut.driver.app.dao;

import com.nut.driver.app.entity.CouponApplicableEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 适用车型或适用用户表
 *
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-18 15:50:56
 */
@Mapper
public interface CouponApplicableDao extends BaseMapper<CouponApplicableEntity> {

        List<CouponApplicableEntity> selectList(Map<String, Object> param);

        List<Map<String, Object>> queryOwnerCarInfo(@Param("userId") Long userId, @Param("DbName") String DbName);

}
