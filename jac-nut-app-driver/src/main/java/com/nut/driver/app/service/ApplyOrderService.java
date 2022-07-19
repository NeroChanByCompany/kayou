package com.nut.driver.app.service;

import com.nut.driver.app.form.ApplyOrderForm;
import com.nut.driver.app.form.CancelOrderForm;
import com.nut.common.result.HttpCommandResultWithData;


import java.util.Map;

/**
 * @Description: 服务预约
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.driver.service
 * @Author: yzl
 * @CreateTime: 2021-06-21 19:29
 * @Version: 1.0
 */
public interface ApplyOrderService{

    Map applyOrder(ApplyOrderForm form);

    HttpCommandResultWithData cancleOrder(CancelOrderForm form);
}
