package com.nut.driver.app.service;

import com.nut.driver.app.form.MyOrderDetailForm;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.driver.app.form.MyOrdersForm;

import java.util.Map;

/**
 * @Description: 我的预约服务
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.app.driver.service
 * @Author: yzl
 * @CreateTime: 2021-06-23 20:06
 * @Version: 1.0
 */
public interface MyOrderService {
    HttpCommandResultWithData myOrderDetail(MyOrderDetailForm form);
    Map myOrders(MyOrdersForm form);
}
