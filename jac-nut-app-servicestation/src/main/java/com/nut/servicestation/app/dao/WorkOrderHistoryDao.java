package com.nut.servicestation.app.dao;

import com.nut.servicestation.app.domain.WorkOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/*
 *  @author wuhaotian 2021/7/6
 */
@Mapper
public interface WorkOrderHistoryDao {
    /**
     * 备份一次外出救援工单数据
     * <b>注意！：work_order表新增字段时sql要一起修改</b>
     *
     * @param workOrder         插入实体
     * @param timesRescueNumber 外出救援次数
     * @return 更新条数
     */
    int insert(@Param(value = "wrkOrder") WorkOrder workOrder, @Param(value = "timesRescueNumber") Integer timesRescueNumber);
}
