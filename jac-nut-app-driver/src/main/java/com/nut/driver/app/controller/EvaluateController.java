package com.nut.driver.app.controller;

import com.nut.common.annotation.RequestJson;
import com.nut.driver.app.form.StationEvaluatesForm;
import com.nut.driver.app.form.ReviewOrderForm;
import com.nut.common.annotation.LoginRequired;
import com.nut.common.base.BaseController;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.Result;
import com.nut.driver.app.service.EvaluateService;

import com.nut.common.utils.JsonUtil;
import com.nut.driver.app.service.IntegralService;
import com.nut.driver.app.service.ServiceStationCallerService;
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
 * @Description: 评价
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.app.driver.controller
 * @Author: yzl
 * @CreateTime: 2021-06-23 14:37
 * @Version: 1.0
 */
@RestController
@Slf4j
public class EvaluateController extends BaseController {

    @Autowired
    private EvaluateService evaluateService;

    @Autowired
    private IntegralService integralService;

    @Autowired
    private ServiceStationCallerService serviceStationCallerService;

    @PostMapping(value = "/stationEvaluates")
    @LoginRequired
    @SneakyThrows
    @ApiImplicitParams({
            @ApiImplicitParam(name = "stationId",value = "服务站ID",dataType = "String"),
            @ApiImplicitParam(name = "driverId",value = "司机ID",dataType = "String"),
            @ApiImplicitParam(name = "flag",value = "查询类型",dataType = "String"),
            @ApiImplicitParam(name = "woCode",value = "工单号",dataType = "String"),
            @ApiImplicitParam(name = "vin",value = "车辆vin码",dataType = "String")
    })
    public HttpCommandResultWithData stationEvaluates(@ApiIgnore @RequestJson StationEvaluatesForm form) {
        log.info("stationEvaluates start param:{}",form);
        this.formValidate(form);
        return Result.ok(evaluateService.stationEvaluates(form));
    }

    @PostMapping(value = "/reviewOrder")
    @LoginRequired
    @SneakyThrows
    @ApiOperation(value = "工单评价")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "woCode",value = "工单号",dataType = "String"),
            @ApiImplicitParam(name = "stationId",value = "服务站ID",dataType = "String"),
            @ApiImplicitParam(name = "content",value = "其他建议",dataType = "String"),
            @ApiImplicitParam(name = "wholeStar",value = "总体满意程度星级",dataType = "String"),
            @ApiImplicitParam(name = "reviewLabel",value = "评价标签",dataType = "String"),
            @ApiImplicitParam(name = "otherLabel",value = "其他补充标签",dataType = "String"),
            @ApiImplicitParam(name = "selfPay",value = "是否自费（1：是；其他：免费）",dataType = "String"),
            @ApiImplicitParam(name = "cost",value = "自费费用",dataType = "String"),
            @ApiImplicitParam(name = "comeAgain",value = "是否再次光临",dataType = "String"),
            @ApiImplicitParam(name = "discontent",value = "不满意的流程",dataType = "String")
    })
    public HttpCommandResultWithData reviewOrder(@ApiIgnore @RequestJson ReviewOrderForm form){
        log.info("reviewOrder start param:{}",form);
        this.formValidate(form);
        HttpCommandResultWithData result=new HttpCommandResultWithData();
        try{
            result = evaluateService.reviewOrder(form);
            if (result.getResultCode() == 200 ){
                log.info("[----用户评价，添加积分----]Start");
                HttpCommandResultWithData integralResult=integralService.addAction(form.getUserId(),7);
                if (integralResult.getResultCode() !=  ECode.SUCCESS.code()) {
                    log.info("[----用户评价，添加积分----]ErrResult:"+ JsonUtil.toJson(integralResult));
                }
                log.info("[----用户评价，添加积分----]Success");
                log.info("[----用户评价，添加积分----]End");
            }
        }catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("评价服务站失败");
            log.error(e.getMessage(), e);
        }
//        serviceStationCallerService.triggerSendProcess(result, form.getWoCode());
        log.info("reviewOrder end return:{}",result.getData());
        return result;
    }



}
