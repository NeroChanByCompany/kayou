package com.nut.driver.app.dto;

import lombok.Data;

/**
 * @Description: 网红字段标识
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.dto
 * @Author: yzl
 * @CreateTime: 2021-10-19 13:12
 * @Version: 1.0
 */
@Data
public class StarSignDto {
    // 我的账单：0-不显示，1-显示
    Integer bill;

    // 推广分享：0-不显示，1-显示
    Integer promotion;
}
