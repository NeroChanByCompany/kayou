package com.nut.servicestation.app.dto;

import lombok.Data;

/**
 * @Description: 消息列表
 */
@Data
public class MsgsDto {
    /**
     * 消息id
     */
    private String messageId;
    /**
     * 消息已读标识
     */
    private Integer readFlag;
    /**
     * 消息附加字段
     */
    private String messageExtra;
    /**
     * 消息跳转code
     */
    private Integer messageCode;
    /**
     * 标题
     */
    private String title;
    /**
     * 内容
     */
    private String content;
    /**
     * 推送时间 毫秒
     */
    private Long sendTime;
    /**
     * 工单状态
     */
    private String woStatus;
    /**
     * 工单类型
     */
    private String woType;
    /**
     * 预约方式
     */
    private String appoType;

}
