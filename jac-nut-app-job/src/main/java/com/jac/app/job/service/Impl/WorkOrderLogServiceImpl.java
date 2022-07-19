package com.jac.app.job.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jac.app.job.entity.WorkOrderLogEntity;
import com.jac.app.job.mapper.WorkOrderLogMapper;
import com.jac.app.job.service.WorkOrderLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author liuBing
 * @Classname WorkOrderLogServiceImpl
 * @Description TODO
 * @Date 2021/8/16 9:58
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class WorkOrderLogServiceImpl extends ServiceImpl<WorkOrderLogMapper, WorkOrderLogEntity> implements WorkOrderLogService {
}
