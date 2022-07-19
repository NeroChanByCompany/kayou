package com.nut.servicestation.app.dto;

import lombok.Data;

import java.util.List;

@Data
public class StationStayWarnCarsDto {

    /**
     *
     */
    private Integer total;
    /**
     *
     */
    private String stationName;
    /**
     *
     */
    private String stationLon;
    /**
     *
     */
    private String stationLat;
    /**
     *
     */
    private String stationRadius;
    /**
     *
     */
    private List<StationStayWarnCarDto> list;


}
