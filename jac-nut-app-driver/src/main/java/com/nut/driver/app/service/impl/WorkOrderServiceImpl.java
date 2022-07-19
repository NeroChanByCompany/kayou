package com.nut.driver.app.service.impl;

import com.nut.common.enums.ServiceStationEnum;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.nut.driver.app.dao.WorkOrderDao;
import com.nut.driver.app.entity.WorkOrderEntity;
import com.nut.driver.app.service.WorkOrderService;

@Service("workOrderService")
@Slf4j
public class WorkOrderServiceImpl extends ServiceImpl<WorkOrderDao, WorkOrderEntity> implements WorkOrderService {


    public Long queryOtwOrderNum(Long autoIncreaseId) {
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        result.setResultCode(ECode.SUCCESS.code());
        //已完结工单状态
        //200	工单关闭-关闭申请-检查
        //210	工单关闭-关闭申请-维修
        //120	工单关闭-拒单
        //240	已完成	维修结束，工单流程结束。
        //125   取消预约
        //210	工单关闭-关闭申请-待接车
        Long otwOrderNum = this.baseMapper.queryOtwOrderNum(autoIncreaseId,
                ServiceStationEnum.CLOSE_INSPECT.code(), ServiceStationEnum.CLOSE_REPAIR.code(),
                ServiceStationEnum.CLOSE_REFUSED.code(), ServiceStationEnum.WORK_DONE.code(),
                ServiceStationEnum.CANCEL_ORDER.code(),ServiceStationEnum.CLOSE_TAKEOFF.code(),
                ServiceStationEnum.CLOSE_RECEIVE.code(),ServiceStationEnum.CLOSE_APPLYING_TAKEOFF.code());
        return otwOrderNum;
    }
}
