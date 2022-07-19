package com.nut.driver.app.dto;

import lombok.Data;

import java.util.List;

/**
 * 用户车队详情接口返回DTO
 */
@Data
public class UserFleetDetailDto {
    private String name;
    private String avatar;
    private String creatorName;
    private String creatorPhone;
    private Integer role;
    private Long adminTotal;
    private List<UserInfo> adminList;
    private Long driverTotal;
    private List<UserInfo> driverList;
    private Long carTotal;
    private List<CarInfo> carList;

@Data
    public static class UserInfo {
        private String nickname;
        private String phone;
        private Long userId;

    }
@Data
    public static class CarInfo {
        private String carId;
        private String carNumber;
        private String chassisNum;
        private String lon;
        private String lat;
        private String location;

    }
}
