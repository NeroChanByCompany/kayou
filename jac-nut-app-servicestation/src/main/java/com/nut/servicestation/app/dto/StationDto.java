package com.nut.servicestation.app.dto;

import lombok.Data;

/**
 * @Description: 服务站返回
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.servicestation.app.dto
 * @Author: yzl
 * @CreateTime: 2021-07-27 20:51
 * @Version: 1.0
 */
@Data
public class StationDto {
    private String id;
    private String name;
    private String address;
}
