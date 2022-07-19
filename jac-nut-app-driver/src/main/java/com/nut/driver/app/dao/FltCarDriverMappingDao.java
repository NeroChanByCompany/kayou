package com.nut.driver.app.dao;

import com.nut.driver.app.domain.FltCarDriverBindRecord;
import com.nut.driver.app.domain.FltCarDriverMapping;
import com.nut.driver.app.pojo.CarInfoPojo;
import com.nut.driver.app.pojo.UserInfoPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FltCarDriverMappingDao {
    int deleteByPrimaryKey(Long id);

    int insert(FltCarDriverMapping record);

    int insertSelective(FltCarDriverMapping record);

    FltCarDriverMapping selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FltCarDriverMapping record);

    int updateByPrimaryKey(FltCarDriverMapping record);

    /* ----------------自定义sql由此向下---------------- */

    /**
     * 根据车队ID和用户ID查询车辆
     *
     * @param teamId 车队ID
     * @param userId 用户ID
     * @return 车信息
     */
    List<CarInfoPojo> selectByTeamIdAndUserId(@Param("teamId") Long teamId, @Param("userId") Long userId);

    /**
     * 根据车队ID和用户ID删除
     *
     * @param teamId 车队ID
     * @param userId 用户ID
     * @return 删除条数
     */
    int deleteByTeamIdAndUserId(@Param("teamId") Long teamId, @Param("userId") Long userId);

    /**
     * 根据车队ID、车辆ID、用户ID查询
     *
     * @param teamId 车队ID
     * @param carId  车辆ID
     * @param userId 用户ID
     * @return 绑定关系
     */
    FltCarDriverMapping selectByTeamCarUser(@Param("teamId") Long teamId, @Param("carId") String carId, @Param("userId") Long userId);

    /**
     * 根据车队ID和车辆ID查询司机
     *
     * @param teamId 车队ID
     * @param carId  车辆ID
     * @return 司机信息
     */
    List<UserInfoPojo> selectByTeamIdAndCarId(@Param("teamId") Long teamId, @Param("carId") String carId);

    /**
     * 根据车队ID和车辆ID删除
     *
     * @param teamId 车队ID
     * @param carId  车辆ID
     * @return 删除条数
     */
    int deleteByTeamIdAndCarId(@Param("teamId") Long teamId, @Param("carId") String carId);

    /**
     * 根据车队ID查询
     *
     * @param teamId 车队ID
     * @return 绑定关系
     */
    List<FltCarDriverMapping> selectByTeamId(Long teamId);

    /**
     * 根据主键批量删除
     *
     * @param ids 主键列表
     * @return 删除条数
     */
    int batchDelete(List<Long> ids);

    List<FltCarDriverBindRecord> selectFltCarDriverRecordByCarId(String carId);

    int insertFltCarDriverRecord(FltCarDriverBindRecord record);
}