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
        //用户所在服务站信息查询
        UserInfoDto userInfoDto = userService.getUserInfoByUserId(command.getUserId(), false);
        if (userInfoDto == null) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("此用户不存在！");
            return result;
        }
        //服务站经纬度信息不能为空
        if (StringUtil.isEmpty(userInfoDto.getServiceStationLon(), userInfoDto.getServiceStationLat())) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("服务站位置信息缺失，请上传服务站位置信息！");
            return result;
        }
        //车辆信息查询
        /*List<LocationByCarPojo> carList = carMapper.queryCarList();
        //当没有车辆时只返回服务站信息
        if ((carList == null) || (carList.size() == 0)) {
            NearbyCarsListDto nearbyCarsListDto = new NearbyCarsListDto();
            nearbyCarsListDto.setServiceStationName(userInfoDto.getServiceStationName());
            nearbyCarsListDto.setServiceLon(userInfoDto.getServiceStationLon());
            nearbyCarsListDto.setServiceLat(userInfoDto.getServiceStationLat());
            result.setData(nearbyCarsListDto);
            return result;
        }*/
        //服务站信息
        NearbyCarsListDto nearbyCarsListDto = new NearbyCarsListDto();
        nearbyCarsListDto.setServiceStationName(userInfoDto.getServiceStationName());
        nearbyCarsListDto.setServiceLon(userInfoDto.getServiceStationLon());
        nearbyCarsListDto.setServiceLat(userInfoDto.getServiceStationLat());

        /*//将车辆集合转为MAP，方便取值
        Map<String, LocationByCarPojo> carMap = new HashMap<>();

        //调用位置云请求参数
        GetCarLocationToStationCommand requestCommand = new GetCarLocationToStationCommand();
        requestCommand.setLon(Double.valueOf(userInfoDto.getServiceStationLon()));
        requestCommand.setLat(Double.valueOf(userInfoDto.getServiceStationLat()));
        requestCommand.setRange(Integer.valueOf(command.getRange()));
        requestCommand.setTerminalIdList(ServerStationPojoToDto.idsList(carList, carMap));
        log.info("[ServiceStationLocationService][queryNearbyCars]请求位置云参数：" + JsonUtil.toJson(requestCommand));*/

        QueryNearByTerminalForm queryNearByTerminalCommand = new QueryNearByTerminalForm();
        queryNearByTerminalCommand.setLongitude(String.valueOf((long)Math.floor(Double.parseDouble(userInfoDto.getServiceStationLon())*1000000d)));
        queryNearByTerminalCommand.setLatitude(String.valueOf((long)Math.floor(Double.parseDouble(userInfoDto.getServiceStationLat())*1000000d)));
        queryNearByTerminalCommand.setUnit("1");
        queryNearByTerminalCommand.setRadius(command.getRange());
        log.info("[ServiceStationLocationService][queryNearbyCars]请求位置云参数：" + JsonUtil.toJson(queryNearByTerminalCommand));

        //调用位置云接口
        HttpCommandResultWithData requestResult = restTemplate.postForObject(nearbyTerminalUrl, queryNearByTerminalCommand, HttpCommandResultWithData.class);

        if (requestResult != null && ECode.SUCCESS.code() == requestResult.getResultCode()) {
            String data = JsonUtil.toJson(requestResult.getData());
            log.info("[ServiceStationLocationService][queryNearbyCars]位置云返回数据：" + data);
            if(null == data){
                result.setResultCode(ECode.DATA_Exception.code());
                result.setMessage("位置云没有查询到符合条件数据！");
                return result;
            }
            // 获取位置数据map
            List<CarLocationToStationNewDto> list = JsonUtil.toList(JsonUtil.toJson(requestResult.getData()), CarLocationToStationNewDto.class);

            if (list != null) {
                //符合范围的车辆集合
                List<NearbyCarsDto> nearbyCarsDtoList = new ArrayList<>();

                //车辆位置与服务站位置距离计算
                for (CarLocationToStationNewDto dto : list) {
                    //计算两点间距离
                    double lon = Double.parseDouble(dto.getLongtitude());
                    double lat = Double.parseDouble(dto.getLatitude());
                    double distance = LonLatUtil.getDistance(Double.valueOf(userInfoDto.getServiceStationLat()), Double.valueOf(userInfoDto.getServiceStationLon()),
                            lat, lon);
                    log.info("[ServiceStationLocationService][queryNearbyCars]通信号：" + dto.getTerminalId() + "与服务站距离：" + distance + "米");
                    //在圆形半径范围的返回前端（前端的半径为KM）
                    if (distance <= (Double.valueOf(command.getRange()) * 1000) && (dto.getTerminalId() != null)) {
                        //车辆基本信息
                        //LocationByCarPojo locationByCarPojo = carMap.get(dto.getTerminalId());
                        LocationByCarPojo locationByCarPojo = carMapper.queryLocationByCarByTerminalId(dto.getTerminalId());
                        if (locationByCarPojo != null) {
                            NearbyCarsDto nearbyCarsDto = new NearbyCarsDto();
                            nearbyCarsDto.setCarLon(String.valueOf(lon));
                            nearbyCarsDto.setCarLat(String.valueOf(lat));
                            nearbyCarsDto.setCarNumber(locationByCarPojo.getCarNumber());
                            //String vin = carMap.get(dto.getTerminalId()).getCarVin();
                            String vin = locationByCarPojo.getCarVin();
                            //对VIN进行校验
                            if (StringUtil.isNotEmpty(vin) && (vin.length() <= 8)) {
                                nearbyCarsDto.setChassisNumber(vin);
                            }
                            if (StringUtil.isNotEmpty(vin) && (vin.length() > 8)) {
                                nearbyCarsDto.setChassisNumber(vin.substring(vin.length() - 8));
                            }
                            //拼接车型等信息
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
                //周边车辆信息
                nearbyCarsListDto.setList(nearbyCarsDtoList);
            }

        } else {
            log.info("[ServiceStationLocationService][queryNearbyCars]调用位置云返回失败信息：.....");
            if (requestResult != null) {
                log.error(requestResult.getMessage());
            }
        }
        result.setData(nearbyCarsListDto);
        log.info("[ServiceStationLocationService][queryNearbyCars] end ....");
        return result;
    }
}
