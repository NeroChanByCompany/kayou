package com.nut.servicestation.app.service;

import com.nut.common.result.HttpCommandResultWithData;
import com.nut.servicestation.app.form.CouponApprovalListForm;
import com.nut.servicestation.app.form.CouponDetailForm;
import com.nut.servicestation.app.form.CouponExchangeForm;

/*
 *  @author wuhaotian 2021/7/7
 */
public interface CouponService {
    /**
     * 扫码获取优惠券信息
     * @param command
     * @return
     */
    HttpCommandResultWithData detail(CouponDetailForm command);
    /**
     * 核销记录
     * @param command
     * @return
     */
    HttpCommandResultWithData approvalList(CouponApprovalListForm command);
    /**
     * 兑换
     * @param command
     * @return
     */
    HttpCommandResultWithData exchange(CouponExchangeForm command);
}
