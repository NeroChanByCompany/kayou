package com.nut.servicestation.app.controller;

import com.nut.common.annotation.LoginRequired;
import com.nut.common.annotation.RequestJson;
import com.nut.common.base.BaseController;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.servicestation.app.form.AcceptVinForm;
import com.nut.servicestation.app.form.CouponExchangeForm;
import com.nut.servicestation.app.service.FirstConfirmedService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/*
 *  @author wuhaotian 2021/7/7
 */
@Slf4j
@RestController
public class FirstConFirmedController extends BaseController {

    @Autowired
    private FirstConfirmedService firstConfirmedService;

    /**
     * 前台传递过来VIN
     */
    @PostMapping(value = "/firstConfirmed")
    @LoginRequired
    public HttpCommandResultWithData businessHandle(@RequestJson AcceptVinForm command) throws Exception {
        log.info("从前台获取到车辆底盘号VIN：{}", command.getCarVin());
        this.formValidate(command);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result = firstConfirmedService.qualificationTest(command);
        return result;
    }
    /**
     * 核销首保券
     */
    @PostMapping(value = "/firstExchange")
    @LoginRequired
    public HttpCommandResultWithData exchange(@RequestJson CouponExchangeForm command) throws Exception {
        log.info("从前台获取到首保券Id：{}", command.getCumId());
        this.formValidate(command);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result=firstConfirmedService.firstExchange(command);
        return result;
    }
}
