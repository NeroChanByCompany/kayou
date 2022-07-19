package com.nut.servicestation.app.service;

import com.nut.servicestation.app.domain.WorkOrder;

/*
 *  @author wuhaotian 2021/7/7
 */
public interface ApplysCheckService {

    /**
     * 工单关闭推送
     */
    void pushWoClose(WorkOrder workOrder, String senderId);
}
