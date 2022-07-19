package com.nut.driver.app.service.impl;

import com.nut.driver.app.entity.IntegralAlterRecordEntity;
import com.nut.driver.app.service.IntegralAlterRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.nut.driver.app.dao.IntegralAlterRecordDao;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class IntegralAlterRecordServiceImpl extends ServiceImpl<IntegralAlterRecordDao, IntegralAlterRecordEntity> implements IntegralAlterRecordService {

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void insertIntegralAlterRecord(IntegralAlterRecordEntity integralAlterRecordEntity){
        this.baseMapper.insertIntegralAlterRecord(integralAlterRecordEntity);
    }


}
