package com.nut.truckingteam.app.client;

import com.nut.common.result.HttpCommandResultWithData;
import com.nut.truckingteam.app.form.PushMesForm;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
}
