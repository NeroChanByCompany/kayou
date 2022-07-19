package com.nut.driver.app.dao;

import com.nut.driver.app.domain.CouponNewRegPhone;
import com.nut.driver.app.domain.CouponUserMapping;
import com.nut.driver.app.entity.CouponUserMappingEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 优惠券与用户关系表
 *
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-18 15:50:56
 */
@Mapper
public interface CouponUserMappingDao extends BaseMapper<CouponUserMappingEntity> {

    /**
     * 查询新注册用户的优惠券
     * @param param
     * @return
     */
    List<CouponNewRegPhone> queryBindNewRegPhoneCoupon(Map<String, Object> param);

    /**
     * 更新优惠券绑定用户
     * @param param
     * @return
     */
    int updateBindNewRegPhoneCoupon(Map<String, Object> param);

    int selectCarBind(@Param("vin") String carVin);

    int insertIntoCouponCarBind(Map<String, Object> param);

    int insertSelective(CouponUserMapping record);

    /**
     * 根据优惠券规则ID来查询指定优惠券规则的发放数量
     * @param infoIds 优惠券规则ID
     * @return 该规则的兑换数量
     */
    List<Map<String, Long>> countByInfoIdInt(@Param("infoIds") List<Long> infoIds);

}
