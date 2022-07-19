package com.nut.driver.app.dto;

import lombok.Data;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.dto
 * @Author: yzl
 * @CreateTime: 2021-06-29 16:02
 * @Version: 1.0
 */
@Data
public class CarPhyExaFaultDetailDto {
    private Long id;

    private String faultCode;

    private String spn;

    private String fmi;

    private String faultDesc;
}
