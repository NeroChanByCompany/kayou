package com.nut.servicestation.app.service.impl;

import com.nut.servicestation.app.dao.StationInfoDao;
import com.nut.servicestation.app.domain.AddWoStationInfo;
import com.nut.servicestation.app.service.StationInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/*
 *  @author wuhaotian 2021/7/8
 */
@Slf4j
@Service("StationInfoService")
public class StationInfoServiceImpl implements StationInfoService {

    @Value("${database_name}")
    private String DbName;

    @Autowired
    private StationInfoDao stationInfoMapper;

    @Override
    public AddWoStationInfo getInfo(Long stationId) {
        return stationInfoMapper.getInfo(DbName, stationId);
    }

    @Override
    public void setPic(Long stationId, String pic) {
        stationInfoMapper.updatePic(DbName, stationId, pic);
    }
}
