package com.nut.truckingteam.app.service;

import org.springframework.scheduling.annotation.Async;

public interface AsyncPushSystemMsgService {

    /**
     * 车牌号变更推送
     *
     * @param carId     车辆ID
     * @param oldCarNum 旧车牌号
     * @param carNum    新车牌号
     * @param senderId  发送者ID
     */
    @Async
    void pushModifyCarNum(String carId, String oldCarNum, String carNum, String senderId);
    /**
     * 车队名称变更推送
     *
     * @param teamId      车队ID
     * @param oldTeamName 旧车队名称
     * @param teamName    新车队名称
     * @param senderId    发送者ID
     */
    @Async
    void pushEditCarTeam(Long teamId, String oldTeamName, String teamName, String senderId);
    /**
     * 管理员或司机被移出车队 推送
     *
     * @param receiverId 接收者ID
     * @param teamId     车队ID
     * @param senderId   发送者ID
     */
    @Async
    public void pushRemovedFromFleet(String receiverId, Long teamId, String senderId);
    /**
     * 邀请司机 推送
     *
     * @param receiverId 接收者ID
     * @param teamId     车队ID
     * @param senderId   发送者ID
     */
    @Async
    void pushAddDriver(String receiverId, Long teamId, String senderId);
    /**
     * 邀请管理员 推送
     *
     * @param receiverId 接收者ID
     * @param teamId     车队ID
     * @param senderId   发送者ID
     */
    @Async
    void pushAddAdmin(String receiverId, Long teamId, String senderId);
    /**
     * 司机绑定车辆 推送
     *
     * @param receiverId 接收者ID
     * @param teamId     车队ID
     * @param carId      车辆ID
     * @param senderId   发送者ID
     */
    @Async
    void pushCarDriverBind(String receiverId, Long teamId, String carId, String senderId);
    /**
     * 司机解绑车辆 推送
     *
     * @param receiverId 接收者ID
     * @param teamId     车队ID
     * @param carId      车辆ID
     * @param senderId   发送者ID
     */
    @Async
    void pushCarDriverUnbind(String receiverId, Long teamId, String carId, String senderId);
    /**
     * 退出车队 推送
     *
     * @param receiverId 接收者ID
     * @param teamId     车队ID
     * @param role       角色
     * @param senderId   发送者ID
     */
    @Async
    void pushQuitFleet(String receiverId, Long teamId, String role, String senderId);
    /**
     * 解散车队 推送
     *
     * @param receiverId 接收者ID
     * @param teamName   车队名称
     * @param senderId   发送者ID
     */
    @Async
    void pushDeleteFleet(String receiverId, String teamName, String senderId);
    /**
     * 转让车队 推送
     *
     * @param receiverId 接收者ID
     * @param teamId     车队ID
     * @param senderId   发送者ID
     */
    @Async
    void pushHandoverFleet(String receiverId, Long teamId, String senderId);
}
