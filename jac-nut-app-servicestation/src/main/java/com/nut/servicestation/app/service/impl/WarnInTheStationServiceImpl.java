package com.nut.servicestation.app.service.impl;

import com.nut.common.constant.ServiceStationVal;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.utils.JsonUtil;
import com.nut.common.utils.StringUtil;
import com.nut.locationservice.app.dto.CarLocationToStationDto;
import com.nut.locationservice.app.form.GetCarLastLocationForm;
import com.nut.servicestation.app.client.LocationServiceClient;
import com.nut.servicestation.app.dao.CarStationStayOvertimeDao;
import com.nut.servicestation.app.domain.CarStationStayOvertime;
import com.nut.servicestation.app.dto.StationStayWarnCarDto;
import com.nut.servicestation.app.dto.StationStayWarnCarsDto;
import com.nut.servicestation.app.dto.UserInfoDto;
import com.nut.servicestation.app.form.QueryStationStayWarnCarsForm;
import com.nut.servicestation.app.pojo.CarWarnPojo;
import com.nut.servicestation.app.service.UserService;
import com.nut.servicestation.app.service.WarnInTheStationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/*
 *  @author wuhaotian 2021/7/5
 */
@Slf4j
@Service("WarnInTheStationService")
public class WarnInTheStationServiceImpl implements WarnInTheStationService {


    @Autowired
    private CarStationStayOvertimeDao carStationStayOvertimeMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private LocationServiceClient locationServiceClient;
    @Value("${SERVICE_STATION_RADIUS:200}")
    private String serviceStationRadius;



    @Override
    public String updWarningStatus(String carId, String staCode, int status) {
        log.info("[WarnInTheStationService][updWarningStatus] update params: carId : {} || staCode : {} || status : {}", carId, staCode, status);
        CarStationStayOvertime csso = carStationStayOvertimeMapper.queryWarningData(carId, staCode);

        if (csso == null) {
            log.info("[WarnInTheStationService][updWarningStatus] This csso is null");
            return "????????????????????????????????????";
        }
        if (csso.getStatus() != ServiceStationVal.INQ_UNDONE) {
            log.info("[WarnInTheStationService][updWarningStatus] This status is not 0");
            return "?????????????????????????????????????????????";
        }
        CarStationStayOvertime updCsso = new CarStationStayOvertime();
        updCsso.setId(csso.getId());
        updCsso.setStatus(status);
        Date nowDate = new Date();
        updCsso.setReleaseTime(nowDate);
        updCsso.setUpdateTime(nowDate);
        carStationStayOvertimeMapper.updateByPrimaryKeySelective(updCsso);
        log.info("[WarnInTheStationService][updWarningStatus]: id:{}", csso.getCarId());
        return "";
    }

    @Override
    public HttpCommandResultWithData queryStationStayWarnCars(QueryStationStayWarnCarsForm command) throws Exception {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());

        StationStayWarnCarsDto carsDto = new StationStayWarnCarsDto();

        // ?????????????????????
        UserInfoDto userInfo = userService.getUserInfoByUserId(command.getUserId(), false);
        if (userInfo == null) {
            result.setMessage("??????????????????!");
            result.setResultCode(ECode.CLIENT_ERROR.code());
            return result;
        }
        carsDto.setStationName(userInfo.getServiceStationName());
        carsDto.setStationLat(userInfo.getServiceStationLat());
        carsDto.setStationLon(userInfo.getServiceStationLon());
        if (StringUtil.isEmpty(userInfo.getStationRadius())) {
            carsDto.setStationRadius(serviceStationRadius);
        } else {
            carsDto.setStationRadius(userInfo.getStationRadius());
        }

        // ??????????????????????????????
        List<CarWarnPojo> warnCarList = carStationStayOvertimeMapper.queryStationStayWarnCar(userInfo.getServiceCode());

        if (warnCarList.size() == 0) {
            log.info("[WarnInTheStationService][queryStationStayWarnCars] this warnCarList is null");
            carsDto.setList(new ArrayList<>());
            carsDto.setTotal(0);
            result.setData(carsDto);
            return result;
        }
        //?????????????????????MAP???????????????
        List<StationStayWarnCarDto> returnCarList = new ArrayList<>();

        //???????????????????????????
        GetCarLastLocationForm requestCommand = new GetCarLastLocationForm();
        requestCommand.setTerminalIdList(idsListForWarn(warnCarList, returnCarList));
        log.info("[WarnInTheStationService][queryStationStayWarnCars]????????????????????????" + JsonUtil.toJson(requestCommand));
        carsDto.setTotal(returnCarList.size());
        // ???????????????????????????????????????????????????
        HttpCommandResultWithData requestResult = locationServiceClient.getCarLastLocationToStation(requestCommand);

        if (requestResult != null && ECode.SUCCESS.code() == requestResult.getResultCode()) {
            log.info("[ServiceStationLocationService][queryNearbyCars]????????????????????????" + JsonUtil.toJson(requestResult.getData()));
            Map<String, CarLocationToStationDto> map = (Map<String, CarLocationToStationDto>) requestResult.getData();

            if (map != null) {
                for (StationStayWarnCarDto dto : returnCarList) {
                    CarLocationToStationDto carLocationToStationDto = JsonUtil.fromJson(JsonUtil.toJson(map.get(dto.getAutoTerminal())), CarLocationToStationDto.class);
                    if (carLocationToStationDto != null) {
                        dto.setCarLat(String.valueOf(carLocationToStationDto.getLat()));
                        dto.setCarLon(String.valueOf(carLocationToStationDto.getLon()));
                    }
                }
                carsDto.setList(returnCarList);
            } else {
                log.info("[WarnInTheStationService][queryStationStayWarnCars]????????????????????????????????????.....");
                log.error(requestResult.getMessage());
            }
        }
        result.setData(carsDto);
        return result;
    }
    /**
     * ??????????????????????????????
     *
     * @param carList       ??????????????????
     * @param returnCarList ??????????????????
     * @return reList ???????????????
     */
    private List<Long> idsListForWarn(List<CarWarnPojo> carList, List<StationStayWarnCarDto> returnCarList) {
        List<Long> reList = new ArrayList<>();
        StationStayWarnCarDto dto;
        for (CarWarnPojo pojo : carList) {
            dto = new StationStayWarnCarDto();
            if (StringUtil.isNotEmpty(pojo.getAutoTerminal())) {
                reList.add(Long.valueOf(pojo.getAutoTerminal()));

                dto.setCarId(pojo.getCarId());
                dto.setAutoTerminal(pojo.getAutoTerminal());
                dto.setCarNumber(pojo.getCarNumber());
                dto.setWithWork(pojo.getWithWork());
                String vin = pojo.getCarVin();
                //???VIN????????????
                if (StringUtil.isNotEmpty(vin) && (vin.length() <= 8)) {
                    dto.setChassisNumber(vin);
                }
                if (StringUtil.isNotEmpty(vin) && (vin.length() > 8)) {
                    dto.setChassisNumber(vin.substring(vin.length() - 8));
                }
                //?????????????????????
                StringBuilder stringBuilder = new StringBuilder();
                if (StringUtil.isNotEmpty(pojo.getCarSeriesName())) {
                    stringBuilder.append(pojo.getCarSeriesName());
                }
                if (StringUtil.isNotEmpty(pojo.getEngine())) {
                    stringBuilder.append("/").append(pojo.getEngine());
                }
                if (StringUtil.isNotEmpty(pojo.getCarModelName())) {
                    stringBuilder.append(" ").append(pojo.getCarModelName());
                }
                dto.setCarModel(stringBuilder.toString());
                //??????????????????????????????????????????APP????????????????????????????????????????????????????????????????????????2?????????
                String phoneNumbers = pojo.getPhoneNumbers();
                if (StringUtil.isNotEmpty(phoneNumbers)) {
                    if (phoneNumbers.length() > 23) {
                        dto.setPhoneNumbers(phoneNumbers.substring(0, 23).replace(",", " / "));
                    } else {
                        dto.setPhoneNumbers(phoneNumbers.replace(",", " / "));
                    }
                }
                dto.setWarnLevel(pojo.getWarnLevel());
                dto.setCarLon("0");
                dto.setCarLat("0");
                returnCarList.add(dto);
            }
        }
        return reList;
    }
}
