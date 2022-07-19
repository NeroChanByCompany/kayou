package com.nut.driver.app.dto;

import lombok.Data;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.dto
 * @Author: yzl
 * @CreateTime: 2021-06-30 09:46
 * @Version: 1.0
 */
@Data
public class TripCarListDto {
    /**
     *车辆ID
     */
    private String carId;
    /**
     *车牌号
     */
    private String carNumber;
    /**
     *行程数
     */
    private String tripCount;
    /**
     *行程总里程
     */
    private String tripTotalMileage;
    /**
     *行程总里程
     */
    private Double tripTotalMileageBySort;
    /**
     *行程总油耗
     */
    private String tripTotalOil;
    /**
     *通信号
     */
    private String autoTerminal;
    /**
     * 底盘号
     */
    private String vin;
    /**
     * 平均评分
     */
    private Integer avgScore;
}
