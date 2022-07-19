package com.nut.driver.app.service;

import com.nut.driver.app.form.QueryAllAppointmentItemListForm;

import java.util.Map;

/**
 * @Description: 项目列表
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.driver.service
 * @Author: yzl
 * @CreateTime: 2021-06-23 13:44
 * @Version: 1.0
 */
public interface ItemsListService {

    Map orderItems(QueryAllAppointmentItemListForm form);

}
