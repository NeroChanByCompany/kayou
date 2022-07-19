package com.nut.locationservice.app.service.impl;

import com.google.protobuf.ByteString;
import com.nut.location.protocol.common.LCLocationData;
import com.nut.location.protocol.common.LCStatusType;
import com.nut.location.protocol.common.LCVehicleStatusData;
import com.nut.locationservice.app.dto.CarLocationInputDto;
import com.nut.locationservice.app.dto.CarLocationOutputDto;
import com.nut.locationservice.app.module.LastLocationResponse;
import com.nut.locationservice.app.service.CarLocationService;
import com.nut.locationservice.app.service.CloudService;
import com.nut.locationservice.common.util.CloudUtil;
import com.nut.locationservice.common.util.NumberFormatUtil;
import com.nut.common.utils.JsonUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.app.locationService.service.impl
 * @Author: yzl
 * @CreateTime: 2021-06-16 19:32
 * @Version: 1.0
 */
@Service
@Slf4j
public class CarLocationServiceImpl implements CarLocationService {

    @Autowired
    private CloudService cloudService;

    @SneakyThrows
    public Map<String, CarLocationOutputDto> getCarLocation(List<CarLocationInputDto> carList) {
        log.info("=====CarLocationService getCarLocation start=====");
        Map<String, CarLocationOutputDto> localtionMap = new HashMap<String, CarLocationOutputDto>();
        try {
            log.info("getCarLocation carList:{}", JsonUtil.toJson(carList));
        } catch (Exception e) {
        }
        try {

            List<Long> tcommunctionIds = new ArrayList<Long>();
            StringBuilder strCars = new StringBuilder();
            for (CarLocationInputDto inputDto : carList) {
                if (strCars.length() == 0) {
                    strCars.append("t-").append(inputDto.getTerminalId()).append(",").append("v-").append(inputDto.getVin());
                } else {
                    strCars.append(";").append("t-").append(inputDto.getTerminalId()).append(",").append("v-").append(inputDto.getVin());
                }
                tcommunctionIds.add(inputDto.getTerminalId().longValue());
            }
            log.info("tcommunctionIds:{}",tcommunctionIds);
            List<LastLocationResponse> lastLocationList = cloudService.queryJsonLastLocation(tcommunctionIds);
            log.info("getLastestLocationData return null:{}", lastLocationList == null);
            log.info("getLastestLocationData:{}",lastLocationList);
            // 转换成bean
            if (CollectionUtils.isNotEmpty(lastLocationList)) {
                // 获取瞬时数据信息
                for (LastLocationResponse lastLocation : lastLocationList) {
                    String strTcommunctionId = String.valueOf(lastLocation.getTerminalId());
                    String strVin = this.getVin(strCars.toString(), strTcommunctionId);
                    CarLocationOutputDto dto = new CarLocationOutputDto();
                    dto.setVin(strVin);
                    dto.setLat(lastLocation.getLat() / 1000000D);
                    dto.setLon(lastLocation.getLon() / 1000000D);
                    // 标准里程
                    dto.setMileage(NumberFormatUtil.getDoubleValueData(lastLocation.getSm() / 1D, 2));
                    // 速度
                    dto.setSpeed(NumberFormatUtil.getDoubleValueData(lastLocation.getSpeed() / 1D, 1));
                    log.info("getCarLocation speed:{}", lastLocation.getSpeed());
                    // 获取车辆状态附加信息
                    if (StringUtils.isNotEmpty(lastLocation.getLatLonPB())) {
                        LCLocationData.LocationData locationData = LCLocationData.LocationData.parseFrom(Base64.getDecoder().decode(lastLocation.getLatLonPB()));
                        List<LCVehicleStatusData.VehicleStatusData> vehicleStatusDatas = locationData.getStatusAddition().getStatusList();
                        for (LCVehicleStatusData.VehicleStatusData vehicleStatusData : vehicleStatusDatas) {
                            if (vehicleStatusData.getTypes() == LCStatusType.StatusType.cumulativeRunningTime) {
                                // 发动机累计运行时间（秒）
                                dto.setCumulativeRunningTime(vehicleStatusData.getStatusValue() / 360000);
                                log.info("getCarLocation cumulativeRunningTime:{}", vehicleStatusData.getStatusValue());
                            } else if (vehicleStatusData.getTypes() == LCStatusType.StatusType.rotation) {
                                // 发动机转速
                                dto.setRotation(NumberFormatUtil.formatNumber(vehicleStatusData.getStatusValue() / 100D, 0));
                                log.info("getCarLocation rotation:{}", vehicleStatusData.getStatusValue());
                            } else if (vehicleStatusData.getTypes() == LCStatusType.StatusType.oilValue) {
                                // 燃油液位（百分比）
                                dto.setOilValue(NumberFormatUtil.formatNumber(vehicleStatusData.getStatusValue() / 100D, 0));
                                log.info("getCarLocation oilValue:{}", vehicleStatusData.getStatusValue());
                            } else if (vehicleStatusData.getTypes() == LCStatusType.StatusType.coolingWaterTem) {
                                // 发动机冷却水温度（摄氏度）
                                dto.setCoolingWaterTem(NumberFormatUtil.formatNumber(vehicleStatusData.getStatusValue() / 100D, 1));
                                log.info("getCarLocation coolingWaterTem:{}", vehicleStatusData.getStatusValue());
                            } else if (vehicleStatusData.getTypes() == LCStatusType.StatusType.oilPressure) {
                                // 机油压力（千帕）
                                dto.setOilPressure(NumberFormatUtil.formatNumber(vehicleStatusData.getStatusValue() / 100D, 1));
                                log.info("getCarLocation oilPressure:{}", vehicleStatusData.getStatusValue());
                            } else if (vehicleStatusData.getTypes() == LCStatusType.StatusType.atmosphericTem) {
                                // 大气温度（摄氏度）
                                dto.setAtmosphericTem(NumberFormatUtil.formatNumber(vehicleStatusData.getStatusValue() / 100D, 1));
                                log.info("getCarLocation atmosphericTem:{}", vehicleStatusData.getStatusValue());
                            }
                        }
                        dto.setStatus(getStatus(locationData));
                    }

                    dto.setStandardFuelCon(Double.parseDouble(String.valueOf(lastLocation.getSm())));

                    dto.setUpTime(lastLocation.getGpsDate());
                    dto.setAveMileage(dto.getStandardFuelCon() / dto.getMileage());
                    dto.setAvgOil(lastLocation.getAvgOil());
                    dto.setAveMileage(dto.getStandardFuelCon() / dto.getMileage());
                    //dto.setMileageEngine(lastLocation.getMileageEngine());
                    dto.setMileageEngine(Double.valueOf(lastLocation.getSm()));
                    dto.setUreaLevel(lastLocation.getUreaLevel());
                    localtionMap.put(strVin, dto);
                    log.info("add localtionMap key = {}, value = {}", strVin, dto);
                }
            }
        } catch (Exception e) {
            log.error("位置云接口调用异常" + e.getMessage());
        }
        log.info("=====CarLocationService getCarLocation end=====");
        return localtionMap;
    }

    /**
     * 获取vin
     *
     * @param allStr
     * @param strTcommunctionId
     * @return
     */
    private String getVin(String allStr, String strTcommunctionId) {
        strTcommunctionId = "t-" + strTcommunctionId;
        try {
            String temp = allStr.substring(allStr.indexOf(strTcommunctionId));
            temp = temp.split(";")[0];
            return temp.split(",")[1].replace("v-", "");
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获取车辆状态（车门+acc+报警+定位）
     */
    private String getStatus(LCLocationData.LocationData locationData) {
        String alarmaddition = "";
        if (locationData.getAdditionAlarm().size() > 7) {
            ByteString additionalarm = locationData.getAdditionAlarm();
            alarmaddition = CloudUtil.getAdditinAlarm(additionalarm.byteAt(5), additionalarm.byteAt(6), additionalarm.byteAt(7));
        }
        return CloudUtil.getDoorStatus(locationData.getStatus()) + CloudUtil.getAccStatus(locationData.getStatus())
                + alarmaddition + CloudUtil.getGpsStatus(locationData.getStatus());

    }

}
