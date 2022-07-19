package com.nut.servicestation.app.service;

import com.nut.common.result.HttpCommandResultWithData;
import org.springframework.scheduling.annotation.Async;

/*
 *  @author wuhaotian 2021/7/2
 */
public interface UptimeService {


    /**
     * 工单操作同步
     */
    void trigger(HttpCommandResultWithData result, String woCode, String step, String type, String mileage);
    /**
     * 工单操作同步（有延时）
     */
    void trigger(HttpCommandResultWithData result, String woCode, String step, String type, String mileage, Long delayMillis);
}
