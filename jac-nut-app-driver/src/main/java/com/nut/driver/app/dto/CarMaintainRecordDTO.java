package com.nut.driver.app.dto;

import lombok.Data;

import java.util.List;

/**
 * @author liuBing
 * @Classname CarMaintainRecordDTO
 * @Description TODO
 * @Date 2021/6/24 13:29
 */
@Data
public class CarMaintainRecordDTO {
    /**
     * 保养项目（以逗号拼接）
     */
    private List<String> maintenanceProgramList;

    /**
     * 保养时间
     */
    private String maintainMileageTime;

    /**
     * 保养里程
     */
    private String maintainMileageMileage;

    /**
     * 更新人
     */
    private String opUser;

    /**
     * 更新时间
     */
    private String opDate;

}
