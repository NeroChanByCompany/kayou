package com.nut.tripanalysis.app.client;

import com.nut.common.annotation.LoginRequired;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.truckingteam.app.form.GetTeamCarsModelAndSeriseForm;
import com.nut.truckingteam.app.form.GetTeamCarsOilWearForm;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/*
 *  @author wuhaotian 2021/7/10
 */
@FeignClient("jac-nut-app-truckingteam")
public interface TruckingteamClient {
    /**
     * 获取同车型车辆列表
     *
     * @param getTeamCarsCommand command
     * @return HttpCommandResultWithData
     */
    @RequestMapping(method = RequestMethod.POST, value = "/getTeamCarsOilWear", consumes = "application/json")
    HttpCommandResultWithData getSameModelCars(@RequestBody GetTeamCarsOilWearForm getTeamCarsCommand);
    /**
     * 获取本车队下全部车型
     *
     * @param getTeamCarsModelAndSeriseCommand command
     * @return HttpCommandResultWithData
     */
    @RequestMapping(method = RequestMethod.POST, value = "/getTeamCarsModelAndSerise", consumes = "application/json")
    HttpCommandResultWithData getTeamCarsModelAndSeriseForm(@RequestBody GetTeamCarsModelAndSeriseForm getTeamCarsModelAndSeriseCommand);
}
