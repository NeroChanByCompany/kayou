package com.nut.truckingteam.app.controller;

import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.PagingInfo;
import com.nut.common.result.Result;
import com.nut.truckingteam.app.dto.CarDto;
import com.nut.truckingteam.app.form.GetDriverCarsForm;
import com.nut.truckingteam.app.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @Description: 根据用户ID 查询 作为司机的车辆和管理的车队的车辆接口（可限定某一级车队范围内）
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.truckingteam.app.controller
 * @Author: yzl
 * @CreateTime: 2021-06-27 13:58
 * @Version: 1.0
 */
@RestController
public class GetDriverCarsController {

    @Autowired
    private TeamService teamService;

    @PostMapping(value = "/getDriverCars")
    public HttpCommandResultWithData getDriverCars(@ApiIgnore @RequestBody GetDriverCarsForm form){
        PagingInfo<CarDto> data = teamService.getDriverCars(form);
        return Result.ok(data);
    }
}
