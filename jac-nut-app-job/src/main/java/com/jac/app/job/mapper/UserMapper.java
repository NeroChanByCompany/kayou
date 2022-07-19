package com.jac.app.job.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jac.app.job.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author liuBing
 * @Classname UserMapper
 * @Description TODO 冻结用户定时任务
 * @Date 2021/8/9 14:37
 */

@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {


}
