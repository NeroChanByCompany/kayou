package com.nut.locationservice.app.pojo;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.app.locationService.pojo
 * @Author: yzl
 * @CreateTime: 2021-06-17 10:18
 * @Version: 1.0
 */
@Data
@ToString
@Accessors(chain = true)
public class QuerySystemTypePojo {
    //系统源
    private String systemSource;
    //系统类型
    private String systemType;
}
