package com.nut.driver.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.PagingInfo;
import com.nut.driver.app.dto.FleetCarsDto;
import com.nut.driver.app.form.*;
import org.springframework.transaction.annotation.Transactional;

public interface FleetCarService {

    /**
     * 车辆与司机绑定
     *
     * @param command 接口参数
     * @return HttpCommandResultWithData
     */
    @Transactional(rollbackFor = Exception.class)
    HttpCommandResultWithData carDriverBind(CarDriverBindForm command);
    /**
     * 车辆与司机解绑
     *
     * @param command 接口参数
     * @return HttpCommandResultWithData
     */
    @Transactional(rollbackFor = Exception.class)
    HttpCommandResultWithData carDriverUnbind(CarDriverUnbindForm command);
    /**
     * 车队车辆列表
     *
     * @param command 接口参数
     * @return HttpCommandResultWithData
     */
    HttpCommandResultWithData<PagingInfo<FleetCarsDto>> query(FleetCarsForm command) throws JsonProcessingException;
    /**
     * 车队与车辆绑定
     *
     * @param command 接口参数
     * @return HttpCommandResultWithData
     */
    @Transactional(rollbackFor = Exception.class)
    HttpCommandResultWithData bind(FleetCarBindForm command);
    /**
     * 车队与车辆解绑
     *
     * @param command 接口参数
     * @return HttpCommandResultWithData
     */
    @Transactional(rollbackFor = Exception.class)
    HttpCommandResultWithData unbind(FleetCarUnbindForm command);
    /**
     * 车队车辆详情
     *
     * @param command 接口参数
     * @return HttpCommandResultWithData
     */
    HttpCommandResultWithData detail(FleetCarDetailForm command) throws JsonProcessingException;
}
