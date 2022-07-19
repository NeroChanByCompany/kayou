package com.nut.servicestation.app.client.fallback;

import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.locationservice.app.form.GetCarLastLocationForm;
import com.nut.locationservice.app.form.GetCarLocationForm;
import com.nut.locationservice.app.form.GetOnlineStatusForm;
import com.nut.servicestation.app.client.LocationServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/*
 *  @author wuhaotian 2021/7/5
 */
@Slf4j
@Component
public class LocationServiceFallback implements LocationServiceClient {
    @Override
    public HttpCommandResultWithData carLocation(GetCarLocationForm getCarLocationCommand) {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        log.info("调用locationService模块 carLocation 接口出现fallback");
        result.setResultCode(ECode.FALLBACK.code());
        result.setMessage(ECode.FALLBACK.message());
        return result;
    }

    @Override
    public HttpCommandResultWithData getOnlineStatus(GetOnlineStatusForm command) {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        log.info("调用locationService模块 getOnlineStatus 接口出现fallback");
        result.setResultCode(ECode.FALLBACK.code());
        result.setMessage(ECode.FALLBACK.message());
        return result;
    }

    @Override
    public HttpCommandResultWithData getCarLastLocationToStation(GetCarLastLocationForm getCarLastLocationCommand) {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        log.info("调用locationService模块 getCarLastLocationToStation 接口出现fallback");
        result.setResultCode(ECode.FALLBACK.code());
        result.setMessage(ECode.FALLBACK.message());
        return result;
    }
}
