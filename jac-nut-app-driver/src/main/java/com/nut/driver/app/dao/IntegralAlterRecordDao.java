package com.nut.driver.app.dao;

import com.github.pagehelper.Page;
import com.nut.driver.app.entity.IntegralAlterRecordEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * 积分变更记录
 *
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-21 14:41:08
 */
@Mapper
@Repository
public interface IntegralAlterRecordDao extends BaseMapper<IntegralAlterRecordEntity> {

    int countByUserIdAndItemAndToday(@Param("userId") String userId, @Param("item") String item);

    Page<IntegralAlterRecordEntity> findByUserId(String userId);

    int insertIntegralAlterRecord(IntegralAlterRecordEntity integralAlterRecord);

    int countByRuleId(@Param("ucId")String ucId,@Param("ruleId")Integer ruleId,
                      @Param("startDate")LocalDateTime startDate, @Param("endDate")LocalDateTime endDate);

    Integer integralCount(@Param("ucId")String ucId,@Param("startDate")LocalDateTime startDate, @Param("endDate")LocalDateTime endDate);

    Integer updateFreeze(@Param("ucId")String ucId,@Param("startDate")LocalDateTime startDate, @Param("endDate")LocalDateTime endDate);


}
