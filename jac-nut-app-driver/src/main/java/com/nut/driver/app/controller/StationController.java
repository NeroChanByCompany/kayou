package com.nut.driver.app.controller;

import com.nut.common.annotation.RequestJson;
import com.nut.driver.app.form.QueryServiceStationDetailForm;
import com.nut.driver.app.form.QueryStationForm;
import com.nut.common.annotation.LoginRequired;
import com.nut.common.base.BaseController;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.Result;
import com.nut.driver.app.service.StationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @Description: 服务站相关接口
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.driver.controller
 * @Author: yzl
 * @CreateTime: 2021-06-23 10:34
 * @Version: 1.0
 */
@RestController
@Api(tags = "服务站相关接口")
@Slf4j
public class StationController extends BaseController {

    @Autowired
    private StationService stationService;

    @PostMapping(value = "/appStations")
    @ApiOperation("服务站列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "distance" , value = "距离" , dataType = "String"),
            @ApiImplicitParam(name = "level" , value = "服务站等级" , dataType = "String"),
            @ApiImplicitParam(name = "keyWord" , value = "关键字" , dataType = "String"),
            @ApiImplicitParam(name = "id" , value = "服务站点所属城市id" , dataType = "String"),
            @ApiImplicitParam(name = "lon" , value = "经度" , dataType = "String"),
            @ApiImplicitParam(name = "lat" , value = "纬度" , dataType = "String"),
            @ApiImplicitParam(name = "centralFlag" , value = "服务站类型" , dataType = "String"),
            @ApiImplicitParam(name = "clientFlg" , value = "客户端标记" , dataType = "String"),
            @ApiImplicitParam(name = "sortType" , value = "排序" , dataType = "Integer")
    })
    @CrossOrigin
    @LoginRequired
    @SneakyThrows
    public HttpCommandResultWithData appStations(@ApiIgnore @RequestJson QueryStationForm form){
        log.info("appStations start param:{}",form);
        this.formValidate(form);
        return Result.ok(stationService.getStationList(form));
    }

    @PostMapping(value = "/appStationDetail")
    @ApiOperation("服务站详情")
    @ApiImplicitParam(name = "stationId" , value = "服务站ID" , dataType = "String")
    @CrossOrigin
    @LoginRequired
    @SneakyThrows
    public HttpCommandResultWithData appStationDetail(@ApiIgnore @RequestJson  QueryServiceStationDetailForm form) {
        log.info("appStationDetail start param:{}",form);
        this.formValidate(form);
        return Result.ok(stationService.appStationDetail(form));
    }


}
