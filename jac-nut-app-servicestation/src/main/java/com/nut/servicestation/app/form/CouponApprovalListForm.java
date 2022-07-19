package com.nut.servicestation.app.form;


import com.nut.common.base.BaseForm;
import com.nut.common.utils.RegexpUtils;
import lombok.Data;

import javax.validation.constraints.Pattern;

/**
 * @description: 核销记录
 **/
@Data
public class CouponApprovalListForm extends BaseForm {

    private String userPhone;

    @Pattern(regexp = RegexpUtils.DATE_BARS_REGEXP, message = "开始日期格式不正确")
    private String cumApprovalTimeStart;

    @Pattern(regexp = RegexpUtils.DATE_BARS_REGEXP, message = "结束日期格式不正确")
    private String cumApprovalTimeEnd;
}
