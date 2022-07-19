package com.nut.driver.app.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.nut.driver.app.domain.Car;
import com.nut.driver.app.dto.CarParmDto;
import com.nut.driver.app.dto.TripCarListDto;
import com.nut.driver.app.entity.CarEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nut.driver.app.entity.ColdCarsEntity;
import com.nut.driver.app.form.QueryCarParmForm;
import com.nut.driver.app.pojo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 车辆表
 *
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-21 14:34:16
 */
@Mapper
@Repository
public interface CarDao extends BaseMapper<CarEntity> {

    /**
     * 根据用户ID查询，所有车队内，与此用户相关的车（创建人、管理员、绑定司机、车主）
     */
    List<CarRolePojo> selectUserRelatedCar(Long userId);

    /**
     * 根据车辆ID列表查询车辆信息
     */
    List<CarInfoPojo> selectByCarIdIn(List<String> list);

    UserInfoPojo queryCarVinAndPhone(@Param(value = "userId") String userId, @Param(value = "carId") String carId);

    /**
     * 根据carid查询车辆
     * @param carIdList
     * @return
     */
    List<String> queryCarVinListCarsByCarIds(List<String> carIdList);

    /**
     * 根据用户id查询
     * @param autoIncreaseId
     * @return
     */
    List<PhyExaPojo> queryPhyCarsByUserId(Long autoIncreaseId);

    /**
     * 根据车辆底盘号查询
     * @param carVin
     * @return
     */
    CarEntity selectByVin(String carVin);

    String queryAutoTerminalByCarId(String carId);

    String getVinByCarId(String carId);

    /**
     * 根据底盘号集合获取车辆集合
     * @param carVinSet
     * @return
     */
    List<Car> queryCarNumberByCarVinList(Set<String> carVinSet);

    /**
     * 根据carId来查询邦车日期
     */
    List<Date> findMappingDateByCarId(@Param("carId") String carId);

    CarEntity selectByChassisNumber(String chassisNumber);

    Car selectByChassisNumber2(String chassisNumber);

    CarEntity selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(CarEntity record);
    /**
     * 更新 tsp 的车牌号
     */
    int updateTspCarNumber(@Param("DbName") String DbName, @Param("chassisNum") String chassisNum, @Param("carNumber") String carNumber);

    int updateExtInfoOk(@Param("carId") Long carId);

    int insertCarBinding(Map map);

    int queryCarBinding(String carVin);

    /**
     * 获取在活动时间前销售的车辆数
     */
    int carOfNumber(Map<String ,Object> param);

    Long getUserCarNum(Long userId);

    /**
     * (统计用)根据车辆ID列表查询车辆信息
     * @param list
     * @return
     */
    List<FltStatisticalPojo> queryUserRelatedCarsByCarIds(List<String> list);
    /**
     * 用户所在车队的所有车辆ID
     * @param userId
     * @return
     */
    List<String> queryAllCarIdByUserId(Long userId);

    /**
    * @Description：${todo}
    * @author YZL
    * @data 2021/6/29 10:36
    */
    String findBrandNameById(String brandId);

    List<RealTimeCarListPojo> queryMonitorCarsByUserId(@Param("userId") Long userId, @Param("teamId") Long teamId);

    /**
     * 根据车辆ID查询，所有车队内，与此车相关的人（创建人、管理员、绑定司机、车主）
     */
    List<CarRolePojo> selectCarRelatedUserByCarId(@Param("carId") String carId);

    CarParmDto queryCarParm(QueryCarParmForm form);

    /**
     * 用户相关车辆查询
     * @param userId
     * @return
     */
    List<TripCarListDto> queryTripCarsByCarIds(Long userId);

    /**
     * 用户相关车辆查询
     * @param userId
     * @return
     */
    List<ColdCarsEntity> queryTripCarsByCarIds2(Long userId);
    /**
     * 根据vin查询车辆结构号
     */
    List<String> queryModelCodeByVin(@Param("carIdList") List<String> carIdList);

    /**
     * 获取购车时间
     * @param carVin
     * @return
     */
    Date queryCarSales(String carVin);

    /**
     * 根据vin查询结构号
     * @param carVin
     * @return
     */
    PhyExaPojo selectPhyByVin(String carVin);

    String selectTerminalIdByCarVin(@Param("carVin") String carVin);

    /**
     * 根据通信号（即：sim卡号）查询车辆信息
     * @param autoTerminal 通信号（即：sim卡号）
     * @return
     */
    CarEntity getByAutoTerminal(@Param("autoTerminal") String autoTerminal);

    /**
     * 根据id查询车辆
     * @param carId
     * @return
     */
    CarEntity selectCar(@Param("carId")String carId);
}
