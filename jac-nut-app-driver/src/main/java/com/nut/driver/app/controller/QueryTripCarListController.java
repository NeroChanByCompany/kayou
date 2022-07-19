package com.nut.driver.app.controller;

import com.netflix.ribbon.proxy.annotation.Http;
import com.nut.common.annotation.LoginRequired;
import com.nut.common.annotation.RequestJson;
import com.nut.common.base.BaseController;
import com.nut.common.exception.ExceptionUtil;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.Result;
import com.nut.driver.app.dto.TripCarListDto;
import com.nut.driver.app.form.QueryTripCarListForm;
import com.nut.driver.app.service.QueryTripCarListService;
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

import java.util.List;

/**
 * @Description: 行程分析
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.controller
 * @Author: yzl
 * @CreateTime: 2021-06-30 09:38
 * @Version: 1.0
 */
@RestController
@Slf4j
@Api(tags = "行程分析")
public class QueryTripCarListController extends BaseController {

    @Autowired
    private QueryTripCarListService queryTripCarListService;

    /**
    * @Description：行程分析首页
    * @author YZL
    * @data 2021/6/30 10:01
    */
    @PostMapping(value = "/queryTripCarList")
    @ApiOperation("行程分析首页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "queryTime" , value = "查询日期" , dataType = "String"),
            @ApiImplicitParam(name = "carNumber" , value = "车牌号" , dataType = "String")
    })
    @LoginRequired
    @SneakyThrows
    public HttpCommandResultWithData queryTripCarList(@ApiIgnore @RequestJson QueryTripCarListForm form){
        log.info("queryTripCarList start param:{}",form);
        List<TripCarListDto> listDtos = null;
        try {
            listDtos = queryTripCarListService.queryTripCarList(form);
        }catch (Exception e){
            log.error(e.getMessage(), e);
            ExceptionUtil.result(ECode.SERVER_ERROR.code(), "车辆列表查询失败！");
        }
        log.info("queryTripCarList end return:{}",listDtos);
        return Result.ok(listDtos);
    }

}
