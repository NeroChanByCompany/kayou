package com.nut.driver.app.service;

import com.github.pagehelper.Page;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.PagingInfo;
import com.nut.driver.app.form.AppUrgentCallForm;
import com.nut.driver.app.form.QueryAppUrgentCallForm;

/**
 * @Description: 功能 紧急电话服务查询接口 APP端使用
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.service
 * @Author: yzl
 * @CreateTime: 2021-06-28 14:43
 * @Version: 1.0
 */

public interface QueryAppUrgentCallService {

    void urgentCall(AppUrgentCallForm form);

    PagingInfo query(QueryAppUrgentCallForm form);
}
