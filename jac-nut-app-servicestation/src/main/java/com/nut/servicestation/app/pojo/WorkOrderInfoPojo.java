package com.nut.servicestation.app.pojo;

import lombok.Data;

import java.util.Date;

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
