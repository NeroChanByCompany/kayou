package com.nut.driver.app.dao;

import com.nut.driver.app.entity.IntegralAlterRecordEntity;
import com.nut.driver.app.entity.IntegralConsumeInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 积分消费信息
 *
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-15 19:38:03
 */
@Mapper
public interface IntegralConsumeInfoDao extends BaseMapper<IntegralConsumeInfoEntity> {

    int insertIntegralAlterRecord(IntegralAlterRecordEntity integralAlterRecord);
}
