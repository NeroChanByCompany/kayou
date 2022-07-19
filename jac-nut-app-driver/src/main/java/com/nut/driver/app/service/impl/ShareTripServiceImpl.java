package com.nut.driver.app.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.utils.HttpUtil;
import com.nut.driver.app.entity.TrackInfoEntity;
import com.nut.driver.app.pojo.TrackPojo;
import com.nut.driver.app.service.ShareTripService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.service.impl
 * @Author: yzl
 * @CreateTime: 2021-06-30 09:22
 * @Version: 1.0
 */
@Service
@Slf4j
public class ShareTripServiceImpl implements ShareTripService {

    @Value("${queryLocationTrackUrl}")
    private String queryLocationTrackUrl;

    @Override
    @SneakyThrows
    public List<TrackInfoEntity> getTrack(String terminalId, Long startTime, Long endTime) {
        Map<String, Object> map = new HashMap<>(5);
        map.put("terminalId", terminalId);
//        map.put("terminalId", "17211315000");

        map.put("startTime", startTime);
//        map.put("startTime", "1626883200");

        map.put("endTime", endTime);
//        map.put("endTime", "1626969600");

        map.put("thin", false);
        map.put("thinLevel", "0");
        map.put("type", 1);
        List<TrackPojo> trackPojoList = HttpUtil.postJsonRequest(queryLocationTrackUrl, map, new TypeReference<HttpCommandResultWithData<List<TrackPojo>>>() {});
        List<TrackInfoEntity> trackInfoEntities = new ArrayList<>();
        if (trackPojoList != null && trackPojoList.size() > 0){
            TrackInfoEntity trackInfoEntity;
            for (TrackPojo trackPojo : trackPojoList){
                trackInfoEntity = new TrackInfoEntity();
                trackInfoEntity.set_x(trackPojo.getLongitude() / 1000000D);
                trackInfoEntity.set_y(trackPojo.getLatitude() / 1000000D);
                trackInfoEntity.set_v(trackPojo.getSpeed());
                trackInfoEntity.set_time(trackPojo.getGpsDate() * 1000);
                trackInfoEntity.setStandardMileage(trackPojo.getStandardMileage());
                trackInfoEntity.set_direction(trackPojo.getDirection());
                trackInfoEntities.add(trackInfoEntity);
            }
        }
        return trackInfoEntities;
    }
}
