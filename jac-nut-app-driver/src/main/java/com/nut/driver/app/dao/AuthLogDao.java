package com.nut.driver.app.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nut.driver.app.entity.AuthLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author liuBing
 * @Classname AuthLogDao
 * @Description TODO
 * @Date 2021/8/10 13:08
 */
@Mapper
public interface AuthLogDao extends BaseMapper<AuthLogEntity> {
}
