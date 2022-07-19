package com.nut.driver.app.controller;

/*
 *  @author wuhaotian 2021/6/28
 */

import com.nut.common.annotation.LoginRequired;
import com.nut.common.annotation.RequestJson;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.driver.app.form.*;
import com.nut.driver.app.service.StatisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 *  @author wuhaotian 2021/6/29
 */
@Slf4j
@RestController
@Api(tags = "报表查询")
public class QueryStatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    /**
     * 车辆报表查询
     */
    @PostMapping(value = "/statistics/carReport")
    @ApiOperation("车辆报表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "beginDate" , value = "统计开始日期" , dataType = "String"),
            @ApiImplicitParam(name = "endDate" , value = "统计结束日期" , dataType = "String")
    })
    @LoginRequired
    public HttpCommandResultWithData carReport(@RequestJson StatisticsCarReportForm form) {
        log.info("[QueryStatisticsController][carReport] start param：{}",form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = statisticsService.carReport(form);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("车辆报表查询失败");
            log.error(e.getMessage(), e);
        }
        log.info("[QueryStatisticsController][carReport] end return:{}",result.getData());
        return result;
    }
    /**
     * 车辆报表-行驶里程-车辆列表
     */
    @PostMapping(value = "/statistics/carReportMileageCarList")
    @ApiOperation("车辆报表-行驶里程-详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "beginDate" , value = "统计开始日期" , dataType = "String"),
            @ApiImplicitParam(name = "endDate" , value = "统计结束日期" , dataType = "String"),
            @ApiImplicitParam(name = "keyword" , value = "关键字" , dataType = "String"),
            @ApiImplicitParam(name = "returnAll" , value = "是否返回全部数据" , dataType = "String")
    })
    @LoginRequired
    public HttpCommandResultWithData carReportMileageCarList(@RequestJson StatisticsMileageCarListForm form) {
        log.info("[QueryStatisticsController][carReportMileageCarList] start param:{}",form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = statisticsService.carReportMileageCarList(form);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("行驶里程车辆列表失败");
            log.error(e.getMessage(), e);
        }
        log.info("[QueryStatisticsController][carReportMileageCarList] end return:{}",result.getData());
        return result;
    }


    /**
     * 车辆报表-行驶里程-车辆列表
     */
    @PostMapping(value = "/statistics/carReportLinMileageCarList")
    @ApiOperation("车辆报表-行驶里程-详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "beginDate" , value = "统计开始日期" , dataType = "String"),
            @ApiImplicitParam(name = "endDate" , value = "统计结束日期" , dataType = "String"),
            @ApiImplicitParam(name = "keyword" , value = "关键字" , dataType = "String"),
            @ApiImplicitParam(name = "returnAll" , value = "是否返回全部数据" , dataType = "String")
    })
    @LoginRequired
    public HttpCommandResultWithData carReportLinMileageCarList(@RequestJson StatisticsLineMileageCarListForm form) {
        log.info("[QueryStatisticsController][carReportMileageCarList] start param:{}",form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = statisticsService.carReportLinMileageCarList(form);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("行驶里程车辆列表失败");
            log.error(e.getMessage(), e);
        }
        log.info("[QueryStatisticsController][carReportMileageCarList] end return:{}",result.getData());
        return result;
    }


    /**
     * 车辆报表-行驶里程-图表接口
     */
    @PostMapping(value = "/statistics/carReportMileageChart")
    @ApiOperation("车辆报表-行驶里程-详情-图表接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "beginDate" , value = "统计开始日期" , dataType = "String"),
            @ApiImplicitParam(name = "endDate" , value = "统计结束日期" , dataType = "String"),
            @ApiImplicitParam(name = "chartType" , value = "区分标志" , dataType = "String"),
            @ApiImplicitParam(name = "carId" , value = "车辆ID" , dataType = "String")
    })
    @LoginRequired
    public HttpCommandResultWithData carReportMileageChart(@RequestJson StatisticsCarReportChartForm form) {
        log.info("[QueryStatisticsController][carReportMileageChart] start param:{}",form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = statisticsService.carReportMileageChart(form);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("行驶里程图表查询失败");
            log.error(e.getMessage(), e);
        }
        log.info("[QueryStatisticsController][carReportMileageChart] end return:{}",result.getData());
        return result;
    }
    /**
     * 车辆报表-车辆里程明细
     */
    @PostMapping(value = "/statistics/carReportDetailedMileageList")
    @ApiOperation("车辆报表-行驶里程-详情-车辆里程明细")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "beginDate" , value = "统计开始日期" , dataType = "String"),
            @ApiImplicitParam(name = "endDate" , value = "统计结束日期" , dataType = "String"),
            @ApiImplicitParam(name = "carId" , value = "车辆ID" , dataType = "String")
    })
    @LoginRequired
    public HttpCommandResultWithData carReportDetailedMileageList(@RequestJson StatisticsMileageCarDetailForm form) {
        log.info("[QueryStatisticsController][carReportDetailedMileageList] start param:{}",form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = statisticsService.carReportDetailedMileageList(form);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("车辆行驶里程明细列表查询失败");
            log.error(e.getMessage(), e);
        }
        log.info("[QueryStatisticsController][carReportDetailedMileageList] end return:{}",result.getData());
        return result;
    }
    /**
     * 车辆报表-总油耗-图表接口
     */
    @PostMapping(value = "/statistics/carReportOilChart")
    @ApiOperation("车辆报表-总油耗-详情-1")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "beginDate" , value = "统计开始日期" , dataType = "String"),
            @ApiImplicitParam(name = "endDate" , value = "统计结束日期" , dataType = "String"),
            @ApiImplicitParam(name = "chartType" , value = "区分标志" , dataType = "String"),
            @ApiImplicitParam(name = "carId" , value = "车辆ID" , dataType = "String")
    })
    @LoginRequired
    public HttpCommandResultWithData carReportOilChart(@RequestJson StatisticsCarReportChartForm form) {
        log.info("[QueryStatisticsController][carReportOilChart] start param:{}",form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = statisticsService.carReportOilChart(form);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("油耗图表查询失败");
            log.error(e.getMessage(), e);
        }
        log.info("[QueryStatisticsController][carReportOilChart] end return:{}",result.getData());
        return result;
    }
    /**
     * 车辆报表-油耗-怠速油耗
     */
    @PostMapping(value = "/statistics/carReportIdlOil")
    @ApiOperation("车辆报表-总油耗-详情-2")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "beginDate" , value = "统计开始日期" , dataType = "String"),
            @ApiImplicitParam(name = "endDate" , value = "统计结束日期" , dataType = "String")
    })
    @LoginRequired
    public HttpCommandResultWithData carReportIdlOil(@RequestJson StatisticsCarReportIdlOilForm form) {
        log.info("[QueryStatisticsController][carReportIdlOil] start param:{}",form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = statisticsService.carReportIdlOil(form);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("车辆怠速油耗查询失败");
            log.error(e.getMessage(), e);
        }
        log.info("[QueryStatisticsController][carReportIdlOil] end return:{}",result.getData());
        return result;
    }
    /**
     * 车辆报表-总油耗-车辆列表
     */
    @PostMapping(value = "/statistics/carReportOilCarList")
    @ApiOperation("车辆报表-总油耗-详情-3")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "beginDate" , value = "统计开始日期" , dataType = "String"),
            @ApiImplicitParam(name = "endDate" , value = "统计结束日期" , dataType = "String"),
            @ApiImplicitParam(name = "keyWord" , value = "关键字" , dataType = "String"),
            @ApiImplicitParam(name = "returnAll" , value = "是否返回全部数据" , dataType = "String")
    })
    @LoginRequired
    public HttpCommandResultWithData carReportOilCarList(@RequestJson StatisticsOilCarListForm form) {
        log.info("[QueryStatisticsController][carReportOilCarList] start param:{}",form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = statisticsService.carReportOilCarList(form);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("油耗车辆列表查询失败");
            log.error(e.getMessage(), e);
        }
        log.info("[QueryStatisticsController][carReportOilCarList] end return:{}",result.getData());
        return result;
    }
    /**
     * 车辆报表-车辆油耗明细
     */
    @PostMapping(value = "/statistics/carReportDetailedOilList")
    @ApiOperation("车辆报表-总油耗-详情-2")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "beginDate" , value = "统计开始日期" , dataType = "String"),
            @ApiImplicitParam(name = "endDate" , value = "统计结束日期" , dataType = "String"),
            @ApiImplicitParam(name = "carId" , value = "车辆ID" , dataType = "String")
    })
    @LoginRequired
    public HttpCommandResultWithData carReportDetailedOilList(@RequestJson StatisticsMileageCarDetailForm form) {
        log.info("[QueryStatisticsController][carReportDetailedOilList] start param:{}",form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = statisticsService.carReportDetailedOilList(form);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("车辆油耗明细列表查询失败");
            log.error(e.getMessage(), e);
        }
        log.info("[QueryStatisticsController][carReportDetailedOilList] end return:{}",result.getData());
        return result;
    }
    /**
     * 车辆报表-行驶时长-图表接口
     */
    @PostMapping(value = "/statistics/carReportDrivingChart")
    @ApiOperation("车辆报表-行驶时长-详情-1")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "beginDate" , value = "统计开始日期" , dataType = "String"),
            @ApiImplicitParam(name = "endDate" , value = "统计结束日期" , dataType = "String"),
            @ApiImplicitParam(name = "chartType" , value = "区分标志" , dataType = "String"),
            @ApiImplicitParam(name = "carId" , value = "车辆ID" , dataType = "String")
    })
    @LoginRequired
    public HttpCommandResultWithData carReportDrivingChart(@RequestJson StatisticsCarReportChartForm form) {
        log.info("[QueryStatisticsController][carReportDrivingChart] start param:{}",form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = statisticsService.carReportDrivingChart(form);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("行驶时长图表查询失败");
            log.error(e.getMessage(), e);
        }
        log.info("[QueryStatisticsController][carReportDrivingChart] end return:{}",result.getData());
        return result;
    }
    /**
     * 车辆报表-油耗-怠速时长
     */
    @PostMapping(value = "/statistics/carReportIdlTime")
    @ApiOperation("车辆报表-总油耗-详情-2")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "beginDate" , value = "统计开始日期" , dataType = "String"),
            @ApiImplicitParam(name = "endDate" , value = "统计结束日期" , dataType = "String")
    })
    @LoginRequired
    public HttpCommandResultWithData carReportIdlTime(@RequestJson StatisticsCarReportIdlOilForm form) {
        log.info("[QueryStatisticsController][carReportIdlTime] start param:{}",form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = statisticsService.carReportIdlTime(form);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("车辆怠速时长查询失败");
            log.error(e.getMessage(), e);
        }
        log.info("[QueryStatisticsController][carReportIdlTime] end return:{}",result.getData());
        return result;
    }
    /**
     * 车辆报表-总油耗-车辆列表
     */
    @PostMapping(value = "/statistics/carReportCarList")
    @ApiOperation("车辆报表-总油耗-详情-3")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "beginDate" , value = "统计开始日期" , dataType = "String"),
            @ApiImplicitParam(name = "endDate" , value = "统计结束日期" , dataType = "String"),
            @ApiImplicitParam(name = "keyWord" , value = "关键字" , dataType = "String"),
            @ApiImplicitParam(name = "returnAll" , value = "是否返回全部数据" , dataType = "String")
    })
    @LoginRequired
    public HttpCommandResultWithData carReportCarList(@RequestJson StatisticsOilCarListForm form) {
        log.info("[QueryStatisticsController][carReportCarList] start param:{}",form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = statisticsService.carReportCarList(form);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("行驶时长车辆列表查询失败");
            log.error(e.getMessage(), e);
        }
        log.info("[QueryStatisticsController][carReportCarList] end return:{}",result.getData());
        return result;
    }
    /**
     * 车辆报表-车辆行驶时长明细
     */
    @PostMapping(value = "/statistics/carReportDetailedDrivingList")
    @ApiOperation("车辆报表-行驶时长-详情-3-车辆列表详情-2")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "beginDate" , value = "统计开始日期" , dataType = "String"),
            @ApiImplicitParam(name = "endDate" , value = "统计结束日期" , dataType = "String"),
            @ApiImplicitParam(name = "carId" , value = "车辆ID" , dataType = "String")
    })
    @LoginRequired
    public HttpCommandResultWithData carReportDetailedDrivingList(@RequestJson StatisticsMileageCarDetailForm form) {
        log.info("[QueryStatisticsController][carReportDetailedDrivingList] start param:{}",form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = statisticsService.carReportDetailedDrivingList(form);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("车辆行驶时长明细列表查询失败");
            log.error(e.getMessage(), e);
        }
        log.info("[QueryStatisticsController][carReportDetailedDrivingList] end return:{}",result.getData());
        return result;
    }
    /**
     * 车辆报表-不良驾驶行为-饼图
     */
    @PostMapping(value = "/badDrivingChartForPie")
    @ApiOperation("车辆报表-不良驾驶行为-详情-1")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "beginDate" , value = "统计开始日期" , dataType = "String"),
            @ApiImplicitParam(name = "endDate" , value = "统计结束日期" , dataType = "String"),
            @ApiImplicitParam(name = "chartType" , value = "区分标志" , dataType = "String"),
            @ApiImplicitParam(name = "carId" , value = "车辆ID" , dataType = "String")
    })
    @LoginRequired
    public HttpCommandResultWithData badDrivingChartForPie(@RequestJson StatisticsCarReportChartForm form) {
        log.info("[QueryStatisticsController][badDrivingChartForPie] start param:{}",form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = statisticsService.badDrivingChartForPie(form);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("不良驾驶行为图表查询失败");
            log.error(e.getMessage(), e);
        }
        log.info("[QueryStatisticsController][badDrivingChartForPie] end return:{}",result.getData());
        return result;
    }
    /**
     * 车辆报表-不良驾驶行为-按行为统计
     */
    @PostMapping(value = "/badDrivingCountByBehavior")
    @ApiOperation("车辆报表-不良驾驶行为-详情-按行为")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "beginDate" , value = "统计开始日期" , dataType = "String"),
            @ApiImplicitParam(name = "endDate" , value = "统计结束日期" , dataType = "String")
    })
    @LoginRequired
    public HttpCommandResultWithData badDrivingCountByBehavior(@RequestJson StatisticsCarReportForm form) {
        log.info("[QueryStatisticsController][badDrivingCountByBehavior] start paran:{}",form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = statisticsService.badDrivingCountByBehavior(form);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("不良驾驶行为按行为查询失败");
            log.error(e.getMessage(), e);
        }
        log.info("[QueryStatisticsController][badDrivingCountByBehavior] end return:{}",result.getData());
        return result;
    }
    /**
     * 车辆报表-不良驾驶行为-按车辆统计
     */
    @PostMapping(value = "/badDrivingCountByCar")
    @ApiOperation("车辆报表-不良驾驶行为-详情-按车辆")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "beginDate" , value = "统计开始日期" , dataType = "String"),
            @ApiImplicitParam(name = "endDate" , value = "统计结束日期" , dataType = "String"),
            @ApiImplicitParam(name = "keyWord" , value = "关键字" , dataType = "String"),
            @ApiImplicitParam(name = "returnAll" , value = "是否返回全部数据" , dataType = "String")
    })
    @LoginRequired
    public HttpCommandResultWithData badDrivingCountByCar(@RequestJson BadDrivingCountByCarForm form) {
        log.info("[QueryStatisticsController][badDrivingCountByCar] start param:{}",form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = statisticsService.badDrivingCountByCar(form);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("不良驾驶行为按车辆查询失败");
            log.error(e.getMessage(), e);
        }
        log.info("[QueryStatisticsController][badDrivingCountByCar] end return:{}",result.getData());
        return result;
    }
    /**
     * 车辆报表-不良驾驶行为-按车辆统计
     */
    @PostMapping(value = "/badDrivingCountByCarDetailChart")
    @ApiOperation("车辆报表-不良驾驶行为-详情-按车辆-详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "beginDate" , value = "统计开始日期" , dataType = "String"),
            @ApiImplicitParam(name = "endDate" , value = "统计结束日期" , dataType = "String"),
            @ApiImplicitParam(name = "carId" , value = "车辆ID" , dataType = "String")
    })
    @LoginRequired
    public HttpCommandResultWithData badDrivingCountByCarDetailChart(@RequestJson StatisticsMileageCarDetailForm form) {
        log.info("[QueryStatisticsController][badDrivingCountByCarDetailChart] start param:{}",form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = statisticsService.badDrivingCountByCarDetailChart(form);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("不良驾驶行为车辆明细查询失败");
            log.error(e.getMessage(), e);
        }
        log.info("[QueryStatisticsController][badDrivingCountByCarDetailChart] end return:{}",result.getData());
        return result;
    }
}
