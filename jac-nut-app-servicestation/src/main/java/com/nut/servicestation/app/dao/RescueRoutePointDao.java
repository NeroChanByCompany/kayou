package com.nut.servicestation.app.dao;

import com.nut.servicestation.app.domain.RescueRoutePoint;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/*
 *  @author wuhaotian 2021/7/5
 */
@Mapper
public interface RescueRoutePointDao {

    RescueRoutePoint selectByWoCode(@Param("woCode") String woCode);

    int updateByPrimaryKeySelective(RescueRoutePoint record);

    int insertSelective(RescueRoutePoint record);

    int deleteByPrimaryKey(Long id);
}
