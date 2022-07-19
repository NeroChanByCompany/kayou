package com.nut.servicestation.app.service.impl;

import com.nut.locationservice.app.dto.CarLocationOutputDto;
import com.nut.servicestation.app.dao.CarDao;
import com.nut.servicestation.app.dao.MeterMileageWoDao;
import com.nut.servicestation.app.domain.Car;
import com.nut.servicestation.app.domain.MeterMileageWo;
import com.nut.servicestation.app.service.AddWoStationService;
import com.nut.servicestation.app.service.AsySaveMeterMileageWoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;

/*
 *  @author wuhaotian 2021/7/5
 */
@Slf4j
@Service("AsySaveMeterMileageWoService")
public class AsySaveMeterMileageWoServiceImpl implements AsySaveMeterMileageWoService {



    @Autowired
    private MeterMileageWoDao meterMileageWoMapper;
    @Autowired
    private AddWoStationService addWoStationService;
    @Autowired
    private CarDao carMapper;

    @Override
    @Async
    public void saveEndRepairMeterMileage(String woCode, String vin, Date timeClose) {
        log.error("[saveEndRepairMeterMileage] start:{},{},{}", woCode, vin, timeClose);
        MeterMileageWo queryMMW = meterMileageWoMapper.queryByWoCode(woCode);
        if (queryMMW != null) {
            /* 获取车辆位置上报信息 */
            CarLocationOutputDto dto = addWoStationService.getCarUpInfo(vin);

            MeterMileageWo updateMMW = new MeterMileageWo();
            updateMMW.setId(queryMMW.getId());
            updateMMW.setTimeClose(timeClose);
            if (dto == null){
                updateMMW.setEndrepairMileage(0D);
            }else {
                updateMMW.setEndrepairMileage(dto.getMileage() == null ? 0D : dto.getMileage());
            }
            meterMileageWoMapper.updateByPrimaryKeySelective(updateMMW);
        } else {
            log.error("[saveEndRepairMeterMileage] no data:{}", woCode);
        }
        log.info("[saveEndRepairMeterMileage] end");
    }

    @Override
    @Async
    public void saveScanReceiveMeterMileage(String woCode, String vin, Double mileage, Date timeReceive) {
        log.info("[saveScanReceiveMeterMileage] start:{},{},{},{}", woCode, vin, mileage, timeReceive);
        MeterMileageWo insertMMW = new MeterMileageWo();
        insertMMW.setWoCode(woCode);
        insertMMW.setChassisNum(vin);
        insertMMW.setTimeReceive(timeReceive);
        insertMMW.setReceiveMileage(mileage);
        insertMMW.setReplaceStatus(1);
        insertMMW.setCreateTime(new Timestamp(System.currentTimeMillis()));
        Car queryCar = carMapper.queryCarModelBaseByVin(vin);
        if (queryCar != null && queryCar.getCarModelBase() != null) {
            log.info("[saveScanReceiveMeterMileage] modelbase:{}", queryCar.getCarModelBase());
            insertMMW.setCarModelBase(queryCar.getCarModelBase());
        }
        meterMileageWoMapper.insertSelective(insertMMW);
        log.info("[saveScanReceiveMeterMileage] end");
    }
}
