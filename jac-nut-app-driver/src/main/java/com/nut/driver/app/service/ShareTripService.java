package com.nut.driver.app.service;

import com.nut.driver.app.entity.TrackInfoEntity;

import java.util.List;

/**
 * @Description: 行程相关
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.service
 * @Author: yzl
 * @CreateTime: 2021-06-30 09:20
 * @Version: 1.0
 */

public interface ShareTripService {
    List<TrackInfoEntity> getTrack(String terminalId, Long startTime, Long endTime);
}
