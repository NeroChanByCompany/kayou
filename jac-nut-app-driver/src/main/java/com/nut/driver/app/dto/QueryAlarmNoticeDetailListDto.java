package com.nut.driver.app.dto;

import lombok.Data;

/**
 * @Description: 查询报警通知详情
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.dto
 * @Author: yzl
 * @CreateTime: 2021-06-30 10:56
 * @Version: 1.0
 */
@Data
public class QueryAlarmNoticeDetailListDto {
    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

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
}
