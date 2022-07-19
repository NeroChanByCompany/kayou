package com.nut.driver.app.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nut.driver.app.entity.CarAnalyseEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author liuBing
 * @Classname StatisticsDao
 * @Description TODO
 * @Date 2021/6/19 17:14
 */
@Mapper
@Repository
public interface StatisticsDao extends BaseMapper<CarAnalyseEntity> {
}
