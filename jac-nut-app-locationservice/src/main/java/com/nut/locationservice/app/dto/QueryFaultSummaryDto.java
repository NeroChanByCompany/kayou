package com.nut.locationservice.app.dto;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.app.locationService.dto
 * @Author: yzl
 * @CreateTime: 2021-06-17 09:23
 * @Version: 1.0
 */
@Data
@ToString
@Accessors(chain = true)
public class QueryFaultSummaryDto implements Serializable {

    private static final long serialVersionUID = 7083343868261360392L;
    // 车辆主键
    private Long id;
    // 底盘号
    private String chassisNum;
    // 车牌号
    private String plateNum;
    // 北斗一体机ID
    private String bdTerCode;
    // FK控制器ID
    private String fkTerCode;
    // 所属经销商
    private String tName;
    // 所属客户
    private String businessName;
    // 车辆型号
    private String carModel;
    // 发动机型号
    private String engineNumber;
    // 发动机类型
    private String engineType;
    // 车架号
    private String structureNum;
    // 故障类型
    private String breakdownDis;
    // SPN
    private String spn;
    // FMI
    private String fmi;
    // 故障发生时间
    private String occurDate;
    // 持续时长
    private String duration;
    // 起始位置
    private String bLoction;
    // 结束位置
    private String eLoction;
    //系统源
    private Integer systemSource;
    //系统类别
    private String systemType;
    //名称及型号
    private String typeModel;
    //AAK 销售状态
    private String aakstatusValue;
    //客户联系方式
    private String linkTelpone;
    //解决办法
    private String faultSolutions;
    //标识码
    private String symbolCode;

}
