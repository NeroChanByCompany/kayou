package com.nut.truckingteam.app.service;

import com.nut.common.result.PagingInfo;
import com.nut.truckingteam.app.dto.CarDto;
import com.nut.truckingteam.app.dto.CarModelAndSeriseDto;
import com.nut.truckingteam.app.form.GetDriverCarsForm;
import com.nut.truckingteam.app.form.GetTeamCarsModelAndSeriseForm;

import java.util.List;

/**
 * @Description: 人车关系基础服务
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.truckingteam.app.service
 * @Author: yzl
 * @CreateTime: 2021-06-27 13:59
 * @Version: 1.0
 */
public interface TeamService {

    PagingInfo<CarDto> getDriverCars(GetDriverCarsForm form);

    List<CarModelAndSeriseDto> getTeamCarsModelAndSerise(GetTeamCarsModelAndSeriseForm command);
}
