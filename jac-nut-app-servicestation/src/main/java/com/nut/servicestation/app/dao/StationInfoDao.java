package com.nut.servicestation.app.dao;

import com.nut.servicestation.app.domain.AddWoStationInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/*
 *  @author wuhaotian 2021/7/8
 */
@Mapper
public interface StationInfoDao {

    AddWoStationInfo getInfo(@Param("hyDbName") String hyDbName, @Param("id") Long id);

    int updatePic(@Param("hyDbName") String hyDbName, @Param("id") Long id, @Param("pic") String pic);
}
