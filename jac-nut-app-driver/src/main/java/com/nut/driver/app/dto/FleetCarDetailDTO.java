package com.nut.driver.app.dto;

import lombok.Data;

import java.util.List;

/**
 * 车队车辆详情接口返回DTO
 */
@Data
public class FleetCarDetailDTO {
    private String carNumber;
    private String chassisNum;
    private String lon;
    private String lat;
    private String location;
    private List<DriverInfo> driverList;

@Data
    public static class DriverInfo {
        private String nickname;
        private String phone;
        private Long userId;
        private String isMasterDriver;

    }
}
