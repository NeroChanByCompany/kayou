package com.nut.driver.app.service;

import com.nut.common.result.HttpCommandResultWithData;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @Description: servicestation模块调用service
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.app.driver.service
 * @Author: yzl
 * @CreateTime: 2021-06-23 17:50
 * @Version: 1.0
 */
@EnableAsync
public interface ServiceStationCallerService {

    void triggerSendProcess(HttpCommandResultWithData result, String woCode);


}
