package com.nut.servicestation.app.service;

import org.springframework.scheduling.annotation.Async;

/*
 *  @author wuhaotian 2021/7/2
 */
public interface AsyPushMessageService {

    /**
     * 推送给司机和车队 跳转页面为预约详情页面
     *
     * @param stype         消息分类
     * @param wildcard      通配符字段
     * @param messageExtra  附加字段
     * @param senderId      发送人id
     * @param receiverPhone 接收者手机号（与chassisNum二选一）
     * @param chassisNum    底盘号（与receiverPhone二选一）
     */
    void pushToDriverAndOwner(String stype, String wildcard, String messageExtra, String senderId, String receiverPhone, String chassisNum);
}
