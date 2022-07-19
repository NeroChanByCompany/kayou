package com.nut.servicestation.app.dao;

import com.nut.servicestation.app.domain.CarStationStayOvertime;
import com.nut.servicestation.app.pojo.CarWarnPojo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/*
 *  @author wuhaotian 2021/7/5
 */
public interface CarStationStayOvertimeDao {

    /**
     * 查询最新预警信息
     * @param carId
     *          车辆ID
     * @param staCode
     *          服务站编码
     * @return  查询结果
     *
     */
    CarStationStayOvertime queryWarningData(@Param("carId") String carId, @Param("staCode") String staCode);

    int updateByPrimaryKeySelective(CarStationStayOvertime record);

    /**
     * 查询服务站进站预警数量
     * @param staCode
     *          服务站编码
     * @return  查询结果
     */
    Long queryInStationWarningCount(String staCode);
    /**
     * 查询进站预警车辆信息
     * @param staCode
     *          服务站编码
     * @return  查询结果
     */
    List<CarWarnPojo> queryStationStayWarnCar(String staCode);
}
