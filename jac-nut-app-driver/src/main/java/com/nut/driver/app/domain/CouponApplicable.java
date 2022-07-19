package com.nut.driver.app.domain;

import lombok.Data;

import java.util.Date;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.driver.domain
 * @Author: yzl
 * @CreateTime: 2021-06-18 15:39
 * @Version: 1.0
 */
@Data
public class CouponApplicable {
    private Long id;

    private Long infoId;

    private String applicableType;

    private String applicableNumber;

    private String applicableVehicleEmission;

    private String applicableVehicleModel;

    private String applicableVehiclePlatform;

    private String applicableVehicleDrive;

    private String applicableVehicleDisplacement;

    private Date createTime;

}
