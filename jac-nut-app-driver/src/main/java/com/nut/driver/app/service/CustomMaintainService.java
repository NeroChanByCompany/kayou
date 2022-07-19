package com.nut.driver.app.service;

import com.nut.common.result.PagingInfo;
import com.nut.driver.app.dto.MaintenanceInfoDTO;
import com.nut.driver.app.dto.QueryDriverMaintenanceDTO;
import com.nut.driver.app.form.AddMaintenanceInfoForm;
import com.nut.driver.app.form.EditMaintenanceInfoForm;
import com.nut.driver.app.form.QueryDriverMaintenanceListForm;
import com.nut.driver.app.form.QueryMaintenanceInfoForm;

/**
 * @author liuBing
 * @Classname CustomMaintainService
 * @Description TODO 自定义维护服务
 * @Date 2021/6/24 14:31
 */
public interface CustomMaintainService {
    /**
     * 查询自定义保养项
     * @param from
     * @return
     */
    PagingInfo<QueryDriverMaintenanceDTO> queryDriverMaintenanceList(QueryDriverMaintenanceListForm from);

    MaintenanceInfoDTO queryMaintanceInfo(QueryMaintenanceInfoForm form);

    void deleteMaintainceInfo(QueryMaintenanceInfoForm from);

    void addValidate(AddMaintenanceInfoForm from);

    void editValidate(EditMaintenanceInfoForm form);

    void editMaintainInfo(EditMaintenanceInfoForm form);
}
