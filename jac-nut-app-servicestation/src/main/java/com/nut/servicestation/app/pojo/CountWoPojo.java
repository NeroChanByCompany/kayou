package com.nut.servicestation.app.pojo;

import lombok.Data;

@Data
public class CountWoPojo {
    /** 工单状态 */
    private Integer woStatus;
    /** 工单类型 */
    private Integer woType;
    /** 指派人ID */
    private String assignTo;
    /** 服务站ID */
    private String stationId;

}
