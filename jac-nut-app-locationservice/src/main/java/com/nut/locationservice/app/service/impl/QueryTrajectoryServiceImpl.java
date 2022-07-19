package com.nut.locationservice.app.service.impl;


import com.nut.location.protocol.common.LCLocationData;
import com.nut.locationservice.app.form.QueryTrajectoryForm;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.app.locationService.service.impl
 * @Author: yzl
 * @CreateTime: 2021-06-17 13:43
 * @Version: 1.0
 */
@Service
@Slf4j
public class QueryTrajectoryServiceImpl {

    @Autowired
    CloudServiceImpl cloudService;

    @SneakyThrows
    public HttpCommandResultWithData queryTrajectory(QueryTrajectoryForm form) {

        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        List<LCLocationData.LocationData> locationData = cloudService.queryByteTrack(form.getTerminalId(), form.getStartTime() / 1000, form.getEndTime() / 1000, false, 11);
        if (CollectionUtils.isNotEmpty(locationData)) {
            result.setData(locationData);
        }
        return result;
    }

}
