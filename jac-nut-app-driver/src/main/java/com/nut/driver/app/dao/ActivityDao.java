package com.nut.driver.app.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nut.driver.app.entity.ActivityEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author liuBing
 * @Classname ActivityDao
 * @Description TODO
 * @Date 2021/7/7 11:01
 */
@Mapper
public interface ActivityDao extends BaseMapper<ActivityEntity> {

    List<ActivityEntity> checkList();
}
