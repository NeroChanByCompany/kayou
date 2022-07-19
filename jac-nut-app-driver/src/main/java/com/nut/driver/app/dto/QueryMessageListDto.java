package com.nut.driver.app.dto;

import lombok.Data;

/**
 * @Description: 查询消息列表
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.dto
 * @Author: yzl
 * @CreateTime: 2021-06-27 11:29
 * @Version: 1.0
 */
@Data
public class QueryMessageListDto {
    /**
     * 消息类型名称
     */
    private String typeName;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 推送时间（毫秒）
     */
    private Long sendTime;

    /**
     * 未读消息数量
     */
    private Integer count;

    /**
     * 消息类型
     */
    private Integer messageType;
}
