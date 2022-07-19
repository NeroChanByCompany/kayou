package com.nut.servicestation.app.service;

import com.nut.servicestation.app.pojo.AreaStayInfoPojo;

/**
 * @author liuBing
 * @Classname InOutAreaToCrmConsumerService
 * @Description TODO
 * @Date 2021/7/7 19:16
 */
public interface InOutAreaToCrmConsumerService {
    void inOutAreaToCrmConsumer(AreaStayInfoPojo pojo, String terminalId);
}
