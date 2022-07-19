package com.nut.driver.app.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nut.driver.app.entity.ScoreTaskRuleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 *
 *
 * @author hcb
 * @email hcb@163.com
 * @date 2021-11-17 15:56:02
 */
@Mapper
public interface ScoreTaskRuleDao extends BaseMapper<ScoreTaskRuleEntity> {

    Map<String, Object> inviteIntegral(@Param("id") String id);

}
