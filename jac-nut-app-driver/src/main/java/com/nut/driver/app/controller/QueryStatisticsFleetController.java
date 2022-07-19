package com.nut.driver.app.controller;

import com.nut.common.annotation.RequestJson;
import com.nut.driver.app.form.StatisticsFleetLineReportForm;
import com.nut.driver.app.form.StatisticsFleetReportForm;
import com.nut.common.annotation.LoginRequired;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.driver.app.service.StatisticsFleetService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 统计
 */
@RestController
@Slf4j
@Api(tags = "统计")
public class QueryStatisticsFleetController {
    @Autowired
    private StatisticsFleetService statisticsFleetService;
    /**
     * 车辆报表查询
     */
    @PostMapping(value = "/statistics/fleetReport")
    @ApiOperation("车队报表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "beginDate" , value = "统计开始日期" , dataType = "String"),
            @ApiImplicitParam(name = "endDate" , value = "统计结束日期" , dataType = "String"),
            @ApiImplicitParam(name = "fleetName" , value = "名称" , dataType = "String"),
            @ApiImplicitParam(name = "returnAll" , value = "是否返回全部数据" , dataType = "String")
    })
    @LoginRequired
    public HttpCommandResultWithData fleetReport(@RequestJson StatisticsFleetReportForm form) {
        log.info("[QueryStatisticsFleetController][fleetReport] start param：{}",form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = statisticsFleetService.fleetReport(form);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("车队报表查询失败");
            log.error(e.getMessage(), e);
        }
        log.info("[QueryStatisticsFleetController][fleetReport] end return:{}",result.getData());
        return result;
    }

    /**
     * 车辆报表-里程查询
     */
    @PostMapping(value = "/statistics/fleetMileageReport")
    @ApiOperation("车队里程")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "beginDate" , value = "统计开始日期" , dataType = "String"),
            @ApiImplicitParam(name = "endDate" , value = "统计结束日期" , dataType = "String"),
            @ApiImplicitParam(name = "fleetName" , value = "名称" , dataType = "String"),
            @ApiImplicitParam(name = "returnAll" , value = "是否返回全部数据" , dataType = "String")
    })
    @LoginRequired
    public HttpCommandResultWithData fleetMileageReport(@RequestJson StatisticsFleetReportForm form) {
        log.info("[QueryStatisticsFleetController][fleetMileageReport] start param:{}",form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = statisticsFleetService.fleetMileageReport(form);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("车队里程查询失败");
            log.error(e.getMessage(), e);
        }
        log.info("[QueryStatisticsFleetController][fleetMileageReport] end return:{}", result.getData());
        return result;
    }


    /**
     * 车辆报表-里程查询-按线路
     */
    @PostMapping(value = "/statistics/fleetLineMileageReport")
    @ApiOperation("车队里程")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "beginDate" , value = "统计开始日期" , dataType = "String"),
            @ApiImplicitParam(name = "endDate" , value = "统计结束日期" , dataType = "String"),
            @ApiImplicitParam(name = "fleetName" , value = "名称" , dataType = "String"),
            @ApiImplicitParam(name = "returnAll" , value = "是否返回全部数据" , dataType = "String")
    })
    @LoginRequired
    public HttpCommandResultWithData fleetLineMileageReport(@RequestJson StatisticsFleetLineReportForm form) {
        log.info("[QueryStatisticsFleetController][fleetMileageReport] start param:{}",form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = statisticsFleetService.fleetLineMileageReport(form);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("车队里程查询失败");
            log.error(e.getMessage(), e);
        }
        log.info("[QueryStatisticsFleetController][fleetMileageReport] end return:{}", result.getData());
        return result;
    }

    /**
     * 车辆报表-油耗查询
     */
    @PostMapping(value = "/statistics/fleetOilReport")
    @ApiOperation("车队油耗")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "beginDate" , value = "统计开始日期" , dataType = "String"),
            @ApiImplicitParam(name = "endDate" , value = "统计结束日期" , dataType = "String"),
            @ApiImplicitParam(name = "fleetName" , value = "名称" , dataType = "String"),
            @ApiImplicitParam(name = "returnAll" , value = "是否返回全部数据" , dataType = "String")
    })
    @LoginRequired
    public HttpCommandResultWithData fleetOilReport(@RequestJson StatisticsFleetReportForm form) {
        log.info("[QueryStatisticsFleetController][fleetOilReport] start param：{}",form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = statisticsFleetService.fleetOilReport(form);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("车队油耗查询失败");
            log.error(e.getMessage(), e);
        }
        log.info("[QueryStatisticsFleetController][fleetOilReport] end return:{}", result.getData());
        return result;
    }

    /**
     * 车辆报表-行驶时长查询
     */
    @PostMapping(value = "/statistics/fleetTimeReport")
    @ApiOperation("行驶时长")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "beginDate" , value = "统计开始日期" , dataType = "String"),
            @ApiImplicitParam(name = "endDate" , value = "统计结束日期" , dataType = "String"),
            @ApiImplicitParam(name = "fleetName" , value = "名称" , dataType = "String"),
            @ApiImplicitParam(name = "returnAll" , value = "是否返回全部数据" , dataType = "String")
    })
    @LoginRequired
    public HttpCommandResultWithData fleetTimeReport(@RequestJson StatisticsFleetReportForm form) {
        log.info("[QueryStatisticsFleetController][fleetTimeReport] start param:{}",form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = statisticsFleetService.fleetTimeReport(form);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("车队行驶时长查询失败");
            log.error(e.getMessage(), e);
        }
        log.info("[QueryStatisticsFleetController][fleetTimeReport] end return:{}", result.getData());
        return result;
    }

    /**
     * 车辆报表-不良驾驶行为查询
     */
    @PostMapping(value = "/statistics/fleetBadDrivingReport")
    @ApiOperation("不良驾驶行为")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "beginDate" , value = "统计开始日期" , dataType = "String"),
            @ApiImplicitParam(name = "endDate" , value = "统计结束日期" , dataType = "String"),
            @ApiImplicitParam(name = "fleetName" , value = "名称" , dataType = "String"),
            @ApiImplicitParam(name = "returnAll" , value = "是否返回全部数据" , dataType = "String")
    })
    @LoginRequired
    public HttpCommandResultWithData fleetBadDrivingReport(@RequestJson StatisticsFleetReportForm form) {
        log.info("[QueryStatisticsFleetController][fleetBadDrivingReport] start param:{}",form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = statisticsFleetService.fleetBadDrivingReport(form);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("车队不良驾驶行为查询失败");
            log.error(e.getMessage(), e);
        }
        log.info("[QueryStatisticsFleetController][fleetBadDrivingReport] end return:{}",result.getData());
        return result;
    }
}
