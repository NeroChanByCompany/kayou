package com.nut.servicestation.app.client;

import com.nut.servicestation.app.client.fallback.ToolsFallback;
import com.nut.tools.app.form.PushMesForm;
import com.nut.tools.app.form.SendSmsForm;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

/*
 *  @author wuhaotian 2021/7/2
 */
@FeignClient(value = "jac-nut-app-tools", fallback = ToolsFallback.class)
public interface ToolsClient {

    /**
     * 推送
     */
    @RequestMapping(method = RequestMethod.POST, value = "/PushMes", consumes = "application/json")
    Map MessagePush(@RequestBody PushMesForm command);
    /**
     * 发送短信
     */
    @RequestMapping(method = RequestMethod.POST, value = "/sendSms", consumes = "application/json")
    Map sendSms(@RequestBody SendSmsForm command);
}
