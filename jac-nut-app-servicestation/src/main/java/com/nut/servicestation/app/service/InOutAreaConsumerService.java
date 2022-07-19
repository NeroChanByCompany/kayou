package com.nut.servicestation.app.service;

import com.nut.servicestation.app.pojo.AreaStayInfoPojo;

/**
 * @author liuBing
 * @Classname InOutAreaConsumerService
 * @Description TODO
 * @Date 2021/7/7 17:49
 */
public interface InOutAreaConsumerService {

    void inOutAreaConsumer(AreaStayInfoPojo pojo, String terminalId);


}
