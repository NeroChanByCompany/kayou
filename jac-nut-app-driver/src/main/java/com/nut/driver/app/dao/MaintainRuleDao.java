package com.nut.driver.app.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nut.driver.app.entity.MaintainRuleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author liuBing
 * @Classname MaintainRuleDao
 * @Description TODO
 * @Date 2021/8/23 13:58
 */
@Mapper
@Repository
public interface MaintainRuleDao extends BaseMapper<MaintainRuleEntity> {

}
