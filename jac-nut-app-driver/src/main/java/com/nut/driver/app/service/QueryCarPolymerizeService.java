package com.nut.driver.app.service;

import com.nut.driver.app.dto.MonitorDto;
import com.nut.driver.app.dto.QueryTrackDto;
import com.nut.driver.app.form.QueryCarPolymerizeForm;
import com.nut.driver.app.form.QueryTrackForm;

/**
 * @Description: 海量打点
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.service
 * @Author: yzl
 * @CreateTime: 2021-06-29 16:51
 * @Version: 1.0
 */
public interface QueryCarPolymerizeService {

    MonitorDto queryCarLocation(QueryCarPolymerizeForm form);

    QueryTrackDto queryTrack(QueryTrackForm form);
}
