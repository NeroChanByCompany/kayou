package com.nut.driver.app.client.fallback;

import com.alibaba.fastjson.JSONObject;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.Result;
import com.nut.driver.app.client.ToolsClient;
import com.nut.driver.app.form.PushMesForm;
import com.nut.driver.app.form.SendSmsForm;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.Map;

public class ToolsClientFallback implements ToolsClient {
    @Override
    public HttpCommandResultWithData MessagePush(PushMesForm form) {
        return null;
    }

    @Override
    public Map sendSms(SendSmsForm form) {
        return null;
    }

    @Override
    public HttpCommandResultWithData JPushMes(JSONObject params){return null;}

    @Override
    public HttpCommandResultWithData sendMessage4Station(PushMesForm form) {
        return Result.result(ECode.FALLBACK.code(),ECode.FALLBACK.message());
    }

}
