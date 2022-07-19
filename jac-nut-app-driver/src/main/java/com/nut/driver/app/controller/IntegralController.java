package com.nut.driver.app.controller;

import com.nut.common.annotation.RequestJson;
import com.nut.common.result.ECode;
import com.nut.driver.app.form.IntegralOperationFreezeForm;
import com.nut.driver.app.form.QueryIntegralForm;
import com.nut.driver.app.service.IntegralService;
import com.nut.common.annotation.LoginRequired;
import com.nut.common.base.BaseController;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.Result;
import com.nut.driver.app.form.IntegralOperationForm;

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

/**
 * @Description: 积分相关接口
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.driver.controller
 * @Author: yzl
 * @CreateTime: 2021-06-21 13:30
 * @Version: 1.0
 */
@RestController
@Slf4j
@Api(value = "积分相关")
public class IntegralController extends BaseController {

    @Autowired
    private IntegralService integralService;

    @PostMapping(value = "/queryIntegral")
    @LoginRequired
    @SneakyThrows
    @ApiOperation(value = "用户积分查询")
    @ApiImplicitParam(name = "ucId", value = "用户id字符串", dataType = "String")
    public HttpCommandResultWithData queryIntegral(@ApiIgnore @RequestJson QueryIntegralForm form) {
        log.info("queryIntegral start param:{}",form);
        return Result.ok(integralService.queryIntegral(form));
    }

    @PostMapping(value = "/newUserJob")
    @LoginRequired
    @SneakyThrows
    @ApiOperation(value = "用户积分任务查询")
    @ApiImplicitParam(name = "ucId", value = "用户id字符串", dataType = "String")
    public HttpCommandResultWithData newUserJob(@ApiIgnore @RequestJson QueryIntegralForm form) {
        log.info("newUserJob start param:{}",form);
        return Result.ok(integralService.newUserJob(form));
    }

    @PostMapping(value = "/queryIntegralHistory")
    @LoginRequired
    @SneakyThrows
    @ApiOperation(value = "用户积分历史")
    @ApiImplicitParam(name = "ucId", value = "用户id字符串", dataType = "String")
    public HttpCommandResultWithData queryIntegralHistory(@ApiIgnore @RequestJson QueryIntegralForm form) {
        log.info("queryIntegralHistory start param:{}",form);
        return Result.ok(integralService.queryIntegralHistory(form));
    }

    @PostMapping(value = "/integralOperation")
    @SneakyThrows
    @ApiOperation(value = "积分操作")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "operationId",name = "操作动作 0加1减",dataType = "Integer"),
            @ApiImplicitParam(value = "integralCounts",name = "积分（加减）数量",dataType = "Integer"),
            @ApiImplicitParam(value = "actionId",name = "积分操作动作",dataType = "String"),
            @ApiImplicitParam(value = "consumeOrder",name = "消费",dataType = "String"),
            @ApiImplicitParam(value = "orderNum",name = "订单数量",dataType = "String")
    })
    public HttpCommandResultWithData integralOperation(@ApiIgnore @RequestJson IntegralOperationForm form){
        log.info("integralOperation start param:{}",form);
        log.info("integralOperation start param:{}",form.getUcId());
        return Result.ok(integralService.integralOperation(form));
    }

    @PostMapping(value = "/integralOperationFreeze")
    @SneakyThrows
    @ApiOperation(value = "积分冻结回滚")
    public HttpCommandResultWithData integralOperationFreeze(@ApiIgnore @RequestJson IntegralOperationFreezeForm form){
        log.info("integralOperation start param:{}",form);
        log.info("integralOperation start param:{}",form.getUcId());
        log.info("integralOperation start param:{}",form.getAutoIncreaseId());
        try{
            integralService.integralOperationFreeze(form);
            return Result.ok();
        }catch (Exception e){
            return Result.result(ECode.CLIENT_ERROR.code(),"积分回滚退回失败");
        }
    }





}
