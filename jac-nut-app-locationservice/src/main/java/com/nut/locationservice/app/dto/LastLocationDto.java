package com.nut.locationservice.app.dto;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.app.locationService.dto
 * @Author: yzl
 * @CreateTime: 2021-06-17 10:58
 * @Version: 1.0
 */
@Data
@ToString
@Accessors(chain = true)
public class LastLocationDto {

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
