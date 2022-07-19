package com.nut.driver.app.domain;

import lombok.Data;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.domain
 * @Author: yzl
 * @CreateTime: 2021-06-29 15:48
 * @Version: 1.0
 */
@Data
public class PhyExaRecord {
    private Long id;

    private String carId;

    private Integer faultNumber;

    private String faultIds;

    private Long phyExaTime;

    private String autoTerminal;
}
