package com.nut.driver.app.controller;

import com.nut.common.annotation.RequestJson;
import com.nut.common.base.BaseController;
import com.nut.driver.app.form.StatisticsCarLinReportForm;
import com.nut.driver.app.form.StatisticsCarReportForm;
import com.nut.driver.app.service.StatisticsService;
import com.nut.common.annotation.LoginRequired;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;

/**
 * @author liuBing
 *
 * @Description TODO 统计接口
 * @Date 2021/6/19 17:08
 */
@Api(tags = "统计接口")
//@RequestMapping("/statistics")
@RestController
@Slf4j
public class StatisticsController extends BaseController {
    /**
     * 服务接口
     */
    @Resource
    private StatisticsService statisticsService;

    /**
     * @Author liuBing
     * @Description //TODO 车辆报表查询 按车辆
     * @Date 17:31 2021/6/19
     * @Param [command]
     * @return com.nut.driver.common.result.HttpCommandResultWithData
     **/
    @ApiOperation(value = "车辆报表查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phone",value = "手机号",dataType = "String"),
            @ApiImplicitParam(name = "type",value = "type类型",dataType = "String"),
    })
    @PostMapping(value = "/carReport")
    @LoginRequired()
    public HttpCommandResultWithData carReport(@ApiIgnore @RequestJson StatisticsCarReportForm form) {
        log.info("carReport start param:{}",form);
        return Result.ok(statisticsService.carReport(form));
    }



    /**
     * @Author liuBing
     * @Description //TODO 车辆报表查询 按线路
     * @Date 17:31 2021/6/19
     * @Param [command]
     * @return com.nut.driver.common.result.HttpCommandResultWithData
     **/
    @ApiOperation(value = "车辆报表查询-按线路")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "beginDate",value = "统计开始日期",dataType = "String"),
            @ApiImplicitParam(name = "endDate",value = "统计结束日期",dataType = "String"),
            @ApiImplicitParam(name = "fleetId",value = "车队id",dataType = "String"),
            @ApiImplicitParam(name = "carids",value = "车辆id",dataType = "String"),
    })
    @PostMapping(value = "/carLineReport")
    @LoginRequired()
    public HttpCommandResultWithData carLineReport(@ApiIgnore @RequestBody StatisticsCarLinReportForm form) throws Exception {
        log.info("carReport start param:{}",form);
        this.formValidate(form);
        return Result.ok(statisticsService.carLineReport(form));
    }
}
