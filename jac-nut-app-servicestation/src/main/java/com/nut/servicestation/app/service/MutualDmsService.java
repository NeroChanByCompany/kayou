package com.nut.servicestation.app.service;

import com.nut.common.result.HttpCommandResultWithData;
import com.nut.servicestation.app.form.PartForm;
import com.nut.servicestation.app.form.WoInfoForm;
import com.nut.servicestation.app.form.WorkOrderForm;

/*
 *  @author wuhaotian 2021/7/6
 */
public interface MutualDmsService {


    /**
     * 服务APP配件查询-车辆配置查询
     *
     * @param command 服务APP配件查询-车辆配置查询
     * @return 服务APP配件查询-车辆配置查询
     */
    HttpCommandResultWithData vehicleProductSearch(PartForm command) throws Exception;


    HttpCommandResultWithData uploadWorkOrderReport(WoInfoForm command) throws Exception;


}
