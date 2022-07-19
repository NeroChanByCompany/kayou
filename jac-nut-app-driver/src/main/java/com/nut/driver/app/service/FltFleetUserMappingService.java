package com.nut.driver.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.driver.app.entity.FltFleetUserMappingEntity;
import com.nut.driver.app.form.FleetAdminBindForm;
import com.nut.driver.app.form.FleetAdminUnbindForm;

/**
 * 车队与用户绑定关系表
 *
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-16 15:48:59
 */
public interface FltFleetUserMappingService extends IService<FltFleetUserMappingEntity> {

    HttpCommandResultWithData bind(FleetAdminBindForm form);

    HttpCommandResultWithData unbind(FleetAdminUnbindForm form);

}

