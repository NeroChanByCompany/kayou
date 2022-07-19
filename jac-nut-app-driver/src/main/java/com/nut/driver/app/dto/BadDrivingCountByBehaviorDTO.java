package com.nut.driver.app.dto;

import lombok.Data;

import java.util.List;

/**
 * @detail 不良驾驶按行为Dto
 */
@Data
public class BadDrivingCountByBehaviorDTO {
    private int totalNumber;
    private List<BadDrivingCountByBehaviorListDTO> list;

}
