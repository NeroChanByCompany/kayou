package com.nut.driver.app.service.impl;

import com.nut.driver.app.form.DbgCallUploadTspInfForm;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.driver.app.client.ServiceStationClient;
import com.nut.driver.app.service.ServiceStationCallerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.app.driver.service.impl
 * @Author: yzl
 * @CreateTime: 2021-06-23 17:57
 * @Version: 1.0
 */
@Slf4j
@Service
public class ServiceStationCallerServiceImpl implements ServiceStationCallerService {

    @Autowired
    private ServiceStationClient serviceStationClient;

    @Async
    public void triggerSendProcess(HttpCommandResultWithData result, String woCode) {
        log.info("[triggerSendProcess]start");
        if (result.getResultCode() == ECode.SUCCESS.code()) {
            DbgCallUploadTspInfForm com = new DbgCallUploadTspInfForm();
            com.setWoCode(woCode);
            log.info("[triggerSendProcess]call servicestation triggerSendProcess");
            serviceStationClient.triggerSendProcess(com);
        }
        log.info("[triggerSendProcess]end");
    }



}
