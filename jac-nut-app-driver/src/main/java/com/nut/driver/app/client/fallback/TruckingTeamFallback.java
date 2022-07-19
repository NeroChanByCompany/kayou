package com.nut.driver.app.client.fallback;

import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.driver.app.client.TruckingTeamClient;
import com.nut.truckingteam.app.form.PushDriverOwnerMsgForm;
import com.nut.driver.app.form.GetDriverCarsForm;

public class TruckingTeamFallback implements TruckingTeamClient {

    @Override
    public HttpCommandResultWithData pushDriverOwnerMsg(PushDriverOwnerMsgForm command) {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.FALLBACK.code());
        result.setMessage(ECode.FALLBACK.message());
        return result;
    }

    @Override
    public HttpCommandResultWithData getDriverCars(GetDriverCarsForm form) {
        return null;
    }
}
