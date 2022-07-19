package com.nut.locationservice.app.form;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @Description: 故障汇总报表
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.app.locationService.form
 * @Author: yzl
 * @CreateTime: 2021-06-17 09:22
 * @Version: 1.0
 */
@Data
@Accessors(chain = true)
@ToString
public class QueryFaultSummaryForm {
    private String spn;// SPN
    private String fmi;// FMI
    private Long beginTime;// 开始时间
    private Long endTime;// 结束时间
    private String dateStr;// 时间
    private String systemType; // 系统类别
}
