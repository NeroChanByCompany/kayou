package com.nut.driver.app.client;

import com.alibaba.fastjson.JSONObject;
import com.nut.common.result.HttpCommandResultWithData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @Description: 直销商城
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.client
 * @Author: yzl
 * @CreateTime: 2021-07-23 14:44
 * @Version: 1.0
 */
@FeignClient(value = "jac-mall-zhixiao-api")
public interface ZhixiaoClient {

    @RequestMapping(method = RequestMethod.POST, value = "/zhixiao/api/recommend/driver/bind", consumes = "application/json")
    HttpCommandResultWithData bindSignal(@RequestBody JSONObject jsonObject);

    @GetMapping(value = "/zhixiao/api/recommend/driver/getByToPhone")
    HttpCommandResultWithData getByToPhone(@RequestParam("toPhone") String toPhone);

}
