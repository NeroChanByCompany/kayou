package com.nut.servicestation.app.service;

import com.nut.common.result.HttpCommandResultWithData;
import com.nut.servicestation.app.form.WoInfoForm;

/*
 *  @author wuhaotian 2021/7/8
 */
public interface WoInfoService {

    /**
     * core process
     */
    HttpCommandResultWithData core(WoInfoForm command, boolean isFromTboss);
}
