package com.nut.servicestation.app.dao;

import com.nut.servicestation.app.domain.RescueRoutePoint;
import com.nut.servicestation.app.domain.RescueRoutePointHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/*
 *  @author wuhaotian 2021/7/5
 */
@Mapper
public interface RescueRoutePointHistoryDao {

    /**
     * 查询历史救援轨迹
     *
     * @param woCode            工单号
     * @param timesRescueNumber 外出救援次数
     * @return 轨迹点
     */
    RescueRoutePointHistory queryHistoryRescueRoutePoint(@Param(value = "woCode") String woCode,
                                                         @Param(value = "timesRescueNumber") String timesRescueNumber);

    int updateByPrimaryKeySelective(RescueRoutePointHistory record);
    /**
     * 备份一次外出救援轨迹
     * <b>注意！：rescue_route_point表新增字段时sql要一起修改</b>
     *
     * @param rescueRoutePoint  插入实体
     * @param timesRescueNumber 外出救援次数
     * @return 更新条数
     */
    int insertHistory(@Param(value = "rescueRoutePoint") RescueRoutePoint rescueRoutePoint,
                      @Param(value = "timesRescueNumber") Integer timesRescueNumber);
}
