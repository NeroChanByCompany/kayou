package com.jac.app.job.client.fallback;

import com.alibaba.fastjson.JSONObject;
import com.jac.app.job.client.ToolsClient;
import com.jac.app.job.common.Result;
import org.springframework.stereotype.Component;

/**
 * @author liuBing
 * @Classname ToolsClientFallback
 * @Description TODO
 * @Date 2021/8/12 15:52
 */
@Component
public class ToolsClientFallback implements ToolsClient {
    @Override
    public Result JPushMes(JSONObject params) {
        return Result.result(Result.NETWORK_FAIL_CODE,"远程服务调用失败!");
    }
}
