package com.nut.servicestation.app.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nut.servicestation.app.domain.WorkOrderStationEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author liuBing
 * @Classname WorkOrderStationDao
 * @Description TODO 抢单信息
 * @Date 2021/8/11 13:03
 */
@Mapper
public interface WorkOrderStationDao extends BaseMapper<WorkOrderStationEntity> {
}
