package com.nut.locationservice.app.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.nut.common.utils.HttpUtil;
import com.nut.common.utils.JsonUtil;
import com.nut.locationservice.app.dao.CarEolSystemDao;
import com.nut.locationservice.app.dao.HyCarDao;
import com.nut.locationservice.app.dto.CarLocationInputDto;
import com.nut.locationservice.app.dto.CarLocationOutputDto;
import com.nut.locationservice.app.form.GetCarLocationForm;
import com.nut.locationservice.app.module.LastLocationResponse;
import com.nut.locationservice.app.pojo.LastLocationPojo;
import com.nut.locationservice.app.service.CarLocationService;
import com.nut.locationservice.app.service.CarPosService;
import com.nut.common.utils.StringUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.app.locationService.service.impl
 * @Author: yzl
 * @CreateTime: 2021-06-16 19:03
 * @Version: 1.0
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class, readOnly = true)
public class CarPosServiceImpl implements CarPosService {

    @Autowired
    private CarLocationService carLocationService;

    @Autowired
    private CarEolSystemDao carEolSystemDao;

    @Autowired
    private HyCarDao hyCarDao;

    @Value("${database_name}")
    private String DatabaseName;

    @Value("${big_data.cloud_base_url}")
    private String baseUrl;
    @Value("${last_location_size_split:5000}")
    private Integer LASTLOCATIONSIZESPLIT;
    @Value("${last_location_parameter_fields:gpsDate,lon,lat,sm,mileage,terminalId,mileageDD}")
    private String fields;
    /**
     * 通过vin获取车辆经纬度
     *
     * @param form vins例:'123','456'
     * @return Map<String, CarLocationOutputDto>
     */
    @Override
    @SneakyThrows
    public Map<String, CarLocationOutputDto> getLocationByVins(GetCarLocationForm form) {
        long startTime = System.currentTimeMillis();    //获取开始时间
        //获取通信号
        List<CarLocationInputDto> carList = getCommunicationId(form.getCarSource(), form.getVins());
        long endTime = System.currentTimeMillis();    //获取结束时间

        log.info("getCommunicationId：" + (endTime - startTime) + "ms");
        startTime = System.currentTimeMillis();    //获取开始时间
        //调用接口获取经纬度
        Map<String, CarLocationOutputDto> map = carLocationService.getCarLocation(carList);
        endTime = System.currentTimeMillis();
        log.info("carLocationService.getCarLocation：" + (endTime - startTime) + "ms");
        return map;
    }


    @SneakyThrows
    @Override
    public List<LastLocationPojo> queryJsonLastLocation(List<Long> commIds) {

        String url = baseUrl + "/location/last/search";
        List<LastLocationPojo> lastLocationList = new ArrayList<>();
            Map<String,Object> map = new HashMap<>();
            map.put("fields",fields.split(","));
            for (int i = 0 ; i < commIds.size() ; i += LASTLOCATIONSIZESPLIT) {
                if(i + LASTLOCATIONSIZESPLIT <commIds.size() ) {
                    map.put("terminalIds",commIds.subList(i,i + LASTLOCATIONSIZESPLIT));
                }else {
                    map.put("terminalIds",commIds.subList(i,commIds.size()));
                }
                String result = HttpUtil.postHttps(url, JSONObject.parseObject(JSON.toJSONString(map)));
                Map<String, Object> resultMap = JsonUtil.toMap(result);
                if (Objects.nonNull(resultMap.get("resultCode")) && resultMap.get("resultCode").toString().equals("200") && resultMap.get("data") != null) {
                    String JSONStr = JSON.toJSONString(resultMap.get("data"));
                    lastLocationList.addAll(JSON.parseObject(JSONStr,new TypeReference<List<LastLocationPojo>>(){}));
                }
            }
        return lastLocationList;
    }

    /**
     * 获取车辆通讯号
     *
     * @param vins 例:'123','456'
     * @return List<CarLocationInputDto>
     */
    public List<CarLocationInputDto> getCommunicationId(String carSource, String vins) {
        log.info("getCommunicationId start");
        log.info("getCommunicationId,carSource={},vins={}", carSource, vins);
        List<CarLocationInputDto> list = null;
        if (StringUtil.isNotEmpty(vins)) {
            String[] vinArray = vins.replace("'", "").split(",");
            if ("eol".equals(carSource)) {
                list = carEolSystemDao.selectByTerminalId(Arrays.asList(vinArray));
            } else {
                list = hyCarDao.selectByTerminalId(DatabaseName, Arrays.asList(vinArray));
            }
        }
        log.info("getCommunicationId end");
        return list;
    }

}
