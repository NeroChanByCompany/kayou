package com.nut.servicestation.app.service.impl;

import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.utils.JsonUtil;
import com.nut.common.utils.LonLatUtil;
import com.nut.common.utils.StringUtil;
import com.nut.servicestation.app.client.LocationServiceClient;
import com.nut.servicestation.app.dao.CarDao;
import com.nut.servicestation.app.dto.CarLocationToStationNewDto;
import com.nut.servicestation.app.dto.NearbyCarsDto;
import com.nut.servicestation.app.dto.NearbyCarsListDto;
import com.nut.servicestation.app.dto.UserInfoDto;
import com.nut.servicestation.app.form.NearbyCarsForm;
import com.nut.servicestation.app.form.QueryNearByTerminalForm;
import com.nut.servicestation.app.pojo.LocationByCarPojo;
import com.nut.servicestation.app.service.ServiceStationLocationService;
import com.nut.servicestation.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/*
 *  @author wuhaotian 2021/7/7
 */
@Slf4j
@Service("ServiceStationLocationService")
public class ServiceStationLocationServiceImpl implements ServiceStationLocationService {


    @Value("${database_name}")
    private String DbName;

    @Value("${nearby_terminal_url}")
    private String nearbyTerminalUrl;
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserService userService;
    @Autowired
    private CarDao carMapper;
    @Autowired
    private LocationServiceClient locationServiceClient;


    @Override
    public HttpCommandResultWithData queryNearbyCars(NearbyCarsForm command) throws Exception {
        log.info("[ServiceStationLocationService][queryNearbyCars] start ....");
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());
        //?????????????????????????????????
        UserInfoDto userInfoDto = userService.getUserInfoByUserId(command.getUserId(), false);
        if (userInfoDto == null) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("?????????????????????");
            return result;
        }
        //????????????????????????????????????
        if (StringUtil.isEmpty(userInfoDto.getServiceStationLon(), userInfoDto.getServiceStationLat())) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("???????????????????????????????????????????????????????????????");
            return result;
        }
        //??????????????????
        /*List<LocationByCarPojo> carList = carMapper.queryCarList();
        //??????????????????????????????????????????
        if ((carList == null) || (carList.size() == 0)) {
            NearbyCarsListDto nearbyCarsListDto = new NearbyCarsListDto();
            nearbyCarsListDto.setServiceStationName(userInfoDto.getServiceStationName());
            nearbyCarsListDto.setServiceLon(userInfoDto.getServiceStationLon());
            nearbyCarsListDto.setServiceLat(userInfoDto.getServiceStationLat());
            result.setData(nearbyCarsListDto);
            return result;
        }*/
        //???????????????
        NearbyCarsListDto nearbyCarsListDto = new NearbyCarsListDto();
        nearbyCarsListDto.setServiceStationName(userInfoDto.getServiceStationName());
        nearbyCarsListDto.setServiceLon(userInfoDto.getServiceStationLon());
        nearbyCarsListDto.setServiceLat(userInfoDto.getServiceStationLat());

        /*//?????????????????????MAP???????????????
        Map<String, LocationByCarPojo> carMap = new HashMap<>();

        //???????????????????????????
        GetCarLocationToStationCommand requestCommand = new GetCarLocationToStationCommand();
        requestCommand.setLon(Double.valueOf(userInfoDto.getServiceStationLon()));
        requestCommand.setLat(Double.valueOf(userInfoDto.getServiceStationLat()));
        requestCommand.setRange(Integer.valueOf(command.getRange()));
        requestCommand.setTerminalIdList(ServerStationPojoToDto.idsList(carList, carMap));
        log.info("[ServiceStationLocationService][queryNearbyCars]????????????????????????" + JsonUtil.toJson(requestCommand));*/

        QueryNearByTerminalForm queryNearByTerminalCommand = new QueryNearByTerminalForm();
        queryNearByTerminalCommand.setLongitude(String.valueOf((long)Math.floor(Double.parseDouble(userInfoDto.getServiceStationLon())*1000000d)));
        queryNearByTerminalCommand.setLatitude(String.valueOf((long)Math.floor(Double.parseDouble(userInfoDto.getServiceStationLat())*1000000d)));
        queryNearByTerminalCommand.setUnit("1");
        queryNearByTerminalCommand.setRadius(command.getRange());
        log.info("[ServiceStationLocationService][queryNearbyCars]????????????????????????" + JsonUtil.toJson(queryNearByTerminalCommand));

        //?????????????????????
        HttpCommandResultWithData requestResult = restTemplate.postForObject(nearbyTerminalUrl, queryNearByTerminalCommand, HttpCommandResultWithData.class);

        if (requestResult != null && ECode.SUCCESS.code() == requestResult.getResultCode()) {
            String data = JsonUtil.toJson(requestResult.getData());
            log.info("[ServiceStationLocationService][queryNearbyCars]????????????????????????" + data);
            if(null == data){
                result.setResultCode(ECode.DATA_Exception.code());
                result.setMessage("?????????????????????????????????????????????");
                return result;
            }
            // ??????????????????map
            List<CarLocationToStationNewDto> list = JsonUtil.toList(JsonUtil.toJson(requestResult.getData()), CarLocationToStationNewDto.class);

            if (list != null) {
                //???????????????????????????
                List<NearbyCarsDto> nearbyCarsDtoList = new ArrayList<>();

                //??????????????????????????????????????????
                for (CarLocationToStationNewDto dto : list) {
                    //?????????????????????
                    double lon = Double.parseDouble(dto.getLongtitude());
                    double lat = Double.parseDouble(dto.getLatitude());
                    double distance = LonLatUtil.getDistance(Double.valueOf(userInfoDto.getServiceStationLat()), Double.valueOf(userInfoDto.getServiceStationLon()),
                            lat, lon);
                    log.info("[ServiceStationLocationService][queryNearbyCars]????????????" + dto.getTerminalId() + "?????????????????????" + distance + "???");
                    //?????????????????????????????????????????????????????????KM???
                    if (distance <= (Double.valueOf(command.getRange()) * 1000) && (dto.getTerminalId() != null)) {
                        //??????????????????
                        //LocationByCarPojo locationByCarPojo = carMap.get(dto.getTerminalId());
                        LocationByCarPojo locationByCarPojo = carMapper.queryLocationByCarByTerminalId(dto.getTerminalId());
                        if (locationByCarPojo != null) {
                            NearbyCarsDto nearbyCarsDto = new NearbyCarsDto();
                            nearbyCarsDto.setCarLon(String.valueOf(lon));
                            nearbyCarsDto.setCarLat(String.valueOf(lat));
                            nearbyCarsDto.setCarNumber(locationByCarPojo.getCarNumber());
                            //String vin = carMap.get(dto.getTerminalId()).getCarVin();
                            String vin = locationByCarPojo.getCarVin();
                            //???VIN????????????
                            if (StringUtil.isNotEmpty(vin) && (vin.length() <= 8)) {
                                nearbyCarsDto.setChassisNumber(vin);
                            }
                            if (StringUtil.isNotEmpty(vin) && (vin.length() > 8)) {
                                nearbyCarsDto.setChassisNumber(vin.substring(vin.length() - 8));
                            }
                            //?????????????????????
                            StringBuffer stringBuffer = new StringBuffer();
                            if (StringUtil.isNotEmpty(locationByCarPojo.getCarSeriesName())) {
                                stringBuffer.append(locationByCarPojo.getCarSeriesName());
                            }
                            if (StringUtil.isNotEmpty(locationByCarPojo.getEngine())) {
                                stringBuffer.append("/").append(locationByCarPojo.getEngine());
                            }
                            if (StringUtil.isNotEmpty(locationByCarPojo.getCarModelName())) {
                                stringBuffer.append(" ").append(locationByCarPojo.getCarModelName());
                            }
                            nearbyCarsDto.setCarModel(stringBuffer.toString());
                            nearbyCarsDtoList.add(nearbyCarsDto);
                        }
                    }
                }
                //??????????????????
                nearbyCarsListDto.setList(nearbyCarsDtoList);
            }

        } else {
            log.info("[ServiceStationLocationService][queryNearbyCars]????????????????????????????????????.....");
            if (requestResult != null) {
                log.error(requestResult.getMessage());
            }
        }
        result.setData(nearbyCarsListDto);
        log.info("[ServiceStationLocationService][queryNearbyCars] end ....");
        return result;
    }
}
