package com.nut.servicestation.app.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.servicestation.app.dto
 * @Author: yzl
 * @CreateTime: 2021-07-27 15:41
 * @Version: 1.0
 */
@Data
public class AreaListDto implements Serializable {

    private String id; //省份或城市ID
    private String name;//省份或城市名称

}
