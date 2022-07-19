package com.nut.driver.app.dto;

import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.dto
 * @Author: yzl
 * @CreateTime: 2021-06-29 15:41
 * @Version: 1.0
 */
@Data
public class CarPhyExaDto {
    private Long total;

    private Long page_total;

    private Integer carNumber;

    private Integer faultCarNumber;

    private Integer faultNumber;

    private List<CarListPhyExaDto> list;
}
