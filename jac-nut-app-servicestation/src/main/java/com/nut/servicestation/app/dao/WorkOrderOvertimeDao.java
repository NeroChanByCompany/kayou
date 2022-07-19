package com.nut.servicestation.app.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

/*
 *  @author wuhaotian 2021/7/6
 */
@Mapper
public interface WorkOrderOvertimeDao {

    /**
     * 清空外出救援预警工单记录
     *
     * @return 超时工单列表
     */
    int deleteByType(Map<String, Object> param);
}
