package com.nut.driver.app.dto;

import lombok.Data;

/**
 * @Description: 我的预约列表
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.dto
 * @Author: yzl
 * @CreateTime: 2021-06-27 15:03
 * @Version: 1.0
 */
@Data
public class MyOrdersDto {
    /**
     * 工单号
     */
    private String woCode;

    /**
     * 工单类型
     */
    private Integer woType;

    /**
     * 服务站ID
     */
    private String stationId;

    /**
     * 服务站名称
     */
    private String stationName;

    /**
     * 提交时间
     */
    private String orderTime;

    /**
     * 项目
     */
    private String item;

    /**
     * 车牌号
     */
    private String carNumber;

    /**
     * vin码
     */
    private String vin;
    /**
     * 工单状态名称
     */
    private String status;
    /**
     * 工单状态
     */
    private Integer woStatus;
}
