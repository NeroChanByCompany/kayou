package com.nut.servicestation.app.service;

/*
 *  @author wuhaotian 2021/7/8
 */
public interface AsyCalculateManCarStationDistanceService {

    void calculateDistance(String type, String woCode, String lon, String lat, String userId);
}
