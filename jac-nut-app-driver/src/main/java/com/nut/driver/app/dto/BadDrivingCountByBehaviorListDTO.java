package com.nut.driver.app.dto;

import lombok.Data;

import java.util.List;

@Data
public class BadDrivingCountByBehaviorListDTO {
    private String badName;
    private int badNumber;
    private int badProportion;

    private List<BadDrivingCountInfoByBehaviorDTO> topTen;

}
