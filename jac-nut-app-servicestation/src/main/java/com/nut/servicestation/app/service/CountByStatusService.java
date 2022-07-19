package com.nut.servicestation.app.service;

import com.nut.common.result.HttpCommandResultWithData;
import com.nut.servicestation.app.form.CountByStatusForm;

/*
 *  @author wuhaotian 2021/7/6
 */
public interface CountByStatusService {

    /**
     * 统计各阶段待处理工单数量
     *
     * @param command 接口参数
     * @return HttpCommandResultWithData
     */
    HttpCommandResultWithData countByStatus(CountByStatusForm command);
}
