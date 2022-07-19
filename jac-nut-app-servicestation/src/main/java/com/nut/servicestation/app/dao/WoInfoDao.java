package com.nut.servicestation.app.dao;

import com.nut.servicestation.app.pojo.CountWoPojo;
import com.nut.servicestation.app.pojo.WoInfoPojo;
import com.nut.servicestation.app.pojo.WoProcessPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/*
 *  @author wuhaotian 2021/7/6
 */
@Mapper
public interface WoInfoDao {
    /**
     * 服务站+工单状态list查询工单信息
     *
     * @param map 服务站ID,工单状态list
     * @return 工单信息
     */
    List<CountWoPojo> queryWoByStationAndStatusIn(Map map);
    /**
     * 通过底盘号查询到该车所有的工单号（包括维修完成与未完成）
     * @param vin
     * @return 工单号列表
     */
    List<String> queryAllCodeByVin(@Param("chassisNum") String vin);
    /**
     * 通过底盘号查询该车尚未完成的工单号
     * @param vin
     * @return 工单号列表
     */
    List<String > queryCodeByVin(@Param("chassisNum") String vin);
    /**
     * 工单号 查询 工单详情
     *
     * @param hyDbName 寰游数据库名
     * @param woCode   工单号
     * @return 工单详情
     */
    List<WoInfoPojo> queryWoInfoByWoCode(@Param("hyDbName") String hyDbName, @Param("woCode") String woCode);
    /**
     * 车辆ID 查询 公告
     *
     * @param hyDbName 寰游数据库名
     * @param carId    车辆ID
     * @return 公告车型
     */
    String queryGgByCarId(@Param("hyDbName") String hyDbName, @Param("carId") String carId);
    /**
     * 查询最新一次的S级故障
     *
     * @param carId 车辆ID
     * @return 公告车型
     */
    String queryLatestSFault(String carId);
    /**
     * 工单号 查询 工单流程
     *
     * @param map 工单号+tboss/app区分
     * @return 工单流程
     */
    List<WoProcessPojo> queryWoProcessByWoCode(Map map);
    /**
     * 工单状态+操作唯一标识list查询图片信息
     *
     * @param map 工单号+操作唯一标识list
     * @return 图片信息
     */
    List<WoProcessPojo> queryWoPhotoByWoCodeAndOperateIn(Map map);

    /**
     * 查询服务站id
     * @param databaseName 数据库名称
     * @param userId 用户id
     * @return
     */
    Long queryStationById(@Param("databaseName") String databaseName,@Param("userId") String userId);

    /**
     * 查询抢单基础信息
     * @param databaseName 数据库名称
     * @param stationId 服务站id
     * @param woCode 工单号
     * @return
     */
    WoInfoPojo queryWorkOrderStation(@Param("databaseName") String databaseName,@Param("stationId") Long stationId,@Param("woCode") String woCode);

    /**
     * 查询当前服务站下所有未接单数量
     * @param serviceStationId
     * @return
     */
    Long queryWorkOrderStationCount(@Param("stationId") String serviceStationId);

    List<String> queryWorkOrderText();
}
