package com.nut.truckingteam.app.service;

import com.nut.common.result.HttpCommandResultWithData;
import com.nut.truckingteam.app.form.GetTeamCarsOilWearForm;

/*
 *  @author wuhaotian 2021/7/10
 */
public interface TripAnalysisService {

    /**
     * 查询用户所有车辆（按车型）
     */
    HttpCommandResultWithData getTeamCarsOilWear(GetTeamCarsOilWearForm command);
}
