package com.nut.driver.app.client;

import com.nut.driver.app.form.DbgCallUploadTspInfForm;
import com.nut.common.result.HttpCommandResultWithData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.app.driver.client
 * @Author: yzl
 * @CreateTime: 2021-06-23 17:51
 * @Version: 1.0
 */
@FeignClient(value = "jac-nut-app-servicestation")
public interface ServiceStationClient {

    /**
     * 触发评价回传
     *
     * @param form 工单号
     * @return HttpCommandResultWithData
     */
    @RequestMapping(method = RequestMethod.POST, value = "/triggerSendProcess", consumes = "application/json")
    HttpCommandResultWithData triggerSendProcess(DbgCallUploadTspInfForm form);


}
