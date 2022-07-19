package com.nut.driver.app.dto;

import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.dto
 * @Author: yzl
 * @CreateTime: 2021-06-29 16:02
 * @Version: 1.0
 */
@Data
public class CarPhyExaAgainExaDto {
    private Integer faultNumber;

    private List<CarPhyExaFaultDetailDto> list;
}
