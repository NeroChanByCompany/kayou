package com.nut.truckingteam.app.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nut.truckingteam.app.form.User;
import org.apache.ibatis.annotations.Mapper;

/*
 *  @author wuhaotian 2021/7/12
 */
@Mapper
public interface UserDao extends BaseMapper<User> {
}
