package com.nut.app.driver.dto;

import lombok.Data;

/**
 * @Description: 我的预约详情
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.app.driver.dto
 * @Author: yzl
 * @CreateTime: 2021-06-23 20:08
 * @Version: 1.0
 */
@Data
public class MyOrderDetailDto {
    /**
     * 工单号
     */
    private String woCode;

    /**
     * 车辆位置
     */
    private String carLocation;

    /**
     * 车辆经度
     */
    private String carLon;

    /**
     * 车辆纬度
     */
    private String carLat;

    /**
     * 工单状态
     */
    private Integer woStatus;

    /**
     * 服务站ID
     */
    private String stationId;

    /**
     * 服务站名称
     */
    private String stationName;

    /**
     * 当前用户是否是预约人
     */
    private Integer isAppoUser;

    /**
     * 预约人姓名
     */
    private String name;

    /**
     * 预约人电话
     */
    private String phone;

    /**
     * 预约时间
     */
    private String orderTime;

    /**
     * 预约创建时间
     */
    private String createTime;

    /**
     * 工单类型
     */
    private String woType;

    /**
     * 保养项目
     */
    private String maintenanceItem;

    /**
     * 维修项目
     */
    private String repairItem;

    /**
     * 用户反馈
     */
    private String feedback;

    /**
     * 用户上传文件
     */
    private String files;

    /**
     * 取消原因
     */
    private String reason;

    /**
     * 评价状态
     */
    private Integer rateStatus;

    /**
     * vin码
     */
    private String vin;

    /**
     * 车牌号
     */
    private String carNumber;

    /**
     * 车队id
     */
    private String teamId;

    /**
     * 车队名称
     */
    private String teamName;

    /**
     * 故障描述
     */
    private String description;

    /**
     * 车型
     */
    private String carType;
    /**
     * 当前用户是否是送修人
     */
    private Integer isReviewer;
    /**
     * 中文工单状态
     */
    private String woStatusValue;
    /**
     * 送修审核电话
     */
    private String auditPhone;
    /**
     * 当前用户是否是车管账号（0：非车管 - 1：车管账号）
     */
    private Integer isAuditer;
    /**
     * 驳回备注
     */
    private String rejectRemarks;
}
