package com.nut.servicestation.app.dto;

import lombok.Data;

@Data
public class QueryDistanceAnomalyDto {

    /** 工单号 */
    private String woCode;
    /** 底盘号 */
    private String chassisNum;
    /** 车牌号 */
    private String carNumber;
    /** 整车平台 */
    private String seriseName;
    /** 工单状态 */
    private String woStatus;
    /** 推荐导航里程 */
    private String estimateMileage;
    /** 实际导航里程 */
    private String mileage;

    /** 推荐导航里程 */
    private Integer estimateMileageInt;
    /** 实际导航里程 */
    private Integer mileageInt;
    /** 出发时间 */
    private String timeDepart;
    /** 接车时间 */
    private String timeReceive;

    /** 服务站名称 */
    private String stationName;
    /** 服务站代码 */
    private String stationCode;
    /** 服务电话 */
    private String stationTel;
    /** 外出人员手机 */
    private String rescueStaffPhone;
}