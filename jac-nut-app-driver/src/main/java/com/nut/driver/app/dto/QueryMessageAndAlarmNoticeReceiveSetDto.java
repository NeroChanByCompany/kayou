package com.nut.driver.app.dto;

import lombok.Data;

/**
 * @Description: 查询消息和报警通知接收设置
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.dto
 * @Author: yzl
 * @CreateTime: 2021-06-30 11:11
 * @Version: 1.0
 */
@Data
public class QueryMessageAndAlarmNoticeReceiveSetDto {
    /**
     * 消息类型名称
     */
    private String typeName;

    /**
     * 消息类型
     */
    private String messageType;

    /**
     * 消息接收状态（0：不接收，1：接收。）
     */
    private Integer receiveState;

    public QueryMessageAndAlarmNoticeReceiveSetDto () {

    }

    public QueryMessageAndAlarmNoticeReceiveSetDto(String typeName, String messageType, Integer receiveState) {
        this.typeName = typeName;
        this.messageType = messageType;
        this.receiveState = receiveState;
    }
}
