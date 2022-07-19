package com.nut.servicestation.app.service.impl;

import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.servicestation.app.form.UptimeCenterDebugForm;
import com.nut.servicestation.app.service.UptimeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/*
 *  @author wuhaotian 2021/7/2
 */
@Slf4j
@Service("UptimeService")
public class UptimeServiceImpl implements UptimeService {

    /**
     * 同步完好率中心接口开关
     */
    @Value("${syncUptimeSwitch:false}")
    private boolean uptimeSwitch;


    @Override
    @Async
    public void trigger(HttpCommandResultWithData result, String woCode, String step, String type, String mileage) {
        log.info("[trigger]start||{}", result.getResultCode());
        if (uptimeSwitch && result.getResultCode() == ECode.SUCCESS.code()) {
            UptimeCenterDebugForm com = new UptimeCenterDebugForm();
            com.setKey(woCode);
            com.setStep(step);
            com.setType(type);
            com.setMileage(mileage);
        }
        log.info("[trigger]end");
    }

    @Override
    @Async
    public void trigger(HttpCommandResultWithData result, String woCode, String step, String type, String mileage, Long delayMillis) {
        // 等待
        if (delayMillis != null) {
            sleep(delayMillis);
        }
        trigger(result, woCode, step, type, mileage);
    }

    /**
     * 延时
     */
    private void sleep(Long delayMillis) {
        try {
            log.info("[trigger] sleep millis:" + delayMillis);
            Thread.sleep(delayMillis);
        } catch (InterruptedException e) {
            log.error("[trigger] sleep error:" + e);
        }
    }




}
