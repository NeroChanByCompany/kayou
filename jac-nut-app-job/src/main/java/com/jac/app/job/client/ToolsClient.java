package com.jac.app.job.client;

import com.alibaba.fastjson.JSONObject;
import com.jac.app.job.client.fallback.ToolsClientFallback;
import com.jac.app.job.common.Result;
import com.jac.app.job.config.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author liuBing
 * @Classname ToolsClient
 * @Description TODO
 * @Date 2021/8/12 15:51
 */
@Component
@FeignClient(value = "jac-nut-app-tools",configuration = FeignConfiguration.class,fallback = ToolsClientFallback.class)
public interface ToolsClient {
    /**
     * 推送消息
     */
    @RequestMapping(method = RequestMethod.POST,value = "/JPushMes", consumes = "application/json")
    Result JPushMes(@RequestBody JSONObject params);
}
