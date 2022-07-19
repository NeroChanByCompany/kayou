package com.nut.driver.app;

import com.nut.driver.app.dto.QueryTrackDto;
import com.nut.driver.app.entity.TrackInfoEntity;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app
 * @Author: yzl
 * @CreateTime: 2021-06-30 09:26
 * @Version: 1.0
 */

public class TrackPointConvertor {

    public static QueryTrackDto map2dto (Map<String,String> encodeMap) {
        if (CollectionUtils.isEmpty(encodeMap)) {
            return null;
        }
        QueryTrackDto dto = new QueryTrackDto();
//            dto.setPoints(encodeMap.get("points"));
        dto.setLons(encodeMap.get("lons"));
        dto.setLats(encodeMap.get("lats"));
        dto.setSpeeds(encodeMap.get("speeds"));
        dto.setTimes(encodeMap.get("times"));
        dto.setInstantOils(encodeMap.get("oils"));
        dto.setDirections(encodeMap.get("directions"));
        dto.setLevels(encodeMap.get("levels"));
        dto.setFirstTime(encodeMap.get("firstTime"));
        return dto;
    }

    public static QueryTrackDto list2dto (List<TrackInfoEntity> trackInfoEntityList) {
        if (CollectionUtils.isEmpty(trackInfoEntityList)) {
            return null;
        }
        StringBuilder lons = new StringBuilder();
        StringBuilder lats = new StringBuilder();
        StringBuilder speeds = new StringBuilder();
        StringBuilder times = new StringBuilder();
        StringBuilder standardMileages = new StringBuilder();
        for (TrackInfoEntity trackInfoEntity :trackInfoEntityList){
            lons.append(trackInfoEntity.get_x()).append(",");
            lats.append(trackInfoEntity.get_y()).append(",");
            speeds.append(trackInfoEntity.get_v()).append(",");
            times.append(trackInfoEntity.get_time()).append(",");
            standardMileages.append(trackInfoEntity.getStandardMileage()).append(",");
        }
        QueryTrackDto dto = new QueryTrackDto();
//            dto.setPoints(encodeMap.get("points"));
        String lon = lons.toString();
        String lat = lats.toString();
        String time = times.toString();
        String speed = speeds.toString();
        String standardMileage = standardMileages.toString();
        dto.setLons(lon.substring(0,lon.lastIndexOf(",")));
        dto.setLats(lat.substring(0,lat.lastIndexOf(",")));
        dto.setTimes(time.substring(0, time.lastIndexOf(",")));
        dto.setSpeeds(speed.substring(0, speed.lastIndexOf(",")));
        dto.setStandardMileage(standardMileage.substring(0, standardMileage.lastIndexOf(",")));
        return dto;
    }
}
