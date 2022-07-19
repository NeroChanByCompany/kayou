package com.nut.driver.app.service.impl;

import com.nut.driver.app.dao.IntegralConsumeInfoDao;
import com.nut.driver.app.entity.IntegralAlterRecordEntity;
import com.nut.driver.app.service.IntegralConsumeInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.nut.driver.app.entity.IntegralConsumeInfoEntity;

@Service("integralConsumeInfoService")
@Slf4j
public class IntegralConsumeInfoServiceImpl extends ServiceImpl<IntegralConsumeInfoDao, IntegralConsumeInfoEntity> implements IntegralConsumeInfoService {

    @Override
    public void insertIntegralAlterRecord(IntegralAlterRecordEntity integralAlterRecord) {
        log.info("增加积分变更历史记录，信息:{}", integralAlterRecord);
        this.baseMapper.insertIntegralAlterRecord(integralAlterRecord);
    }
}
