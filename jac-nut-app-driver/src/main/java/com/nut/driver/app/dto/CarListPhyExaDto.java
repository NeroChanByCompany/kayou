package com.nut.driver.app.dto;

import lombok.Data;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.dto
 * @Author: yzl
 * @CreateTime: 2021-06-29 15:42
 * @Version: 1.0
 */
@Data
public class CarListPhyExaDto {
    private String carId;

    private String carNum;

    private String lastPhyExaTime;

    private Integer faultNumber;

    /**
     * 是否有车联网功能，1：有，0：无
     */
    private String hasNetwork;
}
