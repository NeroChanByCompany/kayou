package com.nut.driver.app.service;

import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.PagingInfo;
import com.nut.driver.app.dto.FleetAdminsDto;
import com.nut.driver.app.form.FleetAdminQuitForm;
import com.nut.driver.app.form.FleetAdminsForm;
import org.springframework.transaction.annotation.Transactional;

public interface AdminService {

    /**
     * 列表
     *
     * @param command 接口参数
     * @return HttpCommandResultWithData
     */
    HttpCommandResultWithData<PagingInfo<FleetAdminsDto>> query(FleetAdminsForm command);
    /**
     * 退出车队
     *
     * @param command 接口参数
     * @return HttpCommandResultWithData
     */
    @Transactional(rollbackFor = Exception.class)
    HttpCommandResultWithData quit(FleetAdminQuitForm command);
}
