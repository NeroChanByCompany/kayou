package com.nut.driver.app.controller;

import com.nut.common.annotation.LoginRequired;
import com.nut.common.annotation.RequestJson;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.Result;
import com.nut.driver.app.form.FeedbackForm;
import com.nut.driver.app.service.FeedbackService;
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

/**
 * @Description: 意见反馈
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.controller
 * @Author: yzl
 * @CreateTime: 2021-06-28 09:55
 * @Version: 1.0
 */
@RestController
@Api(value = "意见反馈")
@Slf4j
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @PostMapping(value = "/feedback/add")
    @SneakyThrows
    @ApiOperation(value = "添加意见反馈")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",dataType = "Integer"),
            @ApiImplicitParam(name = "message",value = "反馈信息",dataType = "String"),
            @ApiImplicitParam(name = "phone",value = "电话",dataType = "String"),
            @ApiImplicitParam(name = "createTime",value = "创建时间",dataType = "Date"),
            @ApiImplicitParam(name = "updateTime",value = "更新时间",dataType = "Date"),
            @ApiImplicitParam(name = "createUser",value = "创建者",dataType = "String"),
            @ApiImplicitParam(name = "updateUser",value = "更新者",dataType = "String")
    })
    public HttpCommandResultWithData addFeedbace(@RequestJson FeedbackForm form){
        log.info("addFeedbace start param:{}",form);
        return Result.ok(feedbackService.addFeedback(form));
    }
}
