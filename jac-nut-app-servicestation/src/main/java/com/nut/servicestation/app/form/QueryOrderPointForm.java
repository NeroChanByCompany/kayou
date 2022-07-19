package com.nut.servicestation.app.form;

import lombok.Data;

/**
 * @Description: 查询轨迹点
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.servicestation.app.form
 * @Author: yzl
 * @CreateTime: 2021-08-26 08:57
 * @Version: 1.0
 */
@Data
public class QueryOrderPointForm {

    private String terminalId;

    private Long startTime;

    private Long endTime;

}
