package com.nut.servicestation.app.service;

import com.nut.common.result.HttpCommandResultWithData;
import com.nut.servicestation.app.form.AcceptVinForm;
import com.nut.servicestation.app.form.CouponExchangeForm;

/*
 *  @author wuhaotian 2021/7/7
 */
public interface FirstConfirmedService {

    HttpCommandResultWithData qualificationTest(AcceptVinForm command) throws Exception;

    HttpCommandResultWithData firstExchange(CouponExchangeForm command);
}
