package com.nut.servicestation.app.service;

import com.nut.common.result.HttpCommandResultWithData;
import com.nut.servicestation.app.form.*;

/**
 * @Description: 服务站发券
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.servicestation.app.service
 * @Author: yzl
 * @CreateTime: 2021-07-26 13:58
 * @Version: 1.0
 */

public interface Coupon2Service {

    HttpCommandResultWithData add(Coupon2Form form);

    HttpCommandResultWithData list(QueryCouponListForm form);

    HttpCommandResultWithData detail(QueryCouponDetailForm form);

}
