package com.nut.locationservice.app.service;

import com.nut.locationservice.app.module.LastLocationResponse;

import java.util.List;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.app.locationService.service
 * @Author: yzl
 * @CreateTime: 2021-06-16 19:40
 * @Version: 1.0
 */

public interface CloudService {

    List<LastLocationResponse> queryJsonLastLocation(List<Long> commIds);

}
