package com.nut.truckingteam.app.dao;

import com.nut.truckingteam.app.dto.CarDto;
import com.nut.truckingteam.app.dto.CarOilWearDto;
import com.nut.truckingteam.app.pojo.CarRolePojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/*
 *  @author wuhaotian 2021/7/10
 */
@Mapper
public interface TeamDao {

    List<CarDto> queryRankCarsByUserId(@Param("userId") Long userId);

    List<CarRolePojo> queryRankCarsByUserIdForMyCars(@Param("userId") Long userId);

    List<CarOilWearDto> queryCarsInfo(Map<String, Object> map);

}
