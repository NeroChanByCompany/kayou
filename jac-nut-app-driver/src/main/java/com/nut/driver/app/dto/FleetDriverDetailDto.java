package com.nut.driver.app.dto;

import lombok.Data;

import java.util.List;

/**
 * 车队司机详情接口返回DTO
 */
@Data
public class FleetDriverDetailDto {
    private String nickname;
    private String phone;
    private List<CarInfo> carList;

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
