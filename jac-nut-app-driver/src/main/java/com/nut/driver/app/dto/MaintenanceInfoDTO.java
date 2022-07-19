package com.nut.driver.app.dto;

import com.nut.driver.app.entity.MaintenanceItemInfoEntity;
import lombok.Data;

import java.util.List;

/**
 * @author liuBing
 * @Classname MaintenanceInfoDTO
 * @Description TODO
 * @Date 2021/6/24 15:15
 */
@Data
public class MaintenanceInfoDTO {

    /**
     * 保养记录id
     */
    private String maintainId;

    /**
     * 保养名称
     */
    private String maintainName;

    /**
     * 提醒方式
     */
    private String maintainType;

    /**
     * 提醒内容
     */
    private String maintainDescribe;

    /**
     * 保养项目总数
     */
    private String maintainItemCount;

    /**
     * 车辆id
     */
    private String carId;

    /**
     * 车牌号
     */
    private String carNumber;

    /**
     * 行驶总里程
     */
    private String mileage;

    /**
     * 备注
     */
    private String remarks;


    /**
     * 状态
     */
    private String maintainStatus;

    /**
     * 底盘号（VIN后8位）
     */
    private String vin;

    private List<MaintenanceItemInfoEntity> maintainItemList;



}
