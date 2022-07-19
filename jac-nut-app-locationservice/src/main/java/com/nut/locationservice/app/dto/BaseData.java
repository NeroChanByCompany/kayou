package com.nut.locationservice.app.dto;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigInteger;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.app.locationService.dto
 * @Author: yzl
 * @CreateTime: 2021-06-16 19:37
 * @Version: 1.0
 */
@Data
@ToString
@Accessors(chain = true)
public class BaseData {

    private BigInteger id;

    private String code;

    private String value;
}
