package com.nut.driver.app.dto;

import lombok.Data;

import java.util.List;

/**
 * @Description: 户车辆详情接口返回DTO
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.dto
 * @Author: yzl
 * @CreateTime: 2021-06-29 17:21
 * @Version: 1.0
 */
@Data
public class UserCarDetailDto {
    private String speed;
    private String rotation;
    private String fuelPercent;
    private String coolingWaterTem;
    private String oilPressure;
    private String atmosphericTem;
    private String lon;
    private String lat;
    private String location;
    private Integer role;
    private String carNumber;
    private String chassisNum;
    private List<TeamInfo> teamList;
    private Integer onlineStatus;
    private String mileage;
    private String totalMileage;
    /**剩余油量百分比**/
    private Double oilValuePercent;
    private Double avgOil;
    /** 总里程 */
    private Double mileageEngine;
    /** 尿素箱液位 */
    private Double ureaLevel;

    private String hasNetwork;

    /**
     * 交车单ID
     */
    private Long deliveryId;

    /**
     * 车辆品牌
     */
    private String carSeriesName;

    @Data
    public static class TeamInfo {
        private Long teamId;
        private String name;
        private String role;
    }
}
