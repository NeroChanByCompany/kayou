package com.nut.driver.app.service;

import com.nut.common.result.HttpCommandResultWithData;
import com.nut.driver.app.form.GetCarLocationForm;

/**
 * @author liuBing
 * @Classname QueryLastLocationService
 * @Description TODO
 * @Date 2021/6/24 15:40
 */
public interface QueryLocationService {
    String getMellage(String carId);

    /**
     * @param com
     * @return
     */
    HttpCommandResultWithData carLocation(GetCarLocationForm com);
}
