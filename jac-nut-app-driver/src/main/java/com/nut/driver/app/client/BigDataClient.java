package com.nut.driver.app.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @Description: 调用大数据
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.client
 * @Author: yzl
 * @CreateTime: 2021-08-31 16:38
 * @Email: yzl379131121@163.com
 * @Version: 1.0
 */
@FeignClient("jac-location-rest")
public interface BigDataClient {

    @RequestMapping(method = RequestMethod.POST,value = "/location/last/search",consumes = "application/json")
    JSONObject queryColdCarInfo(@RequestBody JSONObject jsonObject);
}
