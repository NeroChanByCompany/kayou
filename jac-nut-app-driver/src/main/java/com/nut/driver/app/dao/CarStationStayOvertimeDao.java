package com.nut.driver.app.dao;

import com.nut.driver.app.entity.CarStationStayOvertimeEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 进站超时预警表
 *
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-22 11:00:52
 */
@Mapper
public interface CarStationStayOvertimeDao extends BaseMapper<CarStationStayOvertimeEntity> {

    /**
     * 查询最新预警信息
     * @param carId
     *          车辆ID
     * @param staCode
     *          服务站编码
     * @return  查询结果
     *
     */
    CarStationStayOvertimeEntity queryWarningData(@Param("carId") String carId, @Param("staCode") String staCode);

    int updateByPrimaryKeySelective(CarStationStayOvertimeEntity record);

}
