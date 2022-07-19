package com.nut.tripanalysis.app.client;

import com.nut.common.result.HttpCommandResultWithData;
import com.nut.driver.app.form.TripanalysisQueryModelInfoForm;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/*
 *  @author wuhaotian 2021/7/10
 */
@FeignClient("jac-nut-app-driver")
public interface DriverClient {

    /**
     * tripanalysis调用driver获取车队id
     */
    @RequestMapping(method = RequestMethod.POST, value = "/tripanalysisQueryModelInfo", consumes = "application/json")
    HttpCommandResultWithData tripanalysisQueryModelInfo(@RequestBody TripanalysisQueryModelInfoForm getCurrentCarCommand);
}
