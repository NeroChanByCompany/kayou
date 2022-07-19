package com.nut.driver.app.controller;

import com.nut.common.annotation.RequestJson;
import com.nut.common.result.Result;
import com.nut.driver.app.form.LineForm;
import com.nut.driver.app.service.LineService;
import com.nut.common.annotation.LoginRequired;
import com.nut.common.base.BaseController;
import com.nut.common.result.HttpCommandResultWithData;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 线路接口
 */
@RestController
@Slf4j
@RequestMapping("/")
public class LineController extends BaseController {

    @Autowired
    private LineService lineService;

    /**
     * 增加线路
     */
    @SneakyThrows
    @PostMapping(value = "/line/add")
    @ApiOperation("创建线路")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "线路ID", dataType = "String"),
            @ApiImplicitParam(name = "startCityName", value = "起点城市", dataType = "String"),
            @ApiImplicitParam(name = "endCityName", value = "终点城市", dataType = "String"),
            @ApiImplicitParam(name = "fleetId", value = "车队ID", dataType = "String")
    })
    @LoginRequired
    public HttpCommandResultWithData addLine(@RequestJson LineForm form) {
        log.info("addLine start param:{}", form);
        log.info("[addLine]start");
        this.formValidate(form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result = lineService.addLine(form);
        log.info("addLine end return:{}",result.getData());
        return result;
    }

    /**
     * 查询线路
     */
    @SneakyThrows
    @PostMapping(value = "/line/list")
    @ApiOperation("线路列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "线路ID", dataType = "String"),
            @ApiImplicitParam(name = "startCityName", value = "起点城市", dataType = "String"),
            @ApiImplicitParam(name = "endCityName", value = "终点城市", dataType = "String"),
            @ApiImplicitParam(name = "fleetId", value = "车队ID", dataType = "String")
    })
    @LoginRequired
    public HttpCommandResultWithData list(@RequestJson LineForm form) {
        log.info("list start param",form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result = lineService.list();
        log.info("list end return:{}",result.getData());
        return result;
    }

    /**
     * 删除线路
     */
    @SneakyThrows
    @PostMapping(value = "/line/delete")
    @ApiOperation("删除线路")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "线路ID", dataType = "String"),
            @ApiImplicitParam(name = "startCityName", value = "起点城市", dataType = "String"),
            @ApiImplicitParam(name = "endCityName", value = "终点城市", dataType = "String"),
            @ApiImplicitParam(name = "fleetId", value = "车队ID", dataType = "String")
    })
    @LoginRequired
    public HttpCommandResultWithData deleteLine(@RequestJson LineForm form) {
        log.info("deleteLine start param:{}",form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result = lineService.deleteLine(form);
        log.info("deleteLine end return:{}",result.getData());
        return result;
    }

    /**
     * 更新线路
     */
    @SneakyThrows
    @PostMapping(value = "/line/update")
    @ApiOperation("修改线路")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "线路ID", dataType = "String"),
            @ApiImplicitParam(name = "startCityName", value = "起点城市", dataType = "String"),
            @ApiImplicitParam(name = "endCityName", value = "终点城市", dataType = "String"),
            @ApiImplicitParam(name = "fleetId", value = "车队ID", dataType = "String")
    })
    @LoginRequired
    public HttpCommandResultWithData updateLine(@RequestJson LineForm form) {
        log.info("updateLine start param:{}",form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result = lineService.updateLine(form);
        log.info("updateLine end return:{}",result.getData());
        return result;
    }

    /**
     * 查询线路详情
     */
    @SneakyThrows
    @PostMapping(value = "/line/detail")
    @ApiOperation("查询线路详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "线路ID", dataType = "String"),
            @ApiImplicitParam(name = "startCityName", value = "起点城市", dataType = "String"),
            @ApiImplicitParam(name = "endCityName", value = "终点城市", dataType = "String"),
            @ApiImplicitParam(name = "fleetId", value = "车队ID", dataType = "String")
    })
    @LoginRequired
    public HttpCommandResultWithData lineDetail(@RequestJson LineForm form) {
        log.info("detail start param:{}",form);
        this.formValidate(form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result = lineService.detail(form);
        log.info("detail end return:{}",result.getData());
        return result;
    }


    /**
     * @Author liuBing
     * @Description //TODO 查询用户或车队有关的线路
     * @Date 15:01 2021/7/26
     * @Param [form]
     * @return com.nut.common.result.HttpCommandResultWithData
     **/
    @SneakyThrows
    @PostMapping(value = "/line/userList")
    @ApiOperation("线路列表")
    @ApiImplicitParams({})
    @LoginRequired
    public HttpCommandResultWithData userList(LineForm form) {
        log.info("userList start param:{}","");
        return Result.ok(lineService.userList(form));
    }
}
