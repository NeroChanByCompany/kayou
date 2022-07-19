package com.nut.driver.app.service;

import com.alibaba.fastjson.JSONObject;
import com.nut.driver.app.domain.IntegralAlterRecord;
import com.nut.driver.app.domain.IntegralConsumeConfirmInfo;
import com.nut.driver.app.domain.IntegralConsumeInfo;
import com.nut.driver.app.form.IntegralOperationForm;
import com.nut.driver.common.sdk.VirtualCardConsumeParams;

public interface DuibaService {

    JSONObject integralConsume(IntegralConsumeInfo integralConsumeInfo) throws Exception;

    void add(IntegralOperationForm integralOperationForm);

    String consumeConfirm(IntegralConsumeConfirmInfo integralConsumeConfirmInfo) throws Exception;

    void virtualConsume(VirtualCardConsumeParams virtualCardConsumeParams) throws Exception;

    void insertIntegralAlterRecord(IntegralAlterRecord integralAlterRecord);
}
