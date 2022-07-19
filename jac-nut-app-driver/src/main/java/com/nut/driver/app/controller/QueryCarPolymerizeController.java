package com.nut.driver.app.controller;

import com.netflix.ribbon.proxy.annotation.Http;
import com.nut.common.annotation.LoginRequired;
import com.nut.common.annotation.RequestJson;
import com.nut.common.base.BaseController;
import com.nut.common.exception.ExceptionUtil;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.Result;
import com.nut.driver.app.dto.MonitorDto;
import com.nut.driver.app.dto.QueryTrackDto;
import com.nut.driver.app.form.QueryCarPolymerizeForm;
import com.nut.driver.app.form.QueryTrackForm;
import com.nut.driver.app.service.QueryCarPolymerizeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @Description: 海量打点，实时监控
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.controller
 * @Author: yzl
 * @CreateTime: 2021-06-29 16:49
 * @Version: 1.0
 */
@RestController
@Slf4j
@Api(tags = "实时监控")
public class QueryCarPolymerizeController extends BaseController {

    @Autowired
    private QueryCarPolymerizeService queryCarPolymerizeService;

    /**
     * @Description：查询车辆实时监控列表
     * @author YZL
     * @data 2021/6/30 9:02
     */
    @PostMapping(value = "/queryCarPolymerize")
    @ApiOperation("查询车辆实时监控列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "leftLongitude", value = "左下角经度坐标", dataType = "String"),
            @ApiImplicitParam(name = "leftLatitude", value = "左下角纬度坐标", dataType = "String"),
            @ApiImplicitParam(name = "rightLongitude", value = "右上角经度坐标", dataType = "String"),
            @ApiImplicitParam(name = "rightLatitude", value = "右上角纬度坐标", dataType = "String"),
            @ApiImplicitParam(name = "zoom", value = "地图缩放级别", dataType = "String"),
    })
    @LoginRequired
    @SneakyThrows
    public HttpCommandResultWithData queryCarPolymerize(@ApiIgnore @RequestJson QueryCarPolymerizeForm form) {
        log.info("queryCarPolymerize start param:{}", form);
        MonitorDto monitorDto = null;
        try {
            monitorDto = queryCarPolymerizeService.queryCarLocation(form);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ExceptionUtil.result(ECode.SERVER_ERROR.code(), "车辆位置查询失败");
        }
        log.info("queryCarPolymerize end return:{}", monitorDto);
        return Result.ok(monitorDto);
    }

    /**
     * @Description：轨迹回放
     * @author YZL
     * @data 2021/6/30 9:03
     */
    @PostMapping(value = "/queryTrack")
    @ApiOperation("轨迹回放")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "carId", value = "车辆ID", dataType = "String"),
            @ApiImplicitParam(name = "beginDate", value = "统计开始日期", dataType = "String"),
            @ApiImplicitParam(name = "endDate", value = "统计结束日期", dataType = "String"),
            @ApiImplicitParam(name = "zoom", value = "地图缩放级别", dataType = "String")
    })
    @LoginRequired
    @SneakyThrows
    public HttpCommandResultWithData queryTrack(@ApiIgnore @RequestJson QueryTrackForm form) {
        log.info("queryTrack start param:{}",form);
        this.formValidate(form);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String beginDate = form.getBeginDate();
        String endDate = form.getEndDate();
        LocalDateTime beginTime = LocalDateTime.parse(beginDate,formatter);
        LocalDateTime endTime = LocalDateTime.parse(endDate,formatter);
        Duration duration = Duration.between(beginTime,endTime);
        log.info("开始时间：{}，结束时间：{}，时间间隔：{}", beginDate, endDate, duration.toDays());
        if (duration.toDays() > 7){
            ExceptionUtil.result(ECode.SERVER_ERROR.code(), "起止时间范围不能超过7天，请重新选择日期");
        }
        QueryTrackDto queryTrackDto = null;
        try {
            queryTrackDto = queryCarPolymerizeService.queryTrack(form);
        } catch (Exception e) {
            ExceptionUtil.result(ECode.SERVER_ERROR.code(), "车辆轨迹查询失败");
        }
        log.info("queryTrack end return:{}",queryTrackDto);
        return Result.ok(queryTrackDto);
    }
}
