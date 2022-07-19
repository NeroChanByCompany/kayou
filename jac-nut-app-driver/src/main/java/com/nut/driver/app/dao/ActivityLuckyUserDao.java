package com.nut.driver.app.dao;

import com.nut.driver.app.entity.ActivityLuckyUserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 抽奖用户
 *
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-29 10:43:26
 */
@Mapper
public interface ActivityLuckyUserDao extends BaseMapper<ActivityLuckyUserEntity> {
    int countByPhoneAndAppType(@Param("phone") String phone, @Param("appType") String appType);
}
