package com.nut.driver.app.client;

import com.alibaba.fastjson.JSONObject;
import com.nut.driver.app.form.PushMesForm;
import com.nut.common.result.HttpCommandResultWithData;

import com.nut.driver.app.form.SendSmsForm;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

/**
 * @Description: 推送中心
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.driver.client
 * @Author: yzl
 * @CreateTime: 2021-06-23 09:13
 * @Version: 1.0
 */
@FeignClient(value = "jac-nut-app-tools")
public interface ToolsClient {
    /**
     * 推送
     */
    @RequestMapping(method = RequestMethod.POST, value = "/PushMes", consumes = "application/json")
    HttpCommandResultWithData MessagePush(@RequestBody PushMesForm form);

    /**
     * 发送短信
     */
    @RequestMapping(method = RequestMethod.POST, value = "/sendSms", consumes = "application/json")
    Map sendSms(@RequestBody SendSmsForm form);

    /**
     * 推送消息
     * @param params
     * @return
     */
    @RequestMapping(method = RequestMethod.POST,value = "/JPushMes",consumes = "application/json")
    HttpCommandResultWithData JPushMes(@RequestBody JSONObject params);


    /**
     * 推送
     */
    @RequestMapping(method = RequestMethod.POST, value = "/sendMessage4Station", consumes = "application/json")
    HttpCommandResultWithData sendMessage4Station(@RequestBody PushMesForm form);
}
