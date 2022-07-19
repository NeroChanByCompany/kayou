package com.nut.driver.app.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.nut.driver.app.domain.FltFleetCarMapping;
import com.nut.driver.app.entity.FltFleetCarMappingEntity;
import com.nut.driver.app.pojo.CarDriverInfoCountPojo;
import com.nut.driver.app.pojo.CarInfoPojo;
import com.nut.driver.app.pojo.FleetInfoPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FltFleetCarMappingDao extends BaseMapper<FltFleetCarMapping> {
    int deleteByPrimaryKey(Long id);

    int insert(FltFleetCarMapping record);

    int insertSelective(FltFleetCarMapping record);

    FltFleetCarMapping selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FltFleetCarMapping record);

    int updateByPrimaryKey(FltFleetCarMapping record);

    /* ----------------自定义sql由此向下---------------- */

    /**
     * 根据车队ID和车辆ID查询
     *
     * @param teamId 车队ID
     * @param carId  车辆ID
     * @return 绑定关系
     */
    FltFleetCarMappingEntity selectByTeamIdAndCarId(@Param("teamId") Long teamId, @Param("carId") String carId);

    /**
     * 根据车队ID查询车辆信息
     *
     * @param teamId  车队ID
     * @param keyword 搜索关键字
     * @return 车辆信息
     */
    Page<CarInfoPojo> selectCarByTeamId(@Param("teamId") Long teamId, @Param("keyword") String keyword);

    /**
     * 以车队ID分组计数车辆
     *
     * @param list 车队ID集合
     * @return 计数结果
     */
    List<FleetInfoPojo> countCarsByTeamIdIn(List<Long> list);

    /**
     * 根据车队ID查询
     *
     * @param teamId 车队ID
     * @return 绑定关系
     */
    List<FltFleetCarMappingEntity> selectByTeamId(Long teamId);

    /**
     * 根据主键批量删除
     *
     * @param ids 主键列表
     * @return 删除条数
     */
    int batchDelete(List<Long> ids);

    /**
     * 以车辆ID分组计数车辆
     *
     * @param list 车辆ID列表
     * @return 计数结果
     */
    List<CarDriverInfoCountPojo> countByCarId(List<String> list);

    List<String> queryOwnerCars(@Param("userId") Long userId);
}


