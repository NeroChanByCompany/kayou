package com.nut.truckingteam.app.domain;

import lombok.Data;

import java.util.Date;

@Data
public class UserMessageRecord {
    private Long id;

    private String messageId;

    private String title;

    private String content;

    private Integer pushShowType;

    private Integer messageCode;

    private Integer userVisible;

    private String receiverId;

    private Integer receiveAppType;

    private String senderId;

    private Long sendTime;

    private Integer readFlag;

    private String messageExtra;

    private Integer type;

    private Integer stype;

    private Date createTime;

    private Date updateTime;

    /**
     * 接收状态
     */
    private Integer receiveState;
    /**
     * 消息分类
     */
    private Integer messageType;
    /**
     * 消息分类名称
     */
    private String messageTypeName;
    /**
     * 登录设备标识  0 安卓 1 ios
     */
    private String deviceType;

    /**
     * @Author liuBing
     * @Description //TODO 构造器部分
     * @Date 14:23 2021/5/27
     * @Param []
     **/
    public static UserMessageRecord anUserMessageRecord() {
        return new UserMessageRecord();
    }

    public UserMessageRecord withId(Long id) {
        this.id = id;
        return this;
    }

    public UserMessageRecord withMessageId(String messageId) {
        this.messageId = messageId;
        return this;
    }

    public UserMessageRecord withTitle(String title) {
        this.title = title;
        return this;
    }

    public UserMessageRecord withContent(String content) {
        this.content = content;
        return this;
    }
    public UserMessageRecord withDeviceType(String deviceType) {
        this.deviceType = deviceType;
        return this;
    }
    public UserMessageRecord withPushShowType(Integer pushShowType) {
        this.pushShowType = pushShowType;
        return this;
    }

    public UserMessageRecord withMessageCode(Integer messageCode) {
        this.messageCode = messageCode;
        return this;
    }

    public UserMessageRecord withUserVisible(Integer userVisible) {
        this.userVisible = userVisible;
        return this;
    }

    public UserMessageRecord withReceiverId(String receiverId) {
        this.receiverId = receiverId;
        return this;
    }

    public UserMessageRecord withReceiveAppType(Integer receiveAppType) {
        this.receiveAppType = receiveAppType;
        return this;
    }

    public UserMessageRecord withSenderId(String senderId) {
        this.senderId = senderId;
        return this;
    }

    public UserMessageRecord withSendTime(Long sendTime) {
        this.sendTime = sendTime;
        return this;
    }

    public UserMessageRecord withReadFlag(Integer readFlag) {
        this.readFlag = readFlag;
        return this;
    }

    public UserMessageRecord withMessageExtra(String messageExtra) {
        this.messageExtra = messageExtra;
        return this;
    }

    public UserMessageRecord withType(Integer type) {
        this.type = type;
        return this;
    }

    public UserMessageRecord withStype(Integer stype) {
        this.stype = stype;
        return this;
    }

    public UserMessageRecord withReceiveState(Integer receiveState) {
        this.receiveState = receiveState;
        return this;
    }

    public UserMessageRecord withMessageType(Integer messageType) {
        this.messageType = messageType;
        return this;
    }

    public UserMessageRecord withMessageTypeName(String messageTypeName) {
        this.messageTypeName = messageTypeName;
        return this;
    }

    public UserMessageRecord withCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    public UserMessageRecord withUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public UserMessageRecord build() {
        UserMessageRecord userMessageRecord = new UserMessageRecord();
        userMessageRecord.setId(id);
        userMessageRecord.setMessageId(messageId);
        userMessageRecord.setTitle(title);
        userMessageRecord.setContent(content);
        userMessageRecord.setPushShowType(pushShowType);
        userMessageRecord.setMessageCode(messageCode);
        userMessageRecord.setUserVisible(userVisible);
        userMessageRecord.setReceiverId(receiverId);
        userMessageRecord.setReceiveAppType(receiveAppType);
        userMessageRecord.setSenderId(senderId);
        userMessageRecord.setSendTime(sendTime);
        userMessageRecord.setReadFlag(readFlag);
        userMessageRecord.setMessageExtra(messageExtra);
        userMessageRecord.setType(type);
        userMessageRecord.setStype(stype);
        userMessageRecord.setReceiveState(receiveState);
        userMessageRecord.setMessageType(messageType);
        userMessageRecord.setMessageTypeName(messageTypeName);
        userMessageRecord.setCreateTime(createTime);
        userMessageRecord.setUpdateTime(updateTime);
        userMessageRecord.setDeviceType(deviceType);
        return userMessageRecord;
    }

}
