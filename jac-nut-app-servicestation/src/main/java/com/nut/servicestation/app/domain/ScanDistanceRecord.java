package com.nut.servicestation.app.domain;

import lombok.Data;

import java.util.Date;

@Data
public class ScanDistanceRecord {
    private Long id;

    private String woCode;

    private String appLon;

    private String appLat;

    private String carLon;

    private String carLat;

    private String stationLon;

    private String stationLat;

    private Double appStationDistance;

    private Double appCarDistance;

    private Date scanTime;

}