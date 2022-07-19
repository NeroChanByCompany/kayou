package com.nut.driver.app.dto;

import lombok.Data;

/**
 * @Description: 故障接口返回参数
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.dto
 * @Author: yzl
 * @CreateTime: 2021-06-29 16:15
 * @Version: 1.0
 */
@Data
public class CurrentQueryFaultResponseDto {
    private Long terminalId;

    private String fmi;

    private String spn;

    private String addr;

    private Integer faultbegintime;

    private Integer faultendtime;


    private Integer faultbeginlat;


    private Integer faultbeginlon;

    private Integer faultendlat;

    private Integer faultendlon;

    private Integer faultduration;

    private String faultDate;
}
