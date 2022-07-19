package com.nut.servicestation.app.form;

import com.nut.common.base.BaseForm;
import lombok.Data;

/**
 * @Description: 查询发放优惠券的内容
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.servicestation.app.form
 * @Author: yzl
 * @CreateTime: 2021-08-03 13:27
 * @Version: 1.0
 */
@Data
public class QueryCouponListForm extends BaseForm {
    private String serviceCode;
}
