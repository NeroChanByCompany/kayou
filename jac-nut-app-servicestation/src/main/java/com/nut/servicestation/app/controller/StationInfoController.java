package com.nut.servicestation.app.controller;

import com.nut.common.annotation.LoginRequired;
import com.nut.common.annotation.RequestJson;
import com.nut.common.base.BaseController;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.servicestation.app.dto.UserInfoDto;
import com.nut.servicestation.app.form.StationInfoForm;
import com.nut.servicestation.app.service.StationInfoService;
import com.nut.servicestation.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/*
 *  @author wuhaotian 2021/7/8
 */
@Slf4j
@RestController
public class StationInfoController extends BaseController {

    @Autowired
    private UserService userService;
    @Autowired
    private StationInfoService stationInfoService;

    @PostMapping(value = "/station/info")
    @LoginRequired
    public HttpCommandResultWithData info(@RequestJson StationInfoForm command) {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        UserInfoDto userInfoDto = userService.getUserInfoByUserId(command.getUserId(), false);
        if (userInfoDto == null) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("此用户不存在！");
            return result;
        }

        result.setData(stationInfoService.getInfo(Long.parseLong(userInfoDto.getServiceStationId())));
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());
        return result;
    }
    @PostMapping(value = "/station/info/setPic")
    @LoginRequired
    public HttpCommandResultWithData businessHandle(@RequestJson StationInfoForm command) {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        UserInfoDto userInfoDto = userService.getUserInfoByUserId(command.getUserId(), false);
        if (userInfoDto == null) {
            result.setResultCode(ECode.CLIENT_ERROR.code());
            result.setMessage("此用户不存在！");
            return result;
        }

        stationInfoService.setPic(Long.parseLong(userInfoDto.getServiceStationId()), command.getPic());
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());
        return result;
    }
}
