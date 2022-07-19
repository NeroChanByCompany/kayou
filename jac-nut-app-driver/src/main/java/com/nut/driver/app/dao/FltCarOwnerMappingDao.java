package com.nut.driver.app.dao;

import com.nut.driver.app.domain.FltCarOwnerMapping;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FltCarOwnerMappingDao {
    int deleteByPrimaryKey(Long id);

    int insert(FltCarOwnerMapping record);

    int insertSelective(FltCarOwnerMapping record);

    FltCarOwnerMapping selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FltCarOwnerMapping record);

    int updateByPrimaryKey(FltCarOwnerMapping record);

    /* ----------------自定义sql由此向下---------------- */

    FltCarOwnerMapping selectByCarIdAndUserId(@Param("carId") String carId, @Param("userId") Long userId);

    List<FltCarOwnerMapping> selectByCarId(@Param("carId") String carId);

    /**
     * 查询作为车主的所有车辆ID
     * @param userId
     * @return
     */
    List<String> queryOwnerCars(Long userId);
}