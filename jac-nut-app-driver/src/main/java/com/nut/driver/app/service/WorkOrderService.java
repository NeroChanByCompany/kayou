package com.nut.driver.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nut.driver.app.entity.WorkOrderEntity;

/**
 *
 *
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-16 15:49:32
 */
public interface WorkOrderService extends IService<WorkOrderEntity> {

    Long queryOtwOrderNum(Long autoIncreaseId);

}

