package com.nut.servicestation.app.client;


import com.nut.common.result.HttpCommandResultWithData;
import com.nut.servicestation.app.client.fallback.TspBusinessFallback;
import com.nut.servicestation.app.entity.CarGasInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestParam;


/**
 * @Author zhangling
 * @Date 2022/5/17 17:39
 * @Description tsp接口调用
 */
@FeignClient(value = "tsp-business-service",fallback = TspBusinessFallback.class)
public interface TspBusinessClient {

    /**
     * 获取车辆排放标准
     * @param chassisNum
     * @return
     */
    @GetMapping(value = "/api/jac/system/car/getByChassisNum",produces = "application/json;charset=UTF-8")
    HttpCommandResultWithData<CarGasInfo> getByChassisNum(@RequestParam(value = "chassisNum") String chassisNum);
}


