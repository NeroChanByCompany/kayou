package com.nut.servicestation.app.service.impl;

import com.nut.servicestation.app.dao.ScanDistanceRecordDao;
import com.nut.servicestation.app.domain.ScanDistanceRecord;
import com.nut.servicestation.app.service.AsyScanDistanceRecordSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/*
 *  @author wuhaotian 2021/7/8
 */
@Slf4j
@Service("AsyScanDistanceRecordSaveService")
public class AsyScanDistanceRecordSaveServiceImpl implements AsyScanDistanceRecordSaveService {


    @Autowired
    private ScanDistanceRecordDao sacnDistanceRecordMapper;

    @Override
    @Async
    public void sacnDistanceRecordSave(ScanDistanceRecord record) {
        log.info("[sacnDistanceRecordSave] start:{}", record.getWoCode());
        sacnDistanceRecordMapper.insert(record);
        log.info("[sacnDistanceRecordSave] end");
    }
}
