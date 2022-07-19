package com.nut.truckingteam.app.dao;

import com.nut.truckingteam.app.entity.FltCarOwnerMappingEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

/**
 * 车与车主绑定关系表
 *
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-28 13:12:38
 */
@Mapper
public interface FltCarOwnerMappingDao extends BaseMapper<FltCarOwnerMappingEntity> {
    /**
     * 查询车队的创建者
     */
    String queryPushCreator(Map<String, Object> param);

    /**
     * 查询车队的管理员
     */
    String queryPushAdmin(Map<String, Object> param);

    /**
     * 查询车辆的司机
     */
    String queryPushDriver(Map<String, Object> param);

    /**
     * 查询车辆的车主
     */
    String queryPushOwner(Map<String, Object> param);
}
