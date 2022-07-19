package com.nut.locationservice.app.pojo;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.app.locationService.pojo
 * @Author: yzl
 * @CreateTime: 2021-06-17 10:31
 * @Version: 1.0
 */
@Data
@ToString
@Accessors(chain = true)
public class FaultOriginalPojo {
    //通信号
    private Long terminalId;
    //故障列表
    private List<FaultInfoPojo> faultList;
}
