package com.nut.driver.app.controller;

import com.nut.common.annotation.LoginRequired;
import com.nut.common.annotation.RequestJson;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.driver.app.form.*;
import com.nut.driver.app.service.FleetService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class FleetController {

    @Autowired
    private FleetService fleetService;

    /**
     * 用户车队列表接口
     */
    @PostMapping(value = "/user/fleets")
    @ApiOperation("车队列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name" , value = "车队名称" , dataType = "String"),
            @ApiImplicitParam(name = "returnAll" , value = "是否返回全部数据" , dataType = "String")
    })
    @LoginRequired
    public HttpCommandResultWithData userFleets(@RequestJson UserFleetsForm form) {
        log.info("userFleets start param:{}",form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = fleetService.query(form);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("查询失败，请稍后重试");
            log.error(e.getMessage(), e);
        }
        log.info("userFleets end result:{}",result.getData());
        return result;
    }
    /**
     * 用户车队详情接口
     */
    @PostMapping(value = "/user/fleetDetail")
    @ApiOperation("车队详情")
    @ApiImplicitParam(name = "teamId" , value = "车队ID" , dataType = "Long")
    @LoginRequired
    public HttpCommandResultWithData userFleetDetail(@RequestJson UserFleetDetailForm form) {
        log.info("userFleetDetail start param:{}",form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = fleetService.detail(form);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("查询失败，请稍后重试");
            log.error(e.getMessage(), e);
        }
        log.info("userFleetDetail end return:{}",result.getData());
        return result;
    }
    /**
     * 用户编辑车队接口
     */
    @PostMapping(value = "/user/fleet/update")
    @ApiOperation("编辑车队")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "teamId" , value = "车队ID" , dataType = "Long"),
            @ApiImplicitParam(name = "name" , value = "车队名称" , dataType = "String"),
            @ApiImplicitParam(name = "avatar" , value = "车队头像" , dataType = "String"),

    })
    @LoginRequired
    public HttpCommandResultWithData userFleetUpdate(@RequestJson UserFleetUpdateForm form) {
        log.info("userFleetUpdate start param:{}",form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = fleetService.update(form);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("更新失败，请稍后重试");
            log.error(e.getMessage(), e);
        }
        log.info("userFleetUpdate end return:{}",result.getData());
        return result;
    }
    /**
     * 用户创建车队接口
     */
    @PostMapping(value = "/user/fleet/add")
    @ApiOperation("创建车队")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name" , value = "车队名称" , dataType = "String"),
            @ApiImplicitParam(name = "avatar" , value = "车队头像" , dataType = "String")
    })
    @LoginRequired
    public HttpCommandResultWithData userFleetAdd(@RequestJson UserFleetAddForm form) {
        log.info("userFleetAdd start param:{}",form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = fleetService.add(form);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("提交失败，请稍后重试");
            log.error(e.getMessage(), e);
        }
        log.info("userFleetAdd end return:{}",result.getData());
        return result;
    }
    /**
     * 用户删除车队接口
     */
    @PostMapping(value = "/user/fleet/delete")
    @ApiOperation("删除车队")
    @ApiImplicitParam(name = "teamId" , value = "车队ID" , dataType = "Long")
    @LoginRequired
    public HttpCommandResultWithData userFleetDelete(@RequestJson UserFleetDeleteForm form) {
        log.info("userFleetDelete start param:{}",form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = fleetService.delete(form);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("删除失败，请稍后重试");
            log.error(e.getMessage(), e);
        }
        log.info("userFleetDelete end return:{}",form);
        return result;
    }
}
