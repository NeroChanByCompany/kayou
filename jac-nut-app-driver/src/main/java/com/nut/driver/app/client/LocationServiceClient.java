package com.nut.driver.app.client;

import com.nut.driver.app.client.fallback.LocationServiceClientFallback;
import com.nut.driver.app.form.GetCarLastLocationByAutForm;
import com.nut.driver.app.form.GetCarLocationForm;
import com.nut.common.result.HttpCommandResultWithData;

import com.nut.locationservice.app.form.GetOnlineStatusForm;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationservice.app.locationService.client
 * @Author: yzl
 * @CreateTime: 2021-06-22 09:57
 * @Version: 1.0
 */
@FeignClient(value = "jac-nut-app-locationservice",fallback = LocationServiceClientFallback.class)
public interface LocationServiceClient {

    @PostMapping(value = "/carLocation", consumes = "application/json")
    HttpCommandResultWithData carLocation(GetCarLocationForm form);

    @PostMapping("/lastLocation")
    HttpCommandResultWithData lastLocation(List<Long> commIds);


    @PostMapping(value = "/getCarLastLocationByAut")
    HttpCommandResultWithData getCarLastLocationByAut(@RequestBody GetCarLastLocationByAutForm form);
    /**
     * 查询位置云车辆状态接口
     *
     * @param form GetCarOnlineStatusCommand
     * @return HttpCommandResultWithData
     */
    @RequestMapping(method = RequestMethod.POST, value = "/getOnlineStatus", consumes = "application/json")
    HttpCommandResultWithData getOnlineStatus(@RequestBody GetOnlineStatusForm form);

//    @PostMapping(value = "/getOnlineStatus")
//    HttpCommandResultWithData getOnlineStatus(@RequestBody GetOnlineStatusForm cloudCommand);
}
