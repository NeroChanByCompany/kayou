package com.nut.servicestation.app.service.impl;

import com.nut.common.constant.ServiceStationVal;
import com.nut.servicestation.app.domain.WorkOrder;
import com.nut.servicestation.app.domain.WorkOrderTransferParts;
import com.nut.servicestation.app.form.RescueTransferPartsListForm;
import com.nut.servicestation.app.service.RescueTransferPartsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
 *  @author wuhaotian 2021/7/6
 */
@Slf4j
@Service("RescueTransferPartsService")
public class RescueTransferPartsServiceImpl implements RescueTransferPartsService {
    @Override
    public List<WorkOrderTransferParts> getInsertEntities(List<RescueTransferPartsListForm> list, WorkOrder workOrder) {
        // 当前毫秒时间戳
        long timestampMs = System.currentTimeMillis();
        Date now = new Date();

        List<WorkOrderTransferParts> result = new ArrayList<>();
        for (RescueTransferPartsListForm part : list) {
            WorkOrderTransferParts entity = new WorkOrderTransferParts();
            entity.setWoCode(workOrder.getWoCode());
            entity.setWoType(workOrder.getWoType());
            // 外出救援的场合operate_id存提交时点的时间戳
            entity.setOperateId(String.valueOf(timestampMs));
            entity.setStatus(ServiceStationVal.TRANS_WAIT);
            // 零件信息
            entity.setPartsNo(part.getPartsNo());
            entity.setPartsName(part.getPartsName());
            entity.setPartsNum(Integer.valueOf(part.getPartsNum()));
            entity.setPartsFlag(Integer.valueOf(part.getPartsFlag()));
            entity.setInquireStatus(ServiceStationVal.INQ_UNDONE);
            entity.setCreateTime(now);
            entity.setUpdateTime(now);

            result.add(entity);
        }
        return result;
    }
}
