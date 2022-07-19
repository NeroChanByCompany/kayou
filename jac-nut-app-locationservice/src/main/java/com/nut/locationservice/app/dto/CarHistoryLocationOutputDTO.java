package com.nut.locationservice.app.dto;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author liuBing
 * @Classname CarHistoryLocationOutputDTO
 * @Description TODO 历史数据返回
 * @Date 2021/6/16 14:53
 */
@Data
@ToString
@Accessors(chain = true)
public class CarHistoryLocationOutputDTO implements Serializable {
    private static final long serialVersionUID = 7667243422938063000L;
    /**
     * 车辆vin码
     */
    private String vin;
    /**
     * 终端id
     */
    private String terminalId;
    /**
     * 经度坐标
     */
    private Double lon;
    /**
     * 纬度坐标
     */
    private Double lat;
    /**
     * 里程
     */
    private Double mileage;
    /**
     * 数据上传时间
     */
    private Long upTime;
}
