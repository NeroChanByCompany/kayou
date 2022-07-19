package com.nut.driver.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.PagingInfo;
import com.nut.driver.app.dto.FleetDriversDto;
import com.nut.driver.app.form.*;
import org.springframework.transaction.annotation.Transactional;

public interface DriverService {

    /**
     * 详情
     *
     * @param command 接口参数
     * @return HttpCommandResultWithData
     */
    HttpCommandResultWithData detail(FleetDriverDetailForm command) throws JsonProcessingException;
    /**
     * 绑定
     *
     * @param command 接口参数
     * @return HttpCommandResultWithData
     */
    HttpCommandResultWithData bind(FleetDriverBindForm command);
    /**
     * 解绑
     *
     * @param command 接口参数
     * @return HttpCommandResultWithData
     */
    HttpCommandResultWithData unbind(FleetDriverUnbindForm command);
    /**
     * 列表
     *
     * @param command 接口参数
     * @return HttpCommandResultWithData
     */
    HttpCommandResultWithData<PagingInfo<FleetDriversDto>> query(FleetDriversForm command);
    /**
     * 退出车队
     *
     * @param command 接口参数
     * @return HttpCommandResultWithData
     */
    @Transactional(rollbackFor = Exception.class)
    HttpCommandResultWithData quit(FleetDriverQuitForm command);
}
