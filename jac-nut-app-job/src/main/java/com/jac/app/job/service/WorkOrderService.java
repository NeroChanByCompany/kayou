package com.jac.app.job.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jac.app.job.entity.WorkOrderEntity;
import com.jac.app.job.entity.WorkOrderLogEntity;
import com.jac.app.job.vo.WorkOrderVo;

/**
 * @author liuBing
 * @Classname WorkOrderLogService
 * @Description TODO
 * @Date 2021/8/16 9:58
 */
public interface WorkOrderService extends IService<WorkOrderEntity> {

    /**
     * @Author liuBing
     * @Description //TODO 定时任务重新上传失败工单
     * @Date 10:33 2021/8/16
     * @Param [form]
     * @return java.lang.String
     **/
    String pushWorkOrder(WorkOrderEntity entity);
}
