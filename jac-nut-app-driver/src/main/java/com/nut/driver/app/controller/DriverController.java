package com.nut.driver.app.controller;

import com.nut.common.annotation.LoginRequired;
import com.nut.common.annotation.RequestJson;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.driver.app.form.*;
import com.nut.driver.app.service.DriverService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@Api(value = "driver接口")
public class DriverController {

    @Autowired
    private DriverService driverService;

    /**
     * 车队司机详情接口
     */
    @PostMapping(value = "/fleet/driverDetail")
    @ApiOperation("查询司机详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "teamId" , value = "车队ID" , dataType = "Long"),
            @ApiImplicitParam(name = "driverId" , value = "司机ID" , dataType = "Long")
    })
    @LoginRequired
    public HttpCommandResultWithData fleetDriverDetail(@RequestJson FleetDriverDetailForm form) {
        log.info("fleetDriverDetail start param:{}",form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = driverService.detail(form);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("查询失败，请稍后重试");
            log.error(e.getMessage(), e);
        }
        log.info("fleetDriverDetail end return:{}",result.getData());
        return result;
    }
    /**
     * 车队与司机绑定接口
     */
    @PostMapping(value = "/fleet/driver/bind")
    @ApiOperation("车队与司机绑定")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "teamId" , value = "车队ID" , dataType = "Long"),
            @ApiImplicitParam(name = "driverId" , value = "司机ID" , dataType = "Long")
    })
    @LoginRequired
    public HttpCommandResultWithData fleetDriverBind(@RequestJson FleetDriverBindForm form) {
        log.info("fleetDriverBind start param:{}",form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = driverService.bind(form);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("绑定失败，请稍后重试");
            log.error(e.getMessage(), e);
        }
        log.info("fleetDriverBind end return:{}",result.getData());
        return result;
    }
    /**
     * 车队与司机解绑接口
     */
    @PostMapping(value = "/fleet/driver/unbind")
    @ApiOperation("车队与司机解绑")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "teamId" , value = "车队ID" , dataType = "Long"),
            @ApiImplicitParam(name = "driverId" , value = "司机ID" , dataType = "Long")
    })
    @LoginRequired
    public HttpCommandResultWithData fleetDriverUnbind(@RequestJson FleetDriverUnbindForm form) {
        log.info("fleetDriverUnbind start param:{}",form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = driverService.unbind(form);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("解绑失败，请稍后重试");
            log.error(e.getMessage(), e);
        }
        log.info("fleetDriverUnbind end return:{}",result.getData());
        return result;
    }
    /**
     * 司机退出车队接口
     */
    @PostMapping(value = "/fleet/driver/quit")
    @ApiOperation("司机退出车队")
    @ApiImplicitParam(name = "teamId" , value = "车队ID" , dataType = "Long")
    @LoginRequired
    public HttpCommandResultWithData fleetDriverQuit(@RequestJson FleetDriverQuitForm form) {
        log.info("fleetDriverQuit start param:{}",form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = driverService.quit(form);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("提交失败，请稍后重试");
            log.error(e.getMessage(), e);
        }
        log.info("fleetDriverQuit end return:{}",result.getData());
        return result;
    }
    /**
     *  @author wuhaotian 2021/6/28
     * 车队司机列表接口
     */
    @PostMapping(value = "/fleet/drivers")
    @ApiOperation("车队司机列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "teamId" , value = "车队ID" , dataType = "Long"),
            @ApiImplicitParam(name = "keyword" , value = "关键字" , dataType = "String"),
            @ApiImplicitParam(name = "returnAll" , value = "是否返回全部数据" , dataType = "String"),
            @ApiImplicitParam(name = "exclusiveCarId" , value = "排除的车辆ID" , dataType = "String")
    })
    @LoginRequired
    public HttpCommandResultWithData fleetDrivers(@RequestJson FleetDriversForm form) {
        log.info("fleetDrivers start param:{}",form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = driverService.query(form);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("查询失败，请稍后重试");
            log.error(e.getMessage(), e);
        }
        log.info("fleetDrivers end return:{}",result.getData());
        return result;
    }
}
