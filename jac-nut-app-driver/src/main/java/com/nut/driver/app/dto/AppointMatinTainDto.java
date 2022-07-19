package com.nut.driver.app.dto;

import lombok.Data;

import java.math.BigInteger;

/**
 * @Description: 预约 维修/保养项目
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.driver.dto
 * @Author: yzl
 * @CreateTime: 2021-06-23 13:52
 * @Version: 1.0
 */
@Data
public class AppointMatinTainDto {

    private BigInteger itemId;
    private String itemName;

}
