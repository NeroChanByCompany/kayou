package com.nut.driver.app.dto;

import lombok.Data;

/**
 * @Description: 辆聚合接口返回结果DTO
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.dto
 * @Author: yzl
 * @CreateTime: 2021-06-29 16:53
 * @Version: 1.0
 */
@Data
public class CarPolymerizeDto {
    // 纬度坐标
    private Double latitude;
    // 经度坐标
    private Double longitude;
    // 聚合数量
    private Integer count;
    //聚合值为1时返回车牌号
    private String carNo;
    //聚合值为1时返回车头方向
    private Integer direction;
    //车辆状态。0：离线；1：停止；2：行驶中
    private Integer travelStatus;
    private String carId;

    //底盘号（vin后8位）
    private String vin;
}
