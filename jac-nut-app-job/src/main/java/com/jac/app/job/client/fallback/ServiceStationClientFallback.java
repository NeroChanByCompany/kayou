package com.jac.app.job.client.fallback;

import com.jac.app.job.client.ServiceStationClient;
import com.jac.app.job.common.Result;
import com.jac.app.job.config.FeignConfiguration;
import com.jac.app.job.hanlder.ExceptionUtil;
import com.jac.app.job.vo.WorkOrderVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

/**
 * @author liuBing
 * @Classname ServiceStationClient
 * @Description TODO
 * @Date 2021/8/16 10:47
 */
@Component
public class ServiceStationClientFallback implements ServiceStationClient {


}
