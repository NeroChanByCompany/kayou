package com.nut.driver.app.pojo;

import lombok.Data;

/**
 * @author liuBing
 * @Classname ValidateMaintainInfoPojo
 * @Description TODO
 * @Date 2021/6/24 16:45
 */
@Data
public class ValidateMaintainInfoPojo {
    /**
     * 提醒方式 1：里程；2：时间
     */
    private String maintainType;

    /**
     * 提醒内容
     */
    private String maintainDescribe;

    /**
     * 车辆id
     */
    private String carId;

    /**
     * 车牌号
     */
    private String carNumber;
}
