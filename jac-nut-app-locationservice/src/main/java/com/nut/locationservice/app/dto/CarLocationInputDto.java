package com.nut.locationservice.app.dto;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigInteger;

/**
 * @Description: 获取车辆位置的入参dto
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.app.locationService.dto
 * @Author: yzl
 * @CreateTime: 2021-06-16 19:06
 * @Version: 1.0
 */
@Data
@ToString
@Accessors(chain = true)
public class CarLocationInputDto {

    private String vin;//车辆vin码

    private BigInteger terminalId;//通信号
}
