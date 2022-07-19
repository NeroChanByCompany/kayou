package com.nut.servicestation.app.dto;

import lombok.Data;

/**
 * @author: jiangcm
 * @Description: 	维修项列表
 * @Date: Created in 2018/5/7 18:31
 * @Modified By:
 */
@Data
public class RepairRecordsDto {

    private String operateId;// 维修ID

    private Integer dealType;// 处理方式

    /** 服务类型 */
    private String serviceType;

    /** 费用类型 */
    private String chargeType;

    /** 付费方式 */
    private String payType;

    /**
     * 服务类型或费用类型
     * 对应前端展示的一级选项（服务类型和费用类型糅合到一个字段）
     */
    private String type1;

    /**
     * 处理方式或付费方式
     * 对应前端展示的二级选项（处理方式和付费方式糅合到一个字段）
     */
    private String type2;

    /** 故障描述 */
    private String description;

    /** 是否调件 */
    private String transferParts;

    /** 完成状态 */
    private Integer finishedStatus;

    /** 保修保内分次提交标识（0:否，1：是），非保修保内默认0 */
    private Integer subTwiceMark;

}
