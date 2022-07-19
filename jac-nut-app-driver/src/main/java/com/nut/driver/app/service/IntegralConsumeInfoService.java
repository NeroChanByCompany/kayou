package com.nut.driver.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nut.driver.app.entity.IntegralAlterRecordEntity;
import com.nut.driver.app.entity.IntegralConsumeInfoEntity;

/**
 * 积分消费信息
 *
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-15 19:38:03
 */
public interface IntegralConsumeInfoService extends IService<IntegralConsumeInfoEntity> {

    void insertIntegralAlterRecord(IntegralAlterRecordEntity integralAlterRecord);
}

