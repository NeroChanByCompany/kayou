package com.nut.driver.app.controller;

import com.google.gson.annotations.SerializedName;
import com.nut.common.annotation.RequestJson;
import com.nut.driver.app.form.MyOrderDetailForm;
import com.nut.common.annotation.LoginRequired;
import com.nut.common.base.BaseController;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.Result;
import com.nut.driver.app.form.MyOrdersForm;
import com.nut.driver.app.service.MyOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @Description: 我的预约
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.app.driver.controller
 * @Author: yzl
 * @CreateTime: 2021-06-23 20:01
 * @Version: 1.0
 */
@RestController
@Slf4j
@Api(tags = "我的预约")
public class MyOrderController extends BaseController {

    @Autowired
    private MyOrderService myOrderService;

    @PostMapping(value = "/myOrders")
    @ApiOperation("我的工单列表")
    @ApiImplicitParam(name = "queryTag" , value = "查询标志" , dataType = "String")
    @LoginRequired
    @SneakyThrows
    public HttpCommandResultWithData myOrders(@ApiIgnore @RequestJson MyOrdersForm form){
        log.info("myOrders start param:{}",form);
        this.formValidate(form);
        return Result.ok(myOrderService.myOrders(form));
    }

    @PostMapping(value = "myOrderDetail")
    @ApiOperation("我的工单详情")
    @ApiImplicitParam(name = "woCode" , value = "工单号" , dataType = "String")
    @LoginRequired
    @SneakyThrows
    public HttpCommandResultWithData myOrderDetail(@ApiIgnore @RequestJson  MyOrderDetailForm form){
        log.info("myOrderDetail start param:{}",form);
        this.formValidate(form);
        HttpCommandResultWithData result=new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        try {
             result = myOrderService.myOrderDetail(form);
        }catch (Exception e){
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("查询我的预约详情失败");
            log.error(e.getMessage(),e);
        }
        log.info("myOrderDetail end return:{}",result.getData());
        return result;
    }
}
