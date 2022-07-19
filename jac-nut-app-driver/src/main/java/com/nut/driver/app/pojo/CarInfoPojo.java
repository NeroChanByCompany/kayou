package com.nut.driver.app.pojo;

import lombok.Data;

/**
 * @Description: 车辆基础信息
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.driver.pojo
 * @Author: yzl
 * @CreateTime: 2021-06-21 14:36
 * @Version: 1.0
 */
@Data
public class CarInfoPojo {
    private String carId; // 车辆id
    private String carNumber; // 车牌号
    private String teamId; // 所在车队ID
    private String teamName; // 所在车队名称
    private String carVin;
    private String autoTerminal;
    private Integer extInfoOk;
}
