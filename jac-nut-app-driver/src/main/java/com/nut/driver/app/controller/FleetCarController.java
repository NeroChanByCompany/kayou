package com.nut.driver.app.controller;

import com.nut.common.annotation.LoginRequired;
import com.nut.common.annotation.RequestJson;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.driver.app.form.*;
import com.nut.driver.app.service.FleetCarService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class FleetCarController{

    @Autowired
    private FleetCarService fleetCarService;

    /**
     * 车辆与司机绑定接口
     */
    @PostMapping(value = "/car/driver/bind")
    @ApiOperation("司机与车辆绑定")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "teamId" , value = "车队ID" , dataType = "Long"),
            @ApiImplicitParam(name = "multipleFlag" , value = "多选标识" , dataType = "Integer"),
            @ApiImplicitParam(name = "carId" , value = "车辆ID" , dataType = "String"),
            @ApiImplicitParam(name = "masterDriverId" , value = "主驾驶司机ID" , dataType = "String"),
            @ApiImplicitParam(name = "copilotDriverId" , value = "副驾驶用户ID" , dataType = "String")
    })
    @LoginRequired
    public HttpCommandResultWithData carDriverBind(@RequestJson CarDriverBindForm form) {
        log.info("carDriverBind start param:{}",form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = fleetCarService.carDriverBind(form);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("绑定失败，请稍后重试");
            log.error(e.getMessage(), e);
        }
        log.info("carDriverBind end return:{}",result.getData());
        return result;
    }
    /**
     * 车辆与司机解绑接口
     */
    @RequestMapping(value = "/car/driver/unbind")
    @ApiOperation("司机与车辆解绑")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "teamId" , value = "车队ID" , dataType = "Long"),
            @ApiImplicitParam(name = "multipleFlag" , value = "多选标识" , dataType = "Integer"),
            @ApiImplicitParam(name = "carId" , value = "车辆ID" , dataType = "String"),
            @ApiImplicitParam(name = "diverId" , value = "司机ID" , dataType = "String")
    })
    @LoginRequired
    public HttpCommandResultWithData carDriverUnbind(@RequestJson CarDriverUnbindForm form) {
        log.info("carDriverUnbind start param:{}",form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = fleetCarService.carDriverUnbind(form);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("解绑失败，请稍后重试");
            log.error(e.getMessage(), e);
        }
        log.info("carDriverUnbind end return:{}",result.getData());
        return result;
    }
    /**
     * 车队与车辆绑定接口
     */
    @RequestMapping(value = "/fleet/car/bind")
    @ApiOperation("车队与车辆绑定")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "teamId" , value = "车队ID" , dataType = "Long"),
            @ApiImplicitParam(name = "carId" , value = "车辆ID" , dataType = "String")
    })
    @LoginRequired
    public HttpCommandResultWithData fleetCarBind(@RequestJson FleetCarBindForm form) {
        log.info("fleetCarBind start param:{}",form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = fleetCarService.bind(form);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("绑定失败，请稍后重试");
            log.error(e.getMessage(), e);
        }
        log.info("fleetCarBind end return:{}",result.getData());
        return result;
    }
/**
 *  @author wuhaotian 2021/6/28
 */

    /**
     * 车队与车辆解绑接口
     */
    @PostMapping(value = "/fleet/car/unbind")
    @ApiOperation("车队与车辆解绑")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "teamId" , value = "车队ID" , dataType = "Long"),
            @ApiImplicitParam(name = "carId" , value = "车辆ID" , dataType = "String")
    })
    @LoginRequired
    public HttpCommandResultWithData fleetCarUnbind(@RequestJson FleetCarUnbindForm form) {
        log.info("fleetCarUnbind start param:{}",form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = fleetCarService.unbind(form);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("解绑失败，请稍后重试");
            log.error(e.getMessage(), e);
        }
        log.info("fleetCarUnbind end return:{}",result.getData());
        return result;
    }
    /**
     *  @author wuhaotian 2021/6/28
     */
    /**
     * 车队车辆列表接口
     */
    @PostMapping(value = "/fleet/cars")
    @ApiOperation("车队车辆列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "teamId" , value = "车队ID" , dataType = "Long"),
            @ApiImplicitParam(name = "keyword" , value = "关键字" , dataType = "String"),
            @ApiImplicitParam(name = "returnAll" , value = "是否返回全部数据" , dataType = "String"),
            @ApiImplicitParam(name = "exclusiveDriverId" , value = "排除的司机ID" , dataType = "Long")
    })
    @LoginRequired
    public HttpCommandResultWithData fleetCars(@RequestJson FleetCarsForm form) {
        log.info("fleetCars start param:{}",form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = fleetCarService.query(form);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("查询失败，请稍后重试");
            log.error(e.getMessage(), e);
        }
        log.info("fleetCars end return:{}",result.getData());
        return result;
    }
    /**
     *  @author wuhaotian 2021/6/28
     */
    /**
     * 车队车辆详情接口
     */
    @PostMapping(value = "/fleet/carDetail")
    @ApiOperation("车队车辆详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "teamId" , value = "车队ID" , dataType = "Long"),
            @ApiImplicitParam(name = "carId" , value = "车辆ID" , dataType = "String"),
            @ApiImplicitParam(name = "keyword" , value = "关键字" , dataType = "String"),
    })
    @LoginRequired
    public HttpCommandResultWithData fleetCarDetail(@RequestJson FleetCarDetailForm form) {
        log.info("fleetCarDetail start param:{}",form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = fleetCarService.detail(form);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("查询失败，请稍后重试");
            log.error(e.getMessage(), e);
        }
        log.info("fleetCarDetail end return:{}",result.getData());
        return result;
    }

}


