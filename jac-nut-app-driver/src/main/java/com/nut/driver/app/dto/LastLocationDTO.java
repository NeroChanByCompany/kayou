package com.nut.driver.app.dto;

import lombok.Data;

/**
 * @author liuBing
 * @Classname LastLocationDTO
 * @Description TODO
 * @Date 2021/6/24 15:41
 */
@Data
public class LastLocationDTO {


    /**
     * 终端通信号
     */
    private Long terminalId;
    /**
     * gps时间
     */
    private Long gpsTime;
    /**
     * 经度坐标
     */
    private Double lon;

    /**
     * 纬度坐标
     */
    private Double lat;

    private Double mileage;

    private Double oil;
}
