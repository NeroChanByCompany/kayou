package com.nut.servicestation.app.service.impl;

import com.nut.servicestation.app.service.DistanceAnomalyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/*
 *  @author wuhaotian 2021/7/5
 */
@Slf4j
@Service("DistanceAnomalyService")
public class DistanceAnomalyServiceImpl implements DistanceAnomalyService {


    @Value("${rescue_distance_difference:30}")
    private Integer rescueDistanceDifference;

    @Override
    public boolean isDistanceOverLimit(Integer mapMileage, Integer actualMileage) {
        if (mapMileage == null || actualMileage == null || mapMileage == 0) {
            log.info("[isDistanceOverLimit]mapMileage:{}||actualMileage:{}", mapMileage, actualMileage);
            return true;
        }
        return Math.abs(actualMileage - mapMileage) * 100 / mapMileage >= rescueDistanceDifference;
    }
}
