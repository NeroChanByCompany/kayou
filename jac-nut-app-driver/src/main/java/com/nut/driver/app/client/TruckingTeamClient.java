package com.nut.driver.app.client;

import com.nut.common.result.HttpCommandResultWithData;
import com.nut.driver.app.form.GetDriverCarsForm;
import com.nut.truckingteam.app.form.PushDriverOwnerMsgForm;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.client
 * @Author: yzl
 * @CreateTime: 2021-06-27 13:55
 * @Version: 1.0
 */
@FeignClient(value = "jac-nut-app-truckingteam")
public interface TruckingTeamClient {
    /**
     * 司机车队消息统一推送出口
     *
     * @param command 接口参数
     * @return HttpCommandResultWithData
     */
    @PostMapping(value = "/pushDriverOwnerMsg", consumes = "application/json")
    HttpCommandResultWithData pushDriverOwnerMsg(@RequestBody PushDriverOwnerMsgForm command);


    /**
     * 取得车辆信息
     *
     * @param form 检索条件
     * @return 我的车辆信息
     */
    @RequestMapping(method = RequestMethod.POST, value = "/getDriverCars", consumes = "application/json")
    HttpCommandResultWithData getDriverCars(@RequestBody GetDriverCarsForm form);

}
