package com.nut.locationservice.app.pojo;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @Description: 车辆故障文件
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.app.locationService.pojo
 * @Author: yzl
 * @CreateTime: 2021-06-17 10:19
 * @Version: 1.0
 */
@Data
@ToString
@Accessors(chain = true)
public class CarFaultFilePojo {
    private String id;
    private String procDate;
    private String fileName;
    private String fullPath;
    private String fileSize;
}
