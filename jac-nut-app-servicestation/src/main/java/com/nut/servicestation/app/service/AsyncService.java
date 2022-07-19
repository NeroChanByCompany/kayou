package com.nut.servicestation.app.service;

import com.alibaba.fastjson.JSONArray;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.servicestation.app.dto.OrderReportDetailDto;
import com.nut.servicestation.app.dto.OrderReportDto;

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
     HttpCommandResultWithData pushTicket(OrderReportDetailDto orderReportDetailDto, JSONArray paramArray, OrderReportDto orderReportDto);
}
