package com.nut.servicestation.app.service;

import com.nut.common.result.HttpCommandResultWithData;
import com.nut.servicestation.app.form.QueryStationForm;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.servicestation.app.service
 * @Author: yzl
 * @CreateTime: 2021-07-27 15:36
 * @Version: 1.0
 */
public interface AreaStationService {

    // 2021/07/28
    HttpCommandResultWithData queryStationList(QueryStationForm form);

}
