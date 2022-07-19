package com.nut.driver.app.controller;

import com.nut.common.annotation.LoginRequired;
import com.nut.common.annotation.RequestJson;
import com.nut.common.base.BaseController;
import com.nut.common.exception.ExceptionUtil;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.Result;
import com.nut.driver.app.form.QueryHistoryJourneyForm;
import com.nut.driver.app.service.QueryHistoryJourneyService;
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

import java.util.List;

/**
 * @Description: 查询车辆历史行程
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.controller
 * @Author: yzl
 * @CreateTime: 2021-06-29 11:09
 * @Version: 1.0
 */
@RestController
@Slf4j
@Api(tags = "查询车辆历史行程")
public class QueryHistoryJourneyController extends BaseController {

    @Autowired
    private QueryHistoryJourneyService queryHistoryJourneyService;

    /**
    * @Description：查询车辆历史行程
    * @author YZL
    * @data 2021/6/29 11:10
    */
    @PostMapping(value = "/queryHistoryJourney")
    @ApiOperation("外出救援轨迹")
    @ApiImplicitParam(name = "woCode" , value = "工单号" , dataType = "String")
    @LoginRequired
    @SneakyThrows
    public HttpCommandResultWithData queryHistoryJourney(@ApiIgnore @RequestJson QueryHistoryJourneyForm form){
        log.info("queryHistoryJourney start param:{}",form);
        this.formValidate(form);
        List list = null;
        try {
            list = queryHistoryJourneyService.QueryHistoryJourney(form);
        }catch (Exception e){
            log.error(e.getMessage(),e);
            ExceptionUtil.result(ECode.SERVER_ERROR.code(),"查询车辆历史行程失败!");
        }
        log.info("queryHistoryJourney end return:{}",list);
        return Result.ok(list);
    }

}
