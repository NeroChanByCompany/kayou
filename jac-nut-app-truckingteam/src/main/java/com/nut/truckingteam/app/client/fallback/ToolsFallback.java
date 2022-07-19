package com.nut.truckingteam.app.client.fallback;

import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.truckingteam.app.form.PushMesForm;
import org.springframework.stereotype.Component;


@Component
public class ToolsFallback
{
    /**
     * 指定消息推送
     */

    public HttpCommandResultWithData MessagePush(PushMesForm form) {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.FALLBACK.code());
        result.setMessage(ECode.FALLBACK.message());
        return result;
    }
}
