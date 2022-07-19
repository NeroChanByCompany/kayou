package com.nut.driver.app.pojo;

import lombok.Data;

/**
 * @Description: 查询报警通知列表
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.pojo
 * @Author: yzl
 * @CreateTime: 2021-06-30 10:32
 * @Version: 1.0
 */
@Data
public class QueryAlarmNoticeListPojo {
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
}
