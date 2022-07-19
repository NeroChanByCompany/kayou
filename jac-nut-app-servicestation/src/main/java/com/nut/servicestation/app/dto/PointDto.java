package com.nut.servicestation.app.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 查询轨迹点DTO
 */
@Data
public class PointDto implements Serializable {
    private Integer index;
    private Double latitude;
    private Double longitude;
    private String time;
    private Double radius;

}
