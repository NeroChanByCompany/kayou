package com.nut.servicestation.app.pojo;

import lombok.Data;

@Data
public class WoProcessPojo {

    /** 工单操作状态 */
    private Integer operateCode;
    /** 操作唯一标识 */
    private String operateId;
    /** 服务记录显示标题 */
    private String title;
    /** 服务记录显示内容 */
    private String textJson;
    /** 创建时间 */
    private String createTime;

    /** 地址 */
    private String url;
    /** TBoss服务记录显示内容（目前只接车有特殊显示） */
    private String textJsonTBoss;

    /**
     * 外出救援次数
     */
    private Integer timesRescueNumber;

}
