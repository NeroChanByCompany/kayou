package com.nut.driver.app.dao;

import com.nut.driver.app.entity.DictVehicleUseEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 车辆用途字段表
 * 
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-29 09:29:42
 */
@Mapper
public interface DictVehicleUseDao extends BaseMapper<DictVehicleUseEntity> {

    /**
    * @Description：${todo}
    * @author YZL
    * @data 2021/6/29 9:32
    */
    List<DictVehicleUseEntity> findAll();
}
