package com.nut.tripanalysis.app.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/*
 *  @author wuhaotian 2021/7/10
 */
@Mapper
public interface CarAnalyseDao {

    Double getAvgOilForRanking(@Param("carId") String carId, @Param("beginTime") Long beginTime, @Param("endTime") Long endTime);

}
