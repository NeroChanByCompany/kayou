package com.nut.driver.app.client.fallback;

import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.driver.app.client.LocationServiceClient;
import com.nut.driver.app.form.GetCarLastLocationByAutForm;
import com.nut.driver.app.form.GetCarLocationForm;
import com.nut.locationservice.app.form.GetOnlineStatusForm;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LocationServiceClientFallback implements LocationServiceClient {

    @Override
    public HttpCommandResultWithData carLocation(GetCarLocationForm form) {
        return null;
    }

    @Override
    public HttpCommandResultWithData lastLocation(List<Long> commIds) {
        return null;
    }

    @Override
    public HttpCommandResultWithData getCarLastLocationByAut(GetCarLastLocationByAutForm form) {
        return null;
    }

    @Override
    public HttpCommandResultWithData getOnlineStatus(GetOnlineStatusForm command) {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.FALLBACK.code());
        return result;
    }
}
