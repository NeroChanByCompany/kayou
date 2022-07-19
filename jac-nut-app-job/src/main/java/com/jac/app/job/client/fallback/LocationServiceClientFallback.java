package com.jac.app.job.client.fallback;


import com.jac.app.job.client.LocationServiceClient;
import com.jac.app.job.common.Result;
import com.jac.app.job.vo.GetCarLastLocationByAutVO;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author liuBing
 * @Classname LocationServiceClientFallback
 * @Description TODO
 * @Date 2021/8/13 15:15
 */
@Component
public class LocationServiceClientFallback implements LocationServiceClient {

    @Override
    public Result<Map<Long, LinkedHashMap>> getCarLastLocationByAut(GetCarLastLocationByAutVO vo) {
        return Result.result(Result.NETWORK_FAIL_CODE,"远程服务调用失败!");
    }
}
