package com.nut.driver.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nut.driver.app.entity.IntegralAlterRecordEntity;

/**
 * 积分变更记录
 *
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-21 14:41:08
 */
public interface IntegralAlterRecordService extends IService<IntegralAlterRecordEntity> {

    void insertIntegralAlterRecord(IntegralAlterRecordEntity integralAlterRecordEntity);

}

