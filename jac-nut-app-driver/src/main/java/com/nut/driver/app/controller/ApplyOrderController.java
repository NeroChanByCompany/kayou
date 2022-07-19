package com.nut.driver.app.controller;

import com.nut.common.annotation.RequestJson;
import com.nut.driver.app.form.ApplyOrderForm;
import com.nut.driver.app.form.CancelOrderForm;
import com.nut.common.annotation.LoginRequired;
import com.nut.common.base.BaseController;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.Result;
import com.nut.driver.app.service.ApplyOrderService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @Description: 服务预约
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.driver.controller
 * @Author: yzl
 * @CreateTime: 2021-06-21 19:06
 * @Version: 1.0
 */
@RestController
@Slf4j
public class ApplyOrderController extends BaseController {

    @Autowired
    private ApplyOrderService applyOrderService;

    /**
     * 服务预约
     *
     * @param form
     * @return
     */
    @PostMapping(value = "/applyOrder")
    @SneakyThrows
    @LoginRequired
    @ApiImplicitParams({
            @ApiImplicitParam(name = "woType", value = "工单类型", dataType = "Integer"),
            @ApiImplicitParam(name = "carLocation", value = "车辆位置", dataType = "String"),
            @ApiImplicitParam(name = "carLon", value = "车辆经度", dataType = "String"),
            @ApiImplicitParam(name = "carLat", value = "车辆纬度", dataType = "String"),
            @ApiImplicitParam(name = "appoUserName", value = "预约人", dataType = "String"),
            @ApiImplicitParam(name = "appoUserPhone", value = "预约人电话", dataType = "String"),
            @ApiImplicitParam(name = "carId", value = "预约车辆id", dataType = "String"),
            @ApiImplicitParam(name = "stationId", value = "服务站id", dataType = "String"),
            @ApiImplicitParam(name = "repairItem", value = "维修项目", dataType = "String"),
            @ApiImplicitParam(name = "maintainItem", value = "保养项目", dataType = "String"),
            @ApiImplicitParam(name = "appoArriveTime", value = "预约到站时间", dataType = "String"),
            @ApiImplicitParam(name = "userComment", value = "故障描述", dataType = "String")
    })
    public HttpCommandResultWithData applyOrder(@ApiIgnore @RequestJson ApplyOrderForm form) {
        log.info("applyOrder start param:{}", form);
        this.formValidate(form);
        return Result.ok(applyOrderService.applyOrder(form));
    }

    @PostMapping(value = "/cancelOrder")
    @LoginRequired
    @SneakyThrows
    @ApiImplicitParams({
            @ApiImplicitParam(name = "woCode", value = "工单号", dataType = "String"),
            @ApiImplicitParam(name = "cancelReason", value = "取消原因", dataType = "String")
    })
    public HttpCommandResultWithData cancelOrder(@ApiIgnore @RequestJson CancelOrderForm form) {
        log.info("cancelOrder start param:{}", form);
        this.formValidate(form);
        return Result.ok(applyOrderService.cancleOrder(form));
    }

}
