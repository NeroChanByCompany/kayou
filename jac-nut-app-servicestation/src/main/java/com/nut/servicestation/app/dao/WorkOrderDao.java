package com.nut.servicestation.app.dao;

import com.github.pagehelper.Page;
import com.nut.servicestation.app.domain.RescueRoutePoint;
import com.nut.servicestation.app.domain.WorkOrder;
import com.nut.servicestation.app.dto.*;
import com.nut.servicestation.app.form.RefuseApplyCheckForm;
import com.nut.servicestation.app.pojo.QueryWoListByStatusPojo;
import com.nut.servicestation.app.pojo.WorkOrderInfoPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface WorkOrderDao {
    int deleteByPrimaryKey(Long id);

    int insert(WorkOrder record);

    int insertSelective(WorkOrder record);

    WorkOrder selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(WorkOrder record);

    int updateByPrimaryKey(WorkOrder record);

    /* ----------------自定义sql由此向下---------------- */

    Page<QueryWoListByStatusPojo> queryWoListByStatus(Map<String, Object> param);

    /**
     * 查询工单信息：通过工单号
     */
    QueryWoListByStatusPojo queryWorkOrderByWoCodeNew(@Param("woCode")String woCode);


    /**
     * 查询工单信息
     *
     * @param map
     * @return
     */
    WorkOrder selectByWoCode(Map<String, String> map);

    /**
     * 批量查询工单状态
     *
     * @param list
     * @return
     */
    List<WoStatusDto> queryWoStatusByWoCode(List<String> list);

    /**
     * 查询关闭申请审核
     *
     * @param param
     * @return
     */
    Page<CloseApplysDto> queryCloseApplysWork(Map<String, Object> param);

    /**
     * 查询拒单审核列表
     *
     * @param param
     * @return
     */
    Page<RefuseApplysDto> refuseApplys(Map<String, Object> param);

    /**
     * 查询工单信息
     *
     * @param command
     * @return
     */
    WorkOrder queryWorkOrderByWoCode(RefuseApplyCheckForm command);

    /**
     * 修改申请审核列表查询
     *
     * @param param
     * @return
     */
    Page<ModifyApplysDto> queryModifyApplysWork(Map<String, Object> param);

    /**
     * 通过服务站ID和底盘号查询工单列表
     */
    Page<QueryWoListByStatusPojo> queryWorkOrderByVin(Map<String, String> param);

    /**
     * 查询工单信息
     *
     * @param map
     * @return
     */
    WorkOrder selectByWoCodeAndAssignTo(Map<String, String> map);

    /**
     * 查询指定状态下的所有工单
     *
     * @param list 状态列表
     * @return 工单信息
     */
    List<WorkOrder> selectByWoStatusIn(List<Integer> list);

    /**
     * 查询外出距离异常预警工单列表
     *
     * @param DbName
     * @param searchKey
     * @param list
     * @return
     */
    List<QueryDistanceAnomalyDto> queryDistanceAnomalyOrderList(@Param("DbName") String DbName,
                                                                @Param("searchKey") String searchKey,
                                                                @Param("list") List<Integer> list);

    /**
     * 查询外出距离异常预警总数量
     *
     * @param list
     * @return
     */
    List<RescueRoutePoint> queryDistanceAnomalyOrderCountList(@Param("list") List<Integer> list);

    /**
     * 二次外出更新参数null
     *
     * @param record
     * @return
     */
    int updateNullByPrimaryKey(WorkOrder record);

    /**
     * 车在服务站的待接受或待接车的进出站工单数量
     *
     * @param carId        车辆ID
     * @param staCode      服务站编码
     * @param toBeAccepted 待接受状态
     * @param toReceive    待接车状态
     * @return 工单数量
     */
    Long queryOnTheWayWork(@Param("carId") String carId, @Param("staCode") String staCode,
                           @Param("toBeAccepted") Integer toBeAccepted, @Param("toReceive") Integer toReceive);

    /**
     * 查询用户是否有已经确认出发的在途外出救援工单
     *
     * @param userId       用户ID
     * @param stationCode  服务站编码
     * @param woType       工单类型（外出救援）
     * @param woStatusList 工单状态列表
     * @return 工单数量
     */
    Long countAlreadyDepartWo(@Param("userId") String userId, @Param("stationCode") String stationCode,
                              @Param("woType") Integer woType, @Param("list") List<Integer> woStatusList);

    /**
     * 查询在途工单数
     */
    Long countWoByStatus(Map<String, Object> param);

    /**
     * 根据用户名和类型查询外出未完成工单
     *
     * @param param
     * @return
     */
    List<WorkOrder> selectOutCountByUser(Map<String, Object> param);

    /**
     * 根据用户名和类型查询进站状态为维修中并且添加了维修项的工单
     *
     * @param param
     * @return
     */
    List<WorkOrder> selectInRepairingCountByUser(Map<String, Object> param);

    /**
     * 根据用户名和类型查询进站状态为"关闭申请审核中-维修"的工单
     *
     * @param param
     * @return
     */
    List<WorkOrder> selectInClosingCountByUser(Map<String, Object> param);

    /**
     * 根据底盘号、服务站code 状态 类型查询工单
     *
     * @param chassisNo    底盘号
     * @param stationCode  服务站编码
     * @param woType       工单类型（外出救援）
     * @param woStatusList 工单状态列表
     * @return 工单信息
     */
    List<WorkOrder> selectByVinStationCodeStatus(@Param("chassisNo") String chassisNo, @Param("stationCode") String stationCode,
                                                 @Param("woType") Integer woType, @Param("list") List<Integer> woStatusList);

    /**
     * 通过底盘号查询车辆最近一条创建的工单
     */
    WorkOrderInfoPojo queryLatelyWorkOrderByVin(String carVin);

    /**
     * 通过底盘号查询车辆首保维修项个数
     */
    Long queryFirstCountByChuNum(@Param("chassisNum") String chassisNum);


    /**
     * 查询设备外出工单数量
     **/
    Integer queryOutOrderNum(@Param("deviceId") String deviceId, @Param("userName") String userName);

    /**
     * 查询当前工单是否是抢单工单
     * @param woCode 工单号
     * @param stationId 服务站id
     * @return
     */
    WorkOrder queryWorkOrderStation(@Param("woCode") String woCode,@Param("stationId") String stationId);

    /**
     * 更新当前抢单信息为已绑定
     * @param order
     */
    void updateWorkOrderStationBind(WorkOrder order);

    /**
     * 更新当前抢单信息为已被其他服务站绑定
     * @param order
     */
    void updateWorkOrderStationNoBind(WorkOrder order);

    /**
     * 更新工单信息
     * @param order
     */
    void updateWorkOrder(WorkOrder order);

    /**
     *查询抢单列表
     * @param serviceStationId
     * @return
     */
    Page<QueryWoListByStatusPojo> queryOrderList(String serviceStationId);

    /**
     *查询是否有未完成工单
     * @param carVin
     * @return
     */
    WorkOrder selectByCarVin(@Param("carVin") String carVin,@Param("serviceStationId") String ServiceStationId);

    /**
     * 针对服务站接单员,查询状态100,130,140,220,230
     */
    List<Map<String, Object>> queryWoCodeByVin(@Param("vin") String vin, @Param("stationId") String stationId);

    /**
     * 针对服务站接单员,查询状态130,140,220,230
     */
    List<Map<String, Object>> queryWoCodeByVin1(@Param("vin") String vin, @Param("stationId") String stationId);


    /**
     * 查询状态110, 150, 160, 170, 180, 190, 240, 250, 260, 270
     */
    List<Map<String, Object>> queryWoCodeByVin2(@Param("vin") String vin, @Param("stationId") String stationId);

    /**
     * 查询服务类型为保外（保外保养3和保外维修7）的工单号
     * @return
     */
    List<String> queryWocodeByServiceType();

    List<Integer> getRelationServiceTypeList(String woCode);
}
