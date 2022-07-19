package com.nut.driver.app.dao;

import com.github.pagehelper.Page;
import com.nut.driver.app.entity.WorkOrderEvaluateEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nut.driver.app.pojo.StationEvaluatesPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 工单评价表
 *
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-23 14:59:00
 */
@Mapper
public interface WorkOrderEvaluateDao extends BaseMapper<WorkOrderEvaluateEntity> {

    /**
     * 通过服务站ID查询评价列表
     * @param stationId 服务站ID
     * @return 评价列表
     */
    Page<StationEvaluatesPojo> queryStationEvaluates(@Param("DbName") String DbName, @Param("stationId") Long stationId);

    int insertSelective(WorkOrderEvaluateEntity record);

}
