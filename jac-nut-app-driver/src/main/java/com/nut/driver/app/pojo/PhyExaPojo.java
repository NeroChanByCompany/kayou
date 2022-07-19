package com.nut.driver.app.pojo;

import lombok.Data;

/**
 * @author liuBing
 * @Classname PhyExaPojo
 * @Description TODO
 * @Date 2021/6/23 19:18
 */
@Data
public class PhyExaPojo {
    /**
     * 车辆id
     */
    private String carId;
    /**
     * 车牌号
     */
    private String carNumber;
    /**
     * 终端号
     */
    private String terminalId;
    /**
     * 车架号
     */
    private String carVin;
    /**
     * 结构号
     */
    private String code;
    /**
     * 保养类别
     */
    private Integer maintainType;
    /**
     * 保养内容
     */
    private String maintainContent;
    /**
     * 首保里程(单位：KM)
     */
    private Long firstMileage;
    /**
     * 首保时间(月份间隔)
     */
    private Long firstMonth;
    /**
     * 定保里程(单位KM)
     */
    private Long routineMileage;
    /**
     * 定保时间(月份间隔)
     */
    private Long routineMonth;
    /**
     * 总成
     */
    private Integer sumType;
}
