package com.nut.driver.app.dto;

import lombok.Data;

/**
 * @Description: 查询报警通知列表
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.dto
 * @Author: yzl
 * @CreateTime: 2021-06-30 10:28
 * @Version: 1.0
 */
@Data
public class QueryAlarmNoticeListDto {
    /**
     * 车辆ID
     */
    private String carId;

    /**
     * 车牌号
     */
    private String carNumber;

    /**
     * 底盘号
     */
    private String chassisNum;

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

}
