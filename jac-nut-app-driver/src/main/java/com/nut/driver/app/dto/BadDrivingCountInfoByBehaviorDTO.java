package com.nut.driver.app.dto;

import lombok.Data;

@Data
public class BadDrivingCountInfoByBehaviorDTO {

    /**
     * 车牌号
     */
    private String carNumber;
    /**
     * 行为次数
     */
    private Integer behaviorNumber;
    /**
     * 行为占比
     */
    private Integer behaviorProportion;

}
