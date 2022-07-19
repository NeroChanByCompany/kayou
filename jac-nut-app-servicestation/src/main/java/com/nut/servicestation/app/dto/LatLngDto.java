package com.nut.servicestation.app.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 上传轨迹点DTO
 */
@Data
public class LatLngDto implements Serializable {
    private String index;
    private Double latitude;
    private Double longitude;
    private String time;
    private Double radius;

}
