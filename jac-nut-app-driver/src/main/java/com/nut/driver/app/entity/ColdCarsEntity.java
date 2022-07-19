package com.nut.driver.app.entity;

import lombok.Data;

/**
 * @Description: 冷链车部分信息实体
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.entity
 * @Author: yzl
 * @CreateTime: 2021-09-23 13:39
 * @Version: 1.0
 */
@Data
public class ColdCarsEntity {

    private Long carId;

    private String carNumber;

    private String vin;
}
