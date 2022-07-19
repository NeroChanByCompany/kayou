package com.nut.driver.app.dto;

import lombok.Data;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.dto
 * @Author: yzl
 * @CreateTime: 2021-08-05 13:28
 * @Version: 1.0
 */
@Data
public class OilExceptionUpOutDto {
    private Double beforeOil;
    private Double afterOil;
    private Double changeOil;
    private String startTime;
    private String endTime;
    private String startPlacelongitude;
    private String startPlacelatitude;
    private String endPlacelongitude;
    private String endPlacelatitude;
    private String startLocation;
    private String endLocation;
}
