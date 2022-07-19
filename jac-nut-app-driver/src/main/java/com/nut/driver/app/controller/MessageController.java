package com.nut.driver.app.controller;

import com.nut.common.annotation.LoginRequired;
import com.nut.common.annotation.RequestJson;
import com.nut.common.base.BaseController;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.PagingInfo;
import com.nut.common.result.Result;
import com.nut.driver.app.dto.QueryMessageDetailListDto;
import com.nut.driver.app.form.QueryMessageDetailListForm;
import com.nut.driver.app.form.QueryMessageForm;
import com.nut.driver.app.service.MessageService;
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
 * @Description: 消息接口
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.controller
 * @Author: yzl
 * @CreateTime: 2021-06-27 10:59
 * @Version: 1.0
 */
@RestController
@Slf4j
@Api(value = "消息接口")
public class MessageController extends BaseController {

    @Autowired
    private MessageService messageService;

    @PostMapping(value = "/queryMessageList")
    @ApiOperation("消息列表")
    @LoginRequired
    @SneakyThrows
    public HttpCommandResultWithData queryMessageList(@ApiIgnore @RequestJson QueryMessageForm form){
        log.info("queryMessageList start param:{}",form);
        return Result.ok(messageService.queryMessageList(form));
    }

    @PostMapping(value = "/queryMessageDetailList")
    @ApiOperation("消息分类")
    @ApiImplicitParam(name = "messageType" , value = "消息类型" , dataType = "Integer")
    @LoginRequired
    @SneakyThrows
    public HttpCommandResultWithData queryMessageDetailList(@ApiIgnore @RequestJson QueryMessageDetailListForm form){
        log.info("queryMessageDetailList start param:{}",form);
        this.formValidate(form);
        PagingInfo<QueryMessageDetailListDto> dtoPagingInfo= messageService.queryMessageDetailList(form);
        //修改未读消息为已读
        messageService.updateUnread(form);
        log.info("queryMessageDetailList end return:{}",dtoPagingInfo);
        return Result.ok(dtoPagingInfo);
    }


}
