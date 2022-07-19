package com.nut.servicestation.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.servicestation.app.form.*;

/*
 *  @author wuhaotian 2021/7/5
 */
public interface AcceptRefuseOrderService {

    /**
     * 外出救援立即出发
     */
    HttpCommandResultWithData rescueGo(RescueGoForm command) throws Exception;
    /**
     * 拒绝接单
     */
    HttpCommandResultWithData refuseOrder(RefuseOrderForm command) throws JsonProcessingException;
    /**
     * 扫一扫/根据车辆查询工单
     */
    HttpCommandResultWithData scanWo(ScanWoForm command);
    /**
     * 确认接单
     */
    HttpCommandResultWithData acceptOrder(AcceptOrderForm command) throws JsonProcessingException;

    /**
     * 扫一扫快捷查单（新）
     */
    HttpCommandResultWithData scanOrder(ScanOrderForm form);
}
