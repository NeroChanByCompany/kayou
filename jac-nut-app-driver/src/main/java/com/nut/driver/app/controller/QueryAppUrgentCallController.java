package com.nut.driver.app.controller;

import com.nut.common.annotation.LoginRequired;
import com.nut.common.annotation.RequestJson;
import com.nut.common.base.BaseController;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.Result;
import com.nut.driver.app.dao.QueryAppUrgentCallDao;
import com.nut.driver.app.form.AppUrgentCallForm;
import com.nut.driver.app.form.QueryAppUrgentCallForm;
import com.nut.driver.app.service.QueryAppUrgentCallService;
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
 * @Description: 一键呼救
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.controller
 * @Author: yzl
 * @CreateTime: 2021-06-28 14:34
 * @Version: 1.0
 */
@RestController
@Slf4j
@Api(tags = "一键呼救")
public class QueryAppUrgentCallController extends BaseController {

    @Autowired
    private QueryAppUrgentCallService queryAppUrgentCallService;

    @PostMapping(value = "/queryUrgentCall")
    @ApiOperation("一键救援")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name" , value = "名称" , dataType = "String"),
            @ApiImplicitParam(name = "type" , value = "类型" , dataType = "String")
    })
    @LoginRequired
    @SneakyThrows
    public HttpCommandResultWithData queryUrgentCall(@ApiIgnore @RequestJson QueryAppUrgentCallForm form){
        log.info("queryUrgentCall start param:{}",form);
        return Result.ok(queryAppUrgentCallService.query(form));
    }

    @PostMapping(value = "/urgentCall")
    @ApiOperation("一键救援拨号")
    @ApiImplicitParam(name = "id" , value = "救援电话ID" , dataType = "String")
    @LoginRequired
    @SneakyThrows
    public HttpCommandResultWithData UrgentCall(@ApiIgnore @RequestJson AppUrgentCallForm form){
        log.info("UrgentCall start param:{}",form);
        queryAppUrgentCallService.urgentCall(form);
        return Result.ok();
    }


}
