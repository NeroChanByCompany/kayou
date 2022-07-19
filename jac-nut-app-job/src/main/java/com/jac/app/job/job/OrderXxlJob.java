package com.jac.app.job.job;

import com.jac.app.job.service.OrderService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


/**
 * 任务
 *
 * @author MengJinyue
 * @date 2021-02-02 16:21:49
 */
@Component
@Slf4j
public class OrderXxlJob {
    @Resource
    private OrderService orderService;

    /**
     * 定时查询是否支付成功
     * @author MengJinyue
     */
    @XxlJob("searchPaySuccessFlag")
    public ReturnT<String> searchPaySuccessFlag(String param){
        log.info("[searchPaySuccessFlag,start]");
        Long startTime = System.currentTimeMillis();
        orderService.searchPaySuccessFlag();
        log.info("[searchPaySuccessFlag,end,cost={}s]",(System.currentTimeMillis() - startTime) / 1000);
        XxlJobLogger.log("[searchPaySuccessFlag,end,cost={}s]",(System.currentTimeMillis() - startTime) / 1000);

        return ReturnT.SUCCESS;
    }

    /**
     * 定时远程提交套餐订单
     * @author MengJinyue
     */
    @XxlJob("searchSimOrderFlag")
    public ReturnT<String> searchSimOrderFlag(String param){
        log.info("[searchSimOrderFlag,start]");
        Long startTime = System.currentTimeMillis();
        orderService.searchSimOrderFlag();
        log.info("[searchSimOrderFlag,end,cost={}s]",(System.currentTimeMillis() - startTime) / 1000);
        XxlJobLogger.log("[searchSimOrderFlag,end,cost={}s]",(System.currentTimeMillis() - startTime) / 1000);

        return ReturnT.SUCCESS;
    }
}
