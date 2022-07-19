package com.nut.servicestation.app.dto;

import lombok.Data;

import java.util.List;

/**
 * 周边车辆
 */
@Data
public class NearbyCarsListDto {

    /**
     *服务站名称
     */
    private String serviceStationName;
    /**
     *服务站经度
     */
    private String serviceLon;
    /**
     *服务站纬度
     */
    private String serviceLat;
    /**
     *车辆对象集合
     */
    private List<NearbyCarsDto> list;

}
