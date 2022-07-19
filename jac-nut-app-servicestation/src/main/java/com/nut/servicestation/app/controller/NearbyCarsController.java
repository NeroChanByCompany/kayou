package com.nut.servicestation.app.controller;

import com.nut.common.annotation.LoginRequired;
import com.nut.common.annotation.RequestJson;
import com.nut.common.base.BaseController;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.servicestation.app.form.NearbyCarsForm;
import com.nut.servicestation.app.service.ServiceStationLocationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/*
 *  @author wuhaotian 2021/7/7
 */
@Slf4j
@RestController
public class NearbyCarsController extends BaseController {

    @Autowired
    private ServiceStationLocationService serviceStationLocationService;

    @PostMapping(value = "/nearbyCars")
    @LoginRequired
    public HttpCommandResultWithData businessHandle(@RequestJson NearbyCarsForm command) throws Exception {
        log.info("[NearbyCarsController] start -------");
        this.formValidate(command);
        HttpCommandResultWithData result = new HttpCommandResultWithData();

        try {
            result = serviceStationLocationService.queryNearbyCars(command);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("附近车辆查询失败!");
            log.error(e.getMessage(), e);
        }
        log.info("[NearbyCarsController] end -------");
        return result;
    }
}
