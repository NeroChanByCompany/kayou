package com.nut.servicestation.app.service;

import com.nut.common.result.HttpCommandResultWithData;
import com.nut.servicestation.app.form.StaffListForm;

/*
 *  @author wuhaotian 2021/7/8
 */
public interface RescueInfoQueryService {

    /**
     * 查询指派人员列表
     */
    HttpCommandResultWithData staffList(StaffListForm command);
}
