package com.nut.tools.app.controller;

import com.alibaba.fastjson.JSONObject;

import com.nut.common.base.BaseController;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.Result;
import com.nut.tools.app.form.PushMesForm;
import com.nut.tools.app.service.PushMessageService;
import com.nut.tools.app.service.PushService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: liuBing
 * @Description: 指定消息推送
 */

@RestController
public class MessagePushController extends BaseController {

    @Autowired
    private PushMessageService pushMessageService;

    @Autowired
    private PushService pushService;

    @SneakyThrows
    @PostMapping(value = "/PushMes")
    public HttpCommandResultWithData PushMes(@RequestBody PushMesForm from) {
        this.formValidate(from);
        pushMessageService.pushMessageGetTemplate(from);
           /* JPushMesCommand jPushMesCommand = new JPushMesCommand();
            jPushMesCommand.setAppType(command.getAppType());
            jPushMesCommand.setDeviceType(command.getSortType());
            jPushMesCommand.setRegistrationId(command.get);*/
        pushService.push2Device(from);
        return Result.ok();
    }

    @PostMapping(value = "/JPushMes")
    public HttpCommandResultWithData JPushMes(@RequestBody JSONObject params) {
        pushService.JPushMgs(params);
        return Result.ok();
    }

    /**
     * 单独为外出救援和预约进站的消息通知
     * @author liubing
     * @param from
     * @return
     */
    @SneakyThrows
    @PostMapping(value = "/sendMessage4Station")
    public HttpCommandResultWithData sendMessage4Station(@RequestBody PushMesForm from) {
        this.formValidate(from);
        pushMessageService.pushMessageGetTemplate(from);
        pushService.push2Station(from);
        return Result.ok();
    }
}
