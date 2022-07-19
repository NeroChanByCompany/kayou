package com.nut.servicestation.app.service;

import org.springframework.scheduling.annotation.Async;

/*
 *  @author wuhaotian 2021/7/5
 */
public interface AsyCheckPointCompletenessService {

    /**
     * 检查轨迹点是否完整
     */
    public void checkPointCompleteness(String woCode);
}
