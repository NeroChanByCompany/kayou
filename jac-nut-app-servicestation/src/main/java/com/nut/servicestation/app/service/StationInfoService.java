package com.nut.servicestation.app.service;

import com.nut.servicestation.app.domain.AddWoStationInfo;

/*
 *  @author wuhaotian 2021/7/8
 */
public interface StationInfoService {

    AddWoStationInfo getInfo(Long stationId);

    void setPic(Long stationId, String pic);
}
