package com.nut.servicestation.app.service;

/*
 *  @author wuhaotian 2021/7/5
 */
public interface DistanceAnomalyService {

    /**
     * 根据计算公式判断距离是否在阈值外
     *
     * @param mapMileage    推荐导航里程
     * @param actualMileage 实际救援里程
     * @return true:超出正常范围
     */
    boolean isDistanceOverLimit(Integer mapMileage, Integer actualMileage);
}
