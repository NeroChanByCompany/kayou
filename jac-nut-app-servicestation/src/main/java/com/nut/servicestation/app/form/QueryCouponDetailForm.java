package com.nut.servicestation.app.form;

import com.nut.common.base.BaseForm;
import lombok.Data;

/**
 * @Description: 查询服务站发放的优惠券的详情
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.servicestation.app.form
 * @Author: yzl
 * @CreateTime: 2021-08-03 13:45
 * @Version: 1.0
 */
@Data
public class QueryCouponDetailForm extends BaseForm {
    private String couponId;
}
