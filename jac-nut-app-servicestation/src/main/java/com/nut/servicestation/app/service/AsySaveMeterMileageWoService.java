package com.nut.servicestation.app.service;

import org.springframework.scheduling.annotation.Async;

import java.util.Date;

/*
 *  @author wuhaotian 2021/7/5
 */
public interface AsySaveMeterMileageWoService {


    void saveEndRepairMeterMileage(String woCode, String vin, Date timeClose);

    void saveScanReceiveMeterMileage(String woCode, String vin, Double mileage, Date timeReceive);
}
