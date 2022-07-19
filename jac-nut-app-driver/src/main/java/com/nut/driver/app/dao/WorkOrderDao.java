package com.nut.driver.app.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.nut.driver.app.domain.RelationWorkOrderUser;
import com.nut.driver.app.domain.WorkOrder;
import com.nut.driver.app.dto.MyOrdersDto;
import com.nut.driver.app.entity.WorkOrderEntity;
import com.nut.driver.app.pojo.WorkOrderInfoPojo;
import com.nut.driver.app.pojo.MyOrderDetailPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *
 *
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-16 15:49:32
 */
@Mapper
public interface WorkOrderDao extends BaseMapper<WorkOrderEntity> {

    /**
     * 通过用户自增ID查询在途工单数
     *
     * @param userId       用户自增ID
     * @param closeInspect 工单关闭-关闭申请-检查
     * @param closeRepair  工单关闭-关闭申请-维修
     * @param closeRefused 工单关闭-拒单
     * @param workDone     已完成	维修结束
     * @return
     */
    Long queryOtwOrderNum(@Param("userId") Long userId, @Param("closeInspect") Integer closeInspect, @Param("closeRepair") Integer closeRepair,
                          @Param("closeRefused") Integer closeRefused, @Param("workDone") Integer workDone, @Param("cancelOrder") Integer cancelOrder,
                          @Param("takeoff") Integer takeoff, @Param("recieve") Integer recieve,
                          @Param("applyTakeOff") Integer applyTakeOff);

    /**
     * 通过底盘号查询车辆最近一条创建的工单
     */
    List<WorkOrderInfoPojo> queryLatelyWorkOrderByVin(List<String> vinList);

    int insert(WorkOrderEntity record);

    int  insertRelationWorkOrderUser(RelationWorkOrderUser relationWorkOrderUser);

    /**
     * 根据工单号查询工单信息
     *
     * @param woCode 工单号
     * @return 工单信息
     */
    WorkOrderEntity queryWorkOrderByWoCode(String woCode);

    int updateByPrimaryKeySelective(WorkOrderEntity record);

    int  queryEvaluateNum(String woCode);

    /**
     * 通过工单号查询我的预约详情
     *
     * @param woCode 工单号
     * @return 我的预约详情
     */
    MyOrderDetailPojo queryWorkOrderDetail(String woCode);

    /**
     * 通过预约人ID查询我的预约列表
     *
     * @param autoIncreaseId 预约人ID
     * @param woStatus       工单状态
     * @return 我的预约列表
     */
    Page<MyOrdersDto> queryWorkOrderList(@Param("autoIncreaseId") Long autoIncreaseId, @Param("phone") String phone,
                                         @Param("woStatus") Integer woStatus, @Param("chassisNumList") List<String> chassisNumList);

    /***
     *  获取待评价或是待审核数量
     * @param autoIncreaseId
     * @param phone
     * @param woStatus
     * @param chassisNumList
     * @return
     */
    Long queryEvaluateNumORauditNum(@Param("autoIncreaseId") Long autoIncreaseId, @Param("phone") String phone,
                                    @Param("woStatus") Integer woStatus, @Param("chassisNumList") List<String> chassisNumList);

    List<WorkOrder> queryWorkOrderByStatus(String status);


}
