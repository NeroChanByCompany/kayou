package com.nut.servicestation.app.dao;

import com.nut.servicestation.app.domain.MeterMileageWo;
import org.apache.ibatis.annotations.Mapper;

/*
 *  @author wuhaotian 2021/7/5
 */
@Mapper
public interface MeterMileageWoDao {

    MeterMileageWo queryByWoCode(String woCode);

    int updateByPrimaryKeySelective(MeterMileageWo record);

    int insertSelective(MeterMileageWo record);
}
