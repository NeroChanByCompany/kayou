package com.nut.servicestation.app.client;

import com.alibaba.fastjson.JSONObject;
import com.nut.servicestation.app.form.QueryOrderPointForm;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @Description: 大数据接口调用
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.servicestation.app.client
 * @Author: yzl
 * @CreateTime: 2021-08-26 08:53
 * @Email: yzl379131121@163.com
 * @Version: 1.0
 */
@FeignClient("jac-location-rest")
public interface BigDataClient {

    @RequestMapping(method = RequestMethod.POST,value = "/location/track/queryOrder",consumes = "application/json")
    JSONObject queryOrderPoint(@RequestBody QueryOrderPointForm form);
}
