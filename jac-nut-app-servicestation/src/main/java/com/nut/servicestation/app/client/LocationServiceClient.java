package com.nut.servicestation.app.client;

import com.nut.common.result.HttpCommandResultWithData;
import com.nut.locationservice.app.form.GetCarLastLocationForm;
import com.nut.locationservice.app.form.GetCarLocationForm;
import com.nut.locationservice.app.form.GetOnlineStatusForm;
import com.nut.servicestation.app.client.fallback.LocationServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/*
 *  @author wuhaotian 2021/7/5
 */
@FeignClient(value = "jac-nut-app-locationservice", fallback = LocationServiceFallback.class)
public interface LocationServiceClient {

    /**
     * 查询位置云末次位置
     *
     * @param getCarLocationCommand
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/carLocation", consumes = "application/json")
    HttpCommandResultWithData carLocation(@RequestBody GetCarLocationForm getCarLocationCommand);
    /**
     * 查询位置云车辆状态接口
     *
     * @param command GetCarOnlineStatusCommand
     * @return HttpCommandResultWithData
     */
    @RequestMapping(method = RequestMethod.POST, value = "/getOnlineStatus", consumes = "application/json")
    HttpCommandResultWithData getOnlineStatus(@RequestBody GetOnlineStatusForm command);
    /**
     * @param getCarLastLocationCommand
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/carLastLocationStation", consumes = "application/json")
    HttpCommandResultWithData getCarLastLocationToStation(@RequestBody GetCarLastLocationForm getCarLastLocationCommand);
}
