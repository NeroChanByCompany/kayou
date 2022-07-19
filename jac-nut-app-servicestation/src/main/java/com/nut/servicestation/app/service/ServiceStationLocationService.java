package com.nut.servicestation.app.service;

import com.nut.common.result.HttpCommandResultWithData;
import com.nut.servicestation.app.form.NearbyCarsForm;

/*
 *  @author wuhaotian 2021/7/7
 */
public interface ServiceStationLocationService {
    /**
     * 服务站周边车辆
     *
     * @param command 入参
     * @return result
     * @throws Exception
     */
    HttpCommandResultWithData queryNearbyCars(NearbyCarsForm command) throws Exception;
}
