package com.nut.driver.app.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.utils.JsonUtil;
import com.nut.driver.app.client.LocationServiceClient;
import com.nut.driver.app.dao.CarDao;
import com.nut.driver.app.dto.LastLocationDTO;
import com.nut.driver.app.form.GetCarLastLocationByAutForm;
import com.nut.driver.app.form.GetCarLocationForm;
import com.nut.driver.app.service.QueryLocationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author liuBing
 * @Classname QueryLocationServiceImpl
 * @Description TODO
 * @Date 2021/6/24 15:40
 */
@Slf4j
@Service
public class QueryLocationServiceImpl implements QueryLocationService {

    @Autowired
    private LocationServiceClient locationServiceClient;

    @Autowired
    private CarDao carDao;


    @Override
    public String getMellage(String carId) {
        String mileage = "0.00";
        // 查询终端通信号
        String autoTerminal = carDao.queryAutoTerminalByCarId(carId);
        if (StringUtils.isEmpty(autoTerminal)){
            return mileage;
        }
        List<Long> autoTerminals = new ArrayList<>();
        autoTerminals.add(Long.valueOf(autoTerminal));
        Map<Long, LastLocationDTO> map = queryLastLocation(autoTerminals);

        if (map != null && !map.isEmpty()){
            for (Long at : autoTerminals){
                try {
                    LastLocationDTO lastLocationDto = JsonUtil.fromJson(JsonUtil.toJson(map.get(at)), LastLocationDTO.class);
                    if (lastLocationDto != null && lastLocationDto.getMileage() != null){
                        mileage = lastLocationDto.getMileage().toString();
                    }
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }
        return mileage;
    }

    @Override
    public HttpCommandResultWithData carLocation(GetCarLocationForm com) {
        return locationServiceClient.carLocation(com);
    }


    public Map<Long, LastLocationDTO> queryLastLocation(List<Long> autoTerminal){

        Map<Long, LastLocationDTO> map = new HashMap<>();
        // 查询车辆位置信息
        GetCarLastLocationByAutForm locCommand = new GetCarLastLocationByAutForm();
        locCommand.setTerminalIdList(autoTerminal);
        HttpCommandResultWithData locationResult = locationServiceClient.getCarLastLocationByAut(locCommand);
        if (locationResult != null) {
            if (locationResult.getResultCode() != ECode.SUCCESS.code()) {
                log.info("getCarLocation errMessage : " + locationResult.getMessage());
                return map;
            }
            map = (Map<Long, LastLocationDTO>) locationResult.getData();
        }
        return map;
    }
}
