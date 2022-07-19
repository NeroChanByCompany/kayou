package com.nut.driver.app.dao;

import com.nut.driver.app.entity.UserConfEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户配置表
 *
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-27 15:47:51
 */
@Mapper
public interface UserConfDao extends BaseMapper<UserConfEntity> {
    UserConfEntity findByUserId(@Param("userId") Long userId, @Param("topic") String topic, @Param("key") String key);
    int update(UserConfEntity userConfEntity);
    int insert(UserConfEntity userConfEntity);
    int countByUserId(Long userId);

}
