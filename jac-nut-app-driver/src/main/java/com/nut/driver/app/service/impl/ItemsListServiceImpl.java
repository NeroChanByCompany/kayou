package com.nut.driver.app.service.impl;

import com.nut.driver.app.dao.AppointmentItemsDao;
import com.nut.driver.app.dto.AppointMatinTainDto;
import com.nut.driver.app.form.QueryAllAppointmentItemListForm;
import com.nut.driver.app.service.ItemsListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.driver.service.impl
 * @Author: yzl
 * @CreateTime: 2021-06-23 13:48
 * @Version: 1.0
 */
@Slf4j
@Service
public class ItemsListServiceImpl extends DriverBaseService implements ItemsListService {

    @Autowired
    private AppointmentItemsDao appointmentItemsDao;

    public Map orderItems(QueryAllAppointmentItemListForm form){
        Map<String, List<AppointMatinTainDto>> map = new HashMap<>(2);
        // 维修项目
        map.put("repairList", appointmentItemsDao.queryRepairList("1"));
        // 保养项目
        map.put("maintainList", appointmentItemsDao.queryRepairList("2"));
        log.info("orderItems end return:{}",map);
        return map;
    }

}
