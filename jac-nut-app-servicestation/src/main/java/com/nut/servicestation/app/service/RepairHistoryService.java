package com.nut.servicestation.app.service;

import com.nut.common.result.HttpCommandResultWithData;
import com.nut.servicestation.app.form.RepairHistoryForm;

/*
 *  @author wuhaotian 2021/7/5
 */
public interface RepairHistoryService {

    /**
     * 维修历史查询
     * 根据操作状态，只查询维修记录
     *
     * @param command 接口参数
     * @return HttpCommandResultWithData
     */
    HttpCommandResultWithData queryRepairHistory(RepairHistoryForm command);
}
