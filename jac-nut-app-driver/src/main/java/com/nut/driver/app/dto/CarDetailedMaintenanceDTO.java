package com.nut.driver.app.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author liuBing
 * @Classname CarDetailedMaintenanceDTO
 * @Description TODO 车辆保养详细数据
 * @Date 2021/8/25 19:02
 */
@Data
@Accessors(chain = true)
public class CarDetailedMaintenanceDTO {
    /**
     * 车辆基础信息
     */
    private CarNewMaintainDTO carNewMaintainDTO;
    /**
     * 车辆数据集合
     */
    private MaintainDetailsDTO maintainDetailsDTO;
}
