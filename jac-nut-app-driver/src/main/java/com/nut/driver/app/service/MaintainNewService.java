package com.nut.driver.app.service;

import com.nut.common.result.PagingInfo;
import com.nut.driver.app.dto.CarDetailedMaintenanceDTO;
import com.nut.driver.app.dto.CarMaintainDetailDataDTO;
import com.nut.driver.app.dto.CarMaintainRecordDTO;
import com.nut.driver.app.form.QueryCarMaintainListForm;
import com.nut.driver.app.form.QueryCarNewMaintainDetailsForm;
import com.nut.driver.app.form.UpdateCarNewMaintainFrom;
import com.nut.driver.app.pojo.MaintainPojo;
import com.nut.driver.app.pojo.OldMaintainPojo;
import com.nut.driver.app.pojo.PhyExaPojo;

/**
 * @author liuBing
 * @Classname MaintainNewService
 * @Description TODO 推荐保养相关
 * @Date 2021/6/23 17:17
 */
public interface MaintainNewService {

    @Deprecated
    PagingInfo<OldMaintainPojo> queryCarMaintainPage(QueryCarMaintainListForm from);

    PagingInfo<MaintainPojo> getCarMaintainPage(QueryCarMaintainListForm from);

    CarMaintainDetailDataDTO queryCarNewMaintainDetails(QueryCarNewMaintainDetailsForm from);

    CarDetailedMaintenanceDTO getCarNewMaintainDetails(QueryCarNewMaintainDetailsForm from);

    PagingInfo<CarMaintainRecordDTO>  queryNewMaintainRecord(QueryCarNewMaintainDetailsForm from);

    void updateCarNewMaintain(UpdateCarNewMaintainFrom from);
}
