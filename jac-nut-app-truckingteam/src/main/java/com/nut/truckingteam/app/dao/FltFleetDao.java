package com.nut.truckingteam.app.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nut.truckingteam.app.entity.FltFleetEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 车队表
 *
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-28 13:04:46
 */
@Mapper
public interface FltFleetDao extends BaseMapper<FltFleetEntity> {

    FltFleetEntity selectByPrimaryKey(Long id);

}
