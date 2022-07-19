package com.nut.driver.app.pojo;

import lombok.Data;

/**
 * @Description: 查询报警通知总数和已读数量
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.pojo
 * @Author: yzl
 * @CreateTime: 2021-06-30 10:46
 * @Version: 1.0
 */
@Data
public class QueryAlarmNoticeCountPojo {
    /**
     * 车辆ID
     */
    private String carId;

    /**
     * 消息总数量
     */
    private Integer totalCount;

    /**
     * 已读消息数量
     */
    private Integer readCount;

}
