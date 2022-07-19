package com.nut.driver.app.client;

import cn.hutool.json.JSONObject;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.driver.app.client.fallback.TspBusinessFallback;
import com.nut.driver.app.client.fallback.TspFallback;
import com.nut.driver.app.entity.CarBasicInfoEntity;
import com.nut.driver.app.form.CarBasicInfoForm;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @Description: TSP接口调用
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.client
 * @Author: liubing
 * @CreateTime: 2021-07-22 15:36
 * @Version: 1.0
 */
@FeignClient(value = "tsp-business-service",fallback = TspBusinessFallback.class)
public interface TspBusinessClient {
    /**
     * @Author liuBing
     * @Description //TODO 获取车辆基础信息
     * @Date 13:43 2021/8/3
     * @Param [carVin]
     * @return com.nut.driver.app.entity.CarBasicInfoEntity
     **/
    @PostMapping(value = "/api/jac/system/car/getCarBasicInfo",produces = "application/json;charset=UTF-8")
    HttpCommandResultWithData<CarBasicInfoEntity> getCarBasicInfo(@RequestBody Map<String,Object> param);
}
