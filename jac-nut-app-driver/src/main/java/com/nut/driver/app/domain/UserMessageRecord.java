package com.nut.driver.app.domain;

import lombok.Data;

import java.util.Date;

/**
 * @Description: 消息表
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.domain
 * @Author: yzl
 * @CreateTime: 2021-07-29 15:35
 * @Version: 1.0
 */
@Data
public class UserMessageRecord {

    /**
     * 消息id
     */
    private Long id;
    /**
     * 消息主体id
     */
    private String messageId;
    /**
     * 标题
     */
    private String title;
    /**
     * 内容
     */
    private String content;
    /**
     * 消息展示分类
     */
    private Integer pushShowType;
    /**
     * 跳转编码
     */
    private Integer messageCode;
    /**
     * 用户是否可见
     */
    private Integer userVisible;
    /**
     * 接收者id
     */
    private String receiverId;
    /**
     * 接收的客户端
     */
    private Integer receiveAppType;
    /**
     * 发送者id
     */
    private String senderId;
    /**
     * 发送时间
     */
    private Long sendTime;
    /**
     * 已读未读
     */
    private Integer readFlag;
    /**
     * 附加字段 前端使用
     */
    private String messageExtra;
    /**
     * 大分类
     */
    private Integer type;
    /**
     * 小分类
     */
    private Integer stype;
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
     * 创建时间
     */
    private Date createTime;
    /**
     * 修改时间
     */
    private Date updateTime;
    /**
     * 登录设备标识  0 安卓 1 ios
     */
    private String deviceType;


    /**
     * 论坛帖子id
     */
    private String invitationId;

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

    public UserMessageRecord withInvitationId(String invitationId) {
        this.invitationId = invitationId;
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
        userMessageRecord.setInvitationId(invitationId);
        return userMessageRecord;
    }
}
