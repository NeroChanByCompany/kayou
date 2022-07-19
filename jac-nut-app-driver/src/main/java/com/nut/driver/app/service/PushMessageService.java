package com.nut.driver.app.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

/**
 * @Description: 推送服务类
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.driver.service
 * @Author: yzl
 * @CreateTime: 2021-06-22 13:09
 * @Version: 1.0
 */
@Service
@EnableAsync
public interface PushMessageService {

    /**
     * 推送消息给服务站
     * @param stype
     * @param wildcard
     * @param messageExtra
     * @param senderId
     * @param receiverId
     * @param pushShowType
     */
    @Async
    public void pushToStation(String stype, String wildcard, String messageExtra, String senderId, String receiverId, String pushShowType);
    /**
     * 推送指定消息给服务站
     * @param stype
     * @param wildcard
     * @param messageExtra
     * @param senderId
     * @param receiverId
     * @param pushShowType
     */
    @Async
    public void pushMessageToStation(String stype, String wildcard, String messageExtra, String senderId, String receiverId, String pushShowType);

}
