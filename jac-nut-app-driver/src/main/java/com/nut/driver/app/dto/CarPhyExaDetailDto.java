package com.nut.driver.app.dto;

import lombok.Data;

/**
 * @Description: 车辆体检详情
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.dto
 * @Author: yzl
 * @CreateTime: 2021-06-29 16:01
 * @Version: 1.0
 */
@Data
public class CarPhyExaDetailDto {

    private CarPhyExaAgainExaDto againExa;

    private CarPhyExaLastExaDto lastExa;
}
