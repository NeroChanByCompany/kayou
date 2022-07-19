package com.nut.driver.app.pojo;

import lombok.Data;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.driver.pojo
 * @Author: yzl
 * @CreateTime: 2021-06-21 14:30
 * @Version: 1.0
 */
@Data
public class CarRolePojo {
    private String carId;
    private Long teamId;
    private Integer role;
    private Long userId;
}
