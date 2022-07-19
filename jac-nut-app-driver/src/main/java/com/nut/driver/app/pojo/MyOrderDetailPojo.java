package com.nut.driver.app.pojo;

import lombok.Data;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.app.driver.pojo
 * @Author: yzl
 * @CreateTime: 2021-06-23 20:11
 * @Version: 1.0
 */
@Data
public class MyOrderDetailPojo {
    /**
     * 工单号
     */
    private String woCode;
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
     * 预约人ID
     */
    private Long appoUserId;
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
     * 申请拒单理由
     */
    private String refuseReason;
    /**
     * 申请关闭理由
     */
    private String closeReason;
    /**
     * 取消预约原因
     */
    private String cancelReason;
    /**
     * 故障车辆位置
     */
    private String carLocation;
    /**
     * 故障车辆经度
     */
    private String carLon;
    /**
     * 故障车辆纬度
     */
    private String carLat;
    /**
     * 送修人电话
     */
    private String sendToRepairPhone;
    /**
     * 驳回备注
     */
    private String rejectRemarks;

}
