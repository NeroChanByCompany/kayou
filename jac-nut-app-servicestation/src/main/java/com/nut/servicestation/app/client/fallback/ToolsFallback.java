package com.nut.servicestation.app.client.fallback;

import com.nut.common.result.ECode;
import com.nut.servicestation.app.client.ToolsClient;
import com.nut.tools.app.form.PushMesForm;
import com.nut.tools.app.form.SendSmsForm;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/*
 *  @author wuhaotian 2021/7/2
 */
@Component
public class ToolsFallback implements ToolsClient {
    @Override
    public Map MessagePush(PushMesForm command) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", String.valueOf(ECode.FALLBACK.code()));
        result.put("msg", ECode.FALLBACK.message());
        return result;
    }

    @Override
    public Map sendSms(SendSmsForm command) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", String.valueOf(ECode.FALLBACK.code()));
        result.put("msg", ECode.FALLBACK.message());
        return result;
    }
}
