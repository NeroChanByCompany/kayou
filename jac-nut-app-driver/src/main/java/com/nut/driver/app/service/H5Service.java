package com.nut.driver.app.service;

import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.Result;
import com.nut.driver.app.form.InviteIntegralForm;

import java.util.List;

/**
 * @Description: h5页面调用服务
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.service
 * @Author: yzl
 * @CreateTime: 2021-11-17 15:46
 * @Email: yzl379131121@163.com
 * @Version: 1.0
 */
public interface H5Service {

    HttpCommandResultWithData inviteIntegral(InviteIntegralForm form);
}
