package com.nut.servicestation.app.dao;

import com.nut.servicestation.app.domain.CouponUserMapping;
import org.apache.ibatis.annotations.Mapper;

/*
 *  @author wuhaotian 2021/7/7
 */
@Mapper
public interface CouponUserMappingDao {

    CouponUserMapping selectByPrimaryKey(Long cumId);

    int updateByPrimaryKeySelective(CouponUserMapping record);
}
