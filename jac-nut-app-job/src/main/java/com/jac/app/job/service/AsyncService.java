package com.jac.app.job.service;

import com.alibaba.fastjson.JSONArray;
import com.jac.app.job.dto.OrderReportDetailDto;
import com.jac.app.job.dto.OrderReportDto;


/**
 * @author liuBing
 * @Classname AsyncService
 * @Description TODO
 * @Date 2021/8/16 14:08
 */
public interface AsyncService {
    /**
     * 异步推送工单
     * @param orderReportDetailDto
     * @param paramArray
     * @param orderReportDto
     */
     void pushTicket(OrderReportDetailDto orderReportDetailDto, JSONArray paramArray, OrderReportDto orderReportDto);
}
