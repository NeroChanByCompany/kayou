package com.nut.truckingteam.app.controller;

import com.nut.common.annotation.LoginRequired;
import com.nut.common.base.BaseController;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.truckingteam.app.dto.CarModelAndSeriseDto;
import com.nut.truckingteam.app.form.GetTeamCarsModelAndSeriseForm;
import com.nut.truckingteam.app.service.TeamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/*
 *  @author wuhaotian 2021/7/10
 */
@Slf4j
@RestController
public class GetTeamCarsController extends BaseController {

    @Autowired
    private TeamService teamService;

    @RequestMapping(value = "/getTeamCarsModelAndSerise")
    @LoginRequired
    public HttpCommandResultWithData getTeamCarsModelAndSerise(@RequestBody GetTeamCarsModelAndSeriseForm command) {
        HttpCommandResultWithData<List<CarModelAndSeriseDto>> result = new HttpCommandResultWithData<>();
        List<CarModelAndSeriseDto> data = teamService.getTeamCarsModelAndSerise(command);
        result.setData(data);
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());
        return result;
    }
}
