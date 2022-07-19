package com.nut.servicestation.app.pojo;

import lombok.Data;

@Data
public class RepairRecordDetailPojo {
    /** 故障描述 */
    private String faultDescribe;
    /** 处理方式 */
    private Integer dealType;
    /** 服务类型 */
    private Integer serviceType;
    /** 费用类型 */
    private Integer chargeType;
    /** 付费方式 */
    private Integer payType;
    /** 图片种类 */
    private String photoType;
    /** 图片Url */
    private String url;
    /** 是否调件 */
    private String transferParts;

}
