package com.nut.servicestation.app.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nut.servicestation.app.entity.WorkOrderLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author liuBing
 * @Classname WorkOrderLog
 * @Description TODO
 * @Date 2021/8/16 9:53
 */
@Mapper
public interface WorkOrderLogMapper extends BaseMapper<WorkOrderLogEntity> {
}
