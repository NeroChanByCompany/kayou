package com.nut.tripanalysis.app.service.impl;

import com.nut.common.base.BaseForm;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.utils.JsonUtil;
import com.nut.driver.app.form.TripanalysisQueryModelInfoForm;
import com.nut.tripanalysis.app.client.DriverClient;
import com.nut.tripanalysis.app.service.CommonService;
import com.nut.tripanalysis.app.client.TruckingteamClient;
import com.nut.truckingteam.app.dto.CarModelAndSeriseDto;
import com.nut.truckingteam.app.dto.CarOilWearDto;
import com.nut.truckingteam.app.form.GetTeamCarsModelAndSeriseForm;
import com.nut.truckingteam.app.form.GetTeamCarsOilWearForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
 *  @author wuhaotian 2021/7/10
 */
@Slf4j
@Service("CommonService")
public class CommonServiceImpl implements CommonService {

    @Autowired
    private TruckingteamClient truckingteamClient;

    @Autowired
    private DriverClient driverClient;


    @Override
    public List<CarModelAndSeriseDto> getModelInfoDto(BaseForm command) {
        List<CarModelAndSeriseDto> carModelAndSeriseDto = null;
        try {
            GetTeamCarsModelAndSeriseForm getTeamCarsModelAndSeriseCommand = new GetTeamCarsModelAndSeriseForm();
            getTeamCarsModelAndSeriseCommand.setToken(command.getToken());
            HttpCommandResultWithData httpResult = truckingteamClient.getTeamCarsModelAndSeriseForm(getTeamCarsModelAndSeriseCommand);
            if (httpResult.getResultCode() != ECode.SUCCESS.code()) {
                log.info("调用getTeamCarsModelAndSerise接口返回:" + httpResult.getResultCode() + " " + httpResult.getMessage());
            }
            Object respMap  = httpResult.getData();
            carModelAndSeriseDto = JsonUtil.toList(JsonUtil.toJson(respMap), CarModelAndSeriseDto.class);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("调用getTeamCarsModelAndSerise接口异常:{}", e);
        }
        return carModelAndSeriseDto;
    }

    @Override
    public List<CarOilWearDto> callGetTeamCarsOilWear(String modelId, String token) {
        GetTeamCarsOilWearForm com = new GetTeamCarsOilWearForm();
        com.setToken(token);
        com.setModelId(modelId);
        log.info("调用getTeamCarsOilWear接口参数:[" + com.toString() + "]");
        HttpCommandResultWithData httpResult;
        long sTime = System.currentTimeMillis();
        try {
            httpResult = truckingteamClient.getSameModelCars(com);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("调用getTeamCarsOilWear接口异常:" + e);
            return null;
        } finally {
            long eTime = System.currentTimeMillis();
            log.info("调用getTeamCarsOilWear接口用时:" + (eTime - sTime) + "ms");
        }

        if (httpResult.getResultCode() != ECode.SUCCESS.code()) {
            log.info("调用getTeamCarsOilWear接口返回:" + httpResult.getResultCode() + " " + httpResult.getMessage());
            return null;
        }
        Object respMap =  httpResult.getData();
        List<CarOilWearDto> carOilWearDtos = new ArrayList<>();
        try {
            carOilWearDtos = JsonUtil.toList(JsonUtil.toJson(respMap), CarOilWearDto.class);
        } catch (IOException e) {
            return carOilWearDtos;
        }
        return carOilWearDtos;
    }

    @Override
    public Long getUserId(String token) {
        Long userId = null;
        TripanalysisQueryModelInfoForm driverCommand = new TripanalysisQueryModelInfoForm();
        driverCommand.setToken(token);
        log.info("调用tripanalysisQueryModelInfo接口参数:[" + driverCommand.toString() + "]");
        HttpCommandResultWithData driverResult = driverClient.tripanalysisQueryModelInfo(driverCommand);
        if (driverResult != null && driverResult.getData() != null) {
            userId = Long.valueOf(driverResult.getData().toString());
        }
        return userId;
    }
}
