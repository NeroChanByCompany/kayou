package com.nut.driver.app.domain;

import lombok.Data;

/**
 * @Description: 温度湿度集合
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.domain
 * @Author: yzl
 * @CreateTime: 2021-09-01 17:20
 * @Version: 1.0
 */
@Data
public class TemHum {

    private String temperature;

    private String humidity;

}
