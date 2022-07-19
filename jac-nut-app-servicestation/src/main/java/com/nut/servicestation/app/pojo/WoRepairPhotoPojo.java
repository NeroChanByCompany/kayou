package com.nut.servicestation.app.pojo;

import lombok.Data;

@Data
public class WoRepairPhotoPojo {
    private Long id;
    private String operateId;
    private Integer dealType;
    private Integer serviceType;
    private Integer chargeType;
    private Integer payType;
    private Integer photoNum;
    private Integer finishedStatus;
    private String type;
    private Long timestamp;

}
