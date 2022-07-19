package com.nut.driver.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.driver.app.form.*;
import org.springframework.transaction.annotation.Transactional;

public interface FleetService {

    /**
     * 列表
     *
     * @param command 接口参数
     * @return HttpCommandResultWithData
     */
    HttpCommandResultWithData query(UserFleetsForm command);
    /**
     * 详情
     *
     * @param command 接口参数
     * @return HttpCommandResultWithData
     */
    HttpCommandResultWithData detail(UserFleetDetailForm command) throws JsonProcessingException;
    /**
     * 编辑
     *
     * @param command 接口参数
     * @return HttpCommandResultWithData
     */
    @Transactional(rollbackFor = Exception.class)
    HttpCommandResultWithData update(UserFleetUpdateForm command);
    /**
     * 创建
     *
     * @param command 接口参数
     * @return HttpCommandResultWithData
     */
    @Transactional(rollbackFor = Exception.class)
    HttpCommandResultWithData add(UserFleetAddForm command);
    /**
     * 删除
     *
     * @param command 接口参数
     * @return HttpCommandResultWithData
     */
    @Transactional(rollbackFor = Exception.class)
    HttpCommandResultWithData delete(UserFleetDeleteForm command);
}
