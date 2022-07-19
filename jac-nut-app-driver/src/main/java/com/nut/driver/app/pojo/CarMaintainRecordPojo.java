package com.nut.driver.app.pojo;

import lombok.Data;

/**
 * @author liuBing
 * @Classname CarMaintainRecordPojo
 * @Description TODO
 * @Date 2021/6/24 13:31
 */
@Data
public class CarMaintainRecordPojo {


    private long id;

    /**
     * 底盘号
     */
    private String carVin;

    /**
     * 车牌号
     */
    private String carNumber;

    /**
     * 保养项目（以逗号拼接）
     */
    private String maintenanceProgramStr;

    /**
     *保养时间
     */
    private String maintainMileageTime;

    /**
     *保养里程
     */
    private String maintainMileageMileage;

    /**
     * 保养更新来源：客户APP：1；服务APP：2；crm同步：3
     */
    private Integer maintainSource;

    /**
     *更新人
     */
    private String opUser;

    /**
     *更新时间
     */
    private String opDate;
}
