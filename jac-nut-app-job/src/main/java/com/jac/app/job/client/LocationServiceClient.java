package com.jac.app.job.client;


import com.jac.app.job.client.fallback.LocationServiceClientFallback;
import com.jac.app.job.common.Result;
import com.jac.app.job.config.FeignConfiguration;
import com.jac.app.job.vo.GetCarLastLocationByAutVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author liuBing
 * @Classname LocationServiceClient
 * @Description TODO
 * @Date 2021/8/12 16:48
 */
@FeignClient(value = "jac-nut-app-locationservice",configuration = FeignConfiguration.class, fallback = LocationServiceClientFallback.class)
public interface LocationServiceClient {
    /**
     * 查询位置云末次位置接口
     *
     * @param vo vo
     * @return Result
     */
    @RequestMapping(method = RequestMethod.POST, value = "/getCarLastLocationByAut", consumes = "application/json")
    Result<Map<Long, LinkedHashMap>> getCarLastLocationByAut(@RequestBody GetCarLastLocationByAutVO vo);
}
