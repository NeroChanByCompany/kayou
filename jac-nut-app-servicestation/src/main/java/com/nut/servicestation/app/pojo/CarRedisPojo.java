package com.nut.servicestation.app.pojo;

import lombok.Data;

/**
 * @author liuBing
 * @Classname CarRedisPojo
 * @Description TODO
 * @Date 2021/7/7 19:08
 */
@Data
public class CarRedisPojo {


    /**
     * 车辆ID
     */
    private String carId;
    /**
     * 车辆底盘号
     */
    private String carVin;
    /**
     * 通信号
     */
    private Long terId;
    /**
     * 区域ID（围栏ID）
     */
    private Long areaId;
    /**
     * 进入时间
     */
    private Long inTime;
    /**
     * 离开时间
     */
    private Long outTime;
    /**
     * 停留时间
     */
    private Long stayLen;

}
