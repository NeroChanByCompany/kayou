package com.jac.app.job.client;

import com.jac.app.job.client.fallback.ServiceStationClientFallback;
import com.jac.app.job.common.Result;
import com.jac.app.job.config.FeignConfiguration;
import com.jac.app.job.vo.WorkOrderVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author liuBing
 * @Classname ServiceStationClient
 * @Description TODO
 * @Date 2021/8/16 10:47
 */
@FeignClient(value = "jac-nut-app-servicestation" ,configuration = FeignConfiguration.class,fallback = ServiceStationClientFallback.class)
public interface ServiceStationClient {

}
