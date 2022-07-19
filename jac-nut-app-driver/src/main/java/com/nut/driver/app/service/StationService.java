package com.nut.driver.app.service;

import com.nut.driver.app.form.QueryServiceStationDetailForm;
import com.nut.driver.app.form.QueryStationForm;
import com.nut.common.result.PagingInfo;
import com.nut.driver.app.dto.ServiceStationDetailDto;
import com.nut.driver.app.dto.StationListDto;


/**
 * @Description: 服务站相关
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.driver.service
 * @Author: yzl
 * @CreateTime: 2021-06-23 10:44
 * @Version: 1.0
 */

public interface StationService {

    /**
     * 获取服务站列表
     *
     * @param form
     * @return
     */
    PagingInfo<StationListDto> getStationList(QueryStationForm form);

    ServiceStationDetailDto appStationDetail(QueryServiceStationDetailForm form);
}
