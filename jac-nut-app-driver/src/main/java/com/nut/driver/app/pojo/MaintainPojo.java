package com.nut.driver.app.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author liuBing
 * @Classname MaintainPojo
 * @Description TODO
 * @Date 2021/6/23 17:20
 */
@Data
@Accessors(chain = true)
public class MaintainPojo {
    /**
     * 车牌号
     */
    private String carNumber;
    /**
     * 底盘号
     */
    private String vin;
    /**
     * 下次保养剩余里程
     */
    private Double remainingMileage;
    /**
     * 下次保养总数
     */
    private String nextCount;
    /**
     * 下次保养时间
     */
    private String nextDate;
    /**
     * 是否未保养（1：需要保养）
     */
    private Integer maintainType;

}
