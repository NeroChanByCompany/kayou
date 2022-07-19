package com.nut.tripanalysis.app.service;

import com.nut.common.base.BaseForm;
import com.nut.truckingteam.app.dto.CarModelAndSeriseDto;
import com.nut.truckingteam.app.dto.CarOilWearDto;

import java.util.List;

/*
 *  @author wuhaotian 2021/7/10
 */
public interface CommonService {

    /**
     * 获取车队下全部车型
     * @param command
     * @return
     */
    public List<CarModelAndSeriseDto> getModelInfoDto(BaseForm command);

    /**
     * 调用接口获取同车型车辆列表
     *
     * @return
     */
    List<CarOilWearDto> callGetTeamCarsOilWear(String modelId, String token);


    Long getUserId(String token);
}
