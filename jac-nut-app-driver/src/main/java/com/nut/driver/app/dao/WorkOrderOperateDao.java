package com.nut.driver.app.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nut.driver.app.entity.WorkOrderOperateEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 *
 *
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-23 16:51:40
 */
@Mapper
public interface WorkOrderOperateDao extends BaseMapper<WorkOrderOperateEntity> {

    int insertSelective(WorkOrderOperateEntity record);

    String queryCancelRescueReason(@Param("woCode") String woCode, @Param("operateCode") Integer operateCode);

}
