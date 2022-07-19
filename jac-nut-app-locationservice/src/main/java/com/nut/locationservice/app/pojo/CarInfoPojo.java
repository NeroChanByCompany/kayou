package com.nut.locationservice.app.pojo;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @Description: 车辆信息
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.app.locationService.pojo
 * @Author: yzl
 * @CreateTime: 2021-06-17 09:27
 * @Version: 1.0
 */
@Data
@ToString
@Accessors(chain = true)
public class CarInfoPojo {
    /**
     * 车辆id
     */
    private String carId;

    /**
     * 车辆vin码
     */
    private String vin;

    /**
     * 通信号
     */
    private Long terminalId;
}
