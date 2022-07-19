package com.nut.driver.app.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nut.driver.app.entity.UserListenerLogEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author liuBing
 * @Classname UserListenerLogDao
 * @Description TODO 用户动作监听
 * @Date 2021/9/13 11:14
 */
@Mapper
@Repository
public interface UserListenerLogDao extends BaseMapper<UserListenerLogEntity> {
}
