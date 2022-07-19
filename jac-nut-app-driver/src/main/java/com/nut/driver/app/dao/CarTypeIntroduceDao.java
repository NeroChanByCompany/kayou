package com.nut.driver.app.dao;

import com.nut.driver.app.entity.CarTypeIntroduceEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 车型介绍表
 *
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-21 10:27:33
 */
@Mapper
public interface CarTypeIntroduceDao extends BaseMapper<CarTypeIntroduceEntity> {

    /**
     * @Description TODO 查询品系、驱动、排放、排量、平台查询车型号
     */
    List<String> queryCarModels(Map<String, Object> params);

}
