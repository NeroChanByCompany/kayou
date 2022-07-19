package com.nut.servicestation.app.dao;

import com.nut.servicestation.app.domain.Car;
import com.nut.servicestation.app.pojo.CarRedisPojo;
import com.nut.servicestation.app.pojo.LocationByCarPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/*
 *  @author wuhaotian 2021/7/2
 */
@Mapper
public interface CarDao {

    String queryCarNumberByVin(String carVin);

    /**
     * 根据底盘号查询相关用户ID（创建者、管理员、司机、车主）
     */
    List<String> selectUcIdByChassisNum(String chassisNum);


    Car selectByPrimaryKey(String id);

    CarRedisPojo queryCarByAutoTerminal(String terminalId);


    /**
     * 根据底盘号查询车辆ID和车牌号
     */
    Car selectCarByCarVin(String chassisNum);

    /**
     * VIN查询车辆ID
     *
     * @param vin VIN
     * @return 车辆ID
     */
    String queryCarIdByVin(String vin);

    LocationByCarPojo queryLocationByCarByTerminalId(String terminalId);

    int selectTerminalTypeFromTsp(@Param("chassisNum") String chassisNum, @Param("DbName") String DbName);

    Car queryCarModelBaseByVin(String vin);

    List<Long> queryUcIdByVin0(@Param("vin") String vin);

    Long queryUcIdByVin1(@Param("vin") String vin);

    Long queryUcIdByVin2(@Param("vin") String vin);

}
