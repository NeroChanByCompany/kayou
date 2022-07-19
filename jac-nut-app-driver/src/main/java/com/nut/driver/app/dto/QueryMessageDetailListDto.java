package com.nut.driver.app.dto;

import lombok.Data;

/**
 * @Description: 查询新闻详情列表
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.dto
 * @Author: yzl
 * @CreateTime: 2021-06-27 13:26
 * @Version: 1.0
 */
@Data
public class QueryMessageDetailListDto {
    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 消息ID
     */
    private String messageId;

    /**
     * 消息已读标识
     */
    private Integer readFlag;

    /**
     * 推送时间（毫秒）
     */
    private Long sendTime;

    /**
     * 消息附加字段
     */
    private String messageExtra;

    /**
     * 消息跳转code
     */
    private Integer messageCode;

    /**
     * 图片展示code
     */
    private Integer imageCode;
    /**
     * 论坛消息帖子id
     */
    private String invitationId;
}
