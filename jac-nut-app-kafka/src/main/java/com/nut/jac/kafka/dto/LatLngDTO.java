package com.nut.jac.kafka.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author liuBing
 * @Classname LatLngDTO
 * @Description TODO
 * @Date 2021/8/31 13:29
 */
@Data
@Accessors(chain = true)
public class LatLngDTO {
    private String index;
    private Double latitude;
    private Double longitude;
    private String time;
    private Double radius;
}
