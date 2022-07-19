package com.nut.servicestation.app.dto;

import lombok.Data;

@Data
public class WoProcessDto {

    /** 工单操作状态 */
    private Integer operateCode;

    /** 服务流程标题 */
    private String title;

    /** 服务流程具体内容 */
    private String detailJson;

    /**
     * 外出救援次数
     */
    private Integer timesRescueNumber;

}
