package com.nut.locationservice.app.service.impl;

import com.nut.location.protocol.common.LCLocationData;
import com.nut.location.protocol.webservice.newstatisticsdata.LCGetTerminalLocationDataRes;
import com.nut.locationservice.app.dto.CarHistoryLocationOutputDTO;
import com.nut.locationservice.app.dto.CarHistoryResultDTO;
import com.nut.locationservice.app.form.GetCarHistoryLocationForm;
import com.nut.locationservice.app.service.CarHistoryLocationService;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author liuBing
 * @Classname CarHistoryLocationServiceImpl
 * @Description TODO 具体实现
 * @Date 2021/6/16 10:21
 */
@Service
@Transactional(rollbackFor = Exception.class, readOnly = true)
public class CarHistoryLocationServiceImpl implements CarHistoryLocationService {

    /**
     * @return com.nut.locationstation.app.locationService.dto.CarHistoryResultDTO
     * @Author liuBing
     * @Description //TODO 查询车辆历史信息
     * @Date 10:51 2021/6/16
     * @Param [form] terminalId 车辆id queryDate 查询时间 index 索引 accessTocken token
     **/
    @SneakyThrows
    @Override
    public CarHistoryResultDTO getCarLocation(GetCarHistoryLocationForm form) {
        byte[] locationDataArr = new byte[0];
        //目前不清楚为何要注释掉该行代码
        //basicDataQueryWebService.getTerminalLocationData(form.getTerminalId(),form.getQueryDate(),form.getIndex(), form.getAccessTocken());
        if (locationDataArr != null && locationDataArr.length > 0) {
            LCGetTerminalLocationDataRes.GetTerminalLocationDataRes gps = LCGetTerminalLocationDataRes.GetTerminalLocationDataRes.parseFrom(locationDataArr);
            //获取数据条数
            int lastGps = gps.getTotalRecords();
            //TerminalId
            String tcommunctionId = (form.getTerminalId()).toString();

            if (lastGps != 0) {
                //位置数据
                LCLocationData.LocationData gpsData = gps.getLocationData();
                return new CarHistoryResultDTO(tcommunctionId, new CarHistoryLocationOutputDTO()
                        .setTerminalId(tcommunctionId)
                        .setLon(gpsData.getLongitude() / 1000000D)
                        .setLat(gpsData.getLatitude() / 1000000D));
            }
        }

        return new CarHistoryResultDTO((form.getTerminalId()).toString(), new CarHistoryLocationOutputDTO());

    }


}
