package com.nut.servicestation.app.service;

import com.nut.servicestation.app.domain.WorkOrder;
import com.nut.servicestation.app.domain.WorkOrderTransferParts;
import com.nut.servicestation.app.form.RescueTransferPartsListForm;

import java.util.List;

/*
 *  @author wuhaotian 2021/7/6
 */
public interface RescueTransferPartsService {

    /**
     * 转换插入数据库的类型
     */
    List<WorkOrderTransferParts> getInsertEntities(List<RescueTransferPartsListForm> list, WorkOrder workOrder);
}
