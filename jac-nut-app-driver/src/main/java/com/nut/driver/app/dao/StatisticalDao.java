package com.nut.driver.app.dao;

import com.nut.driver.app.pojo.FleetInfoPojo;
import com.nut.driver.app.pojo.FltStatisticalFleetDataPojo;
import com.nut.driver.app.pojo.FltStatisticalFleetPojo;
import com.nut.driver.app.pojo.FltStatisticalPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface StatisticalDao {
    /**
     * (统计)查询用户所在车队下车辆数据
     *
     * @param startTime
     * @param endTime
     * @param fleetIds
     * @return
     */
    List<FltStatisticalPojo> queryStatisticalByFleetIds(@Param("startTime") Date startTime,
                                                        @Param("endTime") Date endTime,
                                                        @Param("fleetIds") List<Long> fleetIds);

    /**
     * (统计)查询用作为车主的车辆数据
     *
     * @param startTime
     * @param endTime
     * @param carIds
     * @return
     */
    List<FltStatisticalPojo> queryOwnerCarsStatistical(@Param("startTime") Date startTime,
                                                       @Param("endTime") Date endTime,
                                                       @Param("carIds") List<String> carIds);


    /**
     * (统计)查询用作为车主的车辆数据
     *
     * @param startTime
     * @param endTime
     * @param carIds
     * @return
     */
    List<FltStatisticalPojo> querLineOwnerCarsStatistical(@Param("startTime") Date startTime,
                                                       @Param("endTime") Date endTime,
                                                       @Param("carIds") List<String> carIds);


    List<FltStatisticalFleetPojo> queryFleetAndCars(@Param("userId") Long userId,
                                                    @Param("teamName") String teamName);

    List<FltStatisticalFleetPojo> queryFleetAndCarsByTeamId(@Param("teamId") String teamId,
                                                    @Param("teamName") String teamName);

    List<FltStatisticalFleetDataPojo> queryStatisticalByTeamId(@Param("startTime") Date startTime,
                                                               @Param("endTime") Date endTime,
                                                               @Param("fleetIds") List<Long> fleetIds);

    /**
     * 查询车队创建者名称
     * @param fleetIds
     * @return
     */
    List<FltStatisticalFleetPojo> queryFleetCreatorByTeamId(@Param("fleetIds") List<Long> fleetIds);

    /**
     * 查询车队名称
     * @param fleetIds
     * @return
     */
    List<FleetInfoPojo> queryTeamName(@Param("fleetIds") List<Long> fleetIds);

}
