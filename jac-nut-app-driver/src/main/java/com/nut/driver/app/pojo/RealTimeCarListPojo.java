package com.nut.driver.app.pojo;

import lombok.Data;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.pojo
 * @Author: yzl
 * @CreateTime: 2021-06-29 16:57
 * @Version: 1.0
 */
@Data
public class RealTimeCarListPojo {
    private String carId;
    private String vin;
    private String carCode;
    private String mastDriver;
    private String slaveDriver;
    private Long teamId;
    private String teamName;
}
