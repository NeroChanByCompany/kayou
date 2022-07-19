package com.nut.servicestation.app.service;

/*
 *  @author wuhaotian 2021/7/8
 */
public interface AsyTriggerFaultyPartService {

    void triggerFaultyPart(String type, String woCode, String operateId);
}
