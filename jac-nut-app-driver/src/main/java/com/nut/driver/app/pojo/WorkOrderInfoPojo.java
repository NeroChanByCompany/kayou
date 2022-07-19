package com.nut.driver.app.pojo;

import lombok.Data;

import java.util.Date;

/**
 * @Description: 工单信息
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.driver.pojo
 * @Author: yzl
 * @CreateTime: 2021-06-21 20:05
 * @Version: 1.0
 */
@Data
public class WorkOrderInfoPojo {
    /** 服务站省份编码 */
    private String areaCode;

    /** 创建时间 */
    private Date createTime;

    /** 工单号 */
    private String woCode;

    /** 车辆VIN */
    private String carVin;
}
