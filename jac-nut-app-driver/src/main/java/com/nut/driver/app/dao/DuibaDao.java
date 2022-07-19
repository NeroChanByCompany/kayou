package com.nut.driver.app.dao;

import com.github.pagehelper.Page;
import com.nut.driver.app.domain.IntegralAlterRecord;
import com.nut.driver.app.domain.IntegralConsumeInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface DuibaDao {

    int insertIntegralConsumeInfo(IntegralConsumeInfo integralConsumeInfo);

    IntegralConsumeInfo selectOrderByConfirmMap(Map<String, String> params);

    int updateIntegralConsumeInfoByConfirmInfo(IntegralConsumeInfo integralConsumeInfo);

    int insertIntegralAlterRecord(IntegralAlterRecord integralAlterRecord);

    int countByUserIdAndItem(@Param("userId") String userId, @Param("item") String item);

    int countByUserIdAndItemAndToday(@Param("userId") String userId, @Param("item") String item);

    Page<IntegralAlterRecord> findByUserId(String userId);

}